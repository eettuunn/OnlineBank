using System.Text;
using System.Text.Json;
using AutoMapper;
using Common.Exceptions;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using OnlineBank.LoanService.Common.Dtos.Integrations;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Interfaces;
using Common;
using OnlineBank.LoanService.Configs;
using OnlineBank.LoanService.DAL;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.BL.Services;

public class LoanService : ILoanService
{
    private readonly LoanServiceDbContext _context;
    private readonly IMapper _mapper;
    private readonly IntegrationApisUrls _integrationApisUrls;
    private readonly IMessageProducer _messageProducer;
    private readonly ILoanRatingHelper _loanRatingHelper;

    public LoanService(LoanServiceDbContext context, IMapper mapper, IOptions<IntegrationApisUrls> options, IMessageProducer messageProducer, ILoanRatingHelper loanRatingHelper)
    {
        _context = context;
        _mapper = mapper;
        _messageProducer = messageProducer;
        _loanRatingHelper = loanRatingHelper;
        _integrationApisUrls = options.Value;
    }

    public async Task TakeOutLoan(CreateLoanDto createLoanDto, Guid userId)
    {
        if (createLoanDto.loanAmount <= 0)
        {
            throw new BadRequestException("Loan amount must be greater, than 0");
        }

        await IsUserExists(userId);
        await IsBankAccountExists(createLoanDto.bankAccountId);
        
        var loanRateEntity = await _context
            .LoanRates
            .FirstOrDefaultAsync(lr => lr.Id == createLoanDto.loanRateId)
                ?? throw new CantFindByIdException("loan rate", createLoanDto.loanRateId);
        
        MakeTransaction(createLoanDto.bankAccountId, createLoanDto.loanAmount, "TAKE_LOAN", createLoanDto.currencyCode);

        var payments = await CreatePayments(createLoanDto);
        var loanEntity = new LoanEntity
        {   
            StartDate = DateTime.UtcNow,
            EndDate = DateTime.UtcNow.AddDays(createLoanDto.days),
            MonthlyPayment = createLoanDto.loanAmount / createLoanDto.days,
            Debt = createLoanDto.loanAmount,
            LoanRate = loanRateEntity,
            UserId = userId,
            BankAccountId = createLoanDto.bankAccountId,
            CurrencyCode = createLoanDto.currencyCode,
            Payments = payments
        };
        
        await _context.Loans.AddAsync(loanEntity);
        await _context.SaveChangesAsync();
    }

    public async Task MakeLoanPayment(Guid loanId, CreatePaymentDto createPaymentDto, Guid userId)
    {
        var loan = await _context.Loans
                       .Include(l => l.LoanRate)
                       .Include(l => l.Payments)
                       .FirstOrDefaultAsync(l => l.Id == loanId)
                   ?? throw new CantFindByIdException("loan", loanId);
        if (loan.UserId != userId) throw new ConflictException("This is not your loan"); 
        if (loan.Debt <= 0) throw new ConflictException("The loan has already closed");
       
        await IsUserExists(userId);
        await IsBankAccountExists(createPaymentDto.bankAccountId);

        var loanPayment = loan.Payments.FirstOrDefault(p => p.Id == createPaymentDto.paymentId)
                          ?? throw new CantFindByIdException("payment", createPaymentDto.paymentId);
        if (loanPayment.PaymentDate >= DateTime.UtcNow)
        {
            await _loanRatingHelper.UpdateUserLoanRating(true, userId);
        }
        var payment = createPaymentDto.paymentAmount ?? loanPayment.Debt;
        if (payment > loanPayment.Debt)
        {
            payment = loan.Debt;
        }
        var percents = CountCurrentPercents(loan);
        var paymentWithPercents = (double)payment + percents;

        await CheckEnoughMoneyOnBankAcc(createPaymentDto.bankAccountId, paymentWithPercents, loan.CurrencyCode);

        MakeTransaction(createPaymentDto.bankAccountId, (decimal)paymentWithPercents, "REPAY_LOAN", loan.CurrencyCode);

        loanPayment.Debt -= payment;
        loan.Debt -= payment;

        await _context.SaveChangesAsync();
    }

    public async Task<List<LoanListElementDto>> GetUserLoans(Guid userId)
    {
        await IsUserExists(userId);
        
        var loans = await _context
            .Loans
            .Include(l => l.LoanRate)
            .Where(l => l.UserId == userId)
            .ToListAsync();

        var loansDtos = _mapper.Map<List<LoanListElementDto>>(loans);

        foreach (var loanDto in loansDtos)
        {
            var loanEntity = loans.FirstOrDefault(l => l.Id == loanDto.id);
            loanDto.interestRate = loanEntity.LoanRate.InterestRate;
            loanDto.loanRateName = loanEntity.LoanRate.Name;
        }

        return loansDtos;
    }

    public async Task<LoanInfoDto> GetLoanInfo(Guid loanId, Guid userId)
    {
        var loanEntity = await _context.Loans
            .Include(l => l.Payments)
            .FirstOrDefaultAsync(l => l.Id == loanId) ?? throw new CantFindByIdException("loan", loanId);
        if(loanEntity.UserId != userId) throw new ConflictException("This is not your loan");

        var loanDto = new LoanInfoDto
        {
            loanInfo = _mapper.Map<LoanListElementDto>(loanEntity),
            loanPayments = _mapper.Map<List<LoanPaymentDto>>(loanEntity.Payments)
        };
        loanDto.loanPayments = loanDto.loanPayments.OrderBy(p => p.paymentDate).ToList();
        
        return loanDto;
    }


    private async Task IsBankAccountExists(Guid baId)
    {
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.CoreServiceUrl + "/api/bank-accounts/" + baId + "/check-existence";
            var response = await client.GetAsync(url);

            if (response.IsSuccessStatusCode)
            {
                var content = await response.Content.ReadAsStringAsync();
                var exists = Boolean.Parse(content);

                if (!exists) throw new CantFindByIdException("bank account", baId);

                return;
            }

            throw new Exception(await response.Content.ReadAsStringAsync());
        }
    }
    
    private async Task IsUserExists(Guid userId)
    {
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.UserServiceUrl + "/user_api/user/" + userId + "/exist";
            var response = await client.GetAsync(url);

            if (response.IsSuccessStatusCode)
            {
                var content = await response.Content.ReadAsStringAsync();
                var exists = Boolean.Parse(content);
                
                if (!exists) throw new CantFindByIdException("userId", userId);
                
                return;
            }

            throw new Exception(response.StatusCode.ToString());
        }
    }

    private async Task CheckEnoughMoneyOnBankAcc(Guid baId, double amount, string currencyCode)
    {
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.CoreServiceUrl + "/api/bank-accounts/" + baId + "/check-money";
            var body = new CheckMoneyDto
            {   
                amount = amount,
                currencyCode = currencyCode
            };
            var strBody = JsonSerializer.Serialize(body);
            var content = new StringContent(strBody, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(url, content);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(await response.Content.ReadAsStringAsync());
            }
            
            var responseContent = await response.Content.ReadAsStringAsync();
            var enough = Boolean.Parse(responseContent);

            if (!enough) throw new ConflictException("Not enough money on bank account");
        }
    }

    private void MakeTransaction(Guid baId, decimal amount, string transactionType, string currencyCode)
    {
        var transactionMessage = new CreateTransactionMessage
        {   
            amount = amount,
            transactionType = transactionType,
            bankAccountId = baId,
            currencyCode = currencyCode
        };
        _messageProducer.SendMessage(transactionMessage);
    }

    private double CountCurrentPercents(LoanEntity loan)
    {
        double daysInYear = DateTime.IsLeapYear(DateTime.UtcNow.Year) ? 366 : 365;
        double debt = (double)loan.Debt;
        // double daysInMonth = DateTime.DaysInMonth(DateTime.UtcNow.Year, DateTime.UtcNow.Month);
        double daysInMonth = 1;
        double interestRate = loan.LoanRate.InterestRate / 100;
        
        var percents = debt * daysInMonth * interestRate / daysInYear;

        return percents;
    }

    private async Task<List<LoanPaymentEntity>> CreatePayments(CreateLoanDto createLoanDto)
    {
        var monthlyPayment = createLoanDto.loanAmount / createLoanDto.days;
        var currentDate = DateTime.UtcNow;
        var payments = new List<LoanPaymentEntity>();
        for (var i = 0; i < createLoanDto.days; i++)
        {
            var payment = new LoanPaymentEntity
            {
                PaymentDate = currentDate.AddDays(1),
                Debt = monthlyPayment,
                IsExpired = false
            };
            payments.Add(payment);
            await _context.LoanPayments.AddAsync(payment);

            currentDate = currentDate.AddDays(1);
        }

        await _context.SaveChangesAsync();

        return payments;
    }
}