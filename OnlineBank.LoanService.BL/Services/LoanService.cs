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

    public LoanService(LoanServiceDbContext context, IMapper mapper, IOptions<IntegrationApisUrls> options)
    {
        _context = context;
        _mapper = mapper;
        _integrationApisUrls = options.Value;
    }

    public async Task TakeOutLoan(CreateLoanDto createLoanDto)
    {
        if (createLoanDto.loanAmount <= 0)
        {
            throw new BadRequestException("Loan amount must be greater, than 0");
        }

        await IsUserExists(createLoanDto.userId);
        await IsBankAccountExists(createLoanDto.bankAccountId);
        
        var loanRateEntity = await _context
            .LoanRates
            .FirstOrDefaultAsync(lr => lr.Id == createLoanDto.loanRateId)
                ?? throw new CantFindByIdException("loan rate", createLoanDto.loanRateId);
        
        await MakeTransaction(createLoanDto.bankAccountId, createLoanDto.userId, createLoanDto.loanAmount, "TAKE_LOAN");
        
        var loanEntity = new LoanEntity
        {   
            StartDate = DateTime.UtcNow,
            EndDate = DateTime.UtcNow.AddMonths(createLoanDto.months),
            MonthlyPayment = createLoanDto.loanAmount / createLoanDto.months,
            Debt = createLoanDto.loanAmount,
            LoanRate = loanRateEntity,
            UserId = createLoanDto.userId,
            BankAccountId = createLoanDto.bankAccountId
        };
        
        await _context.Loans.AddAsync(loanEntity);
        await _context.SaveChangesAsync();
    }

    public async Task MakeLoanPayment(Guid loanId, PaymentDto paymentDto)
    {
        var loan = await _context.Loans
                       .Include(l => l.LoanRate)
                       .FirstOrDefaultAsync(l => l.Id == loanId)
                   ?? throw new CantFindByIdException("loan", loanId);
        if (loan.UserId != paymentDto.userId) throw new ConflictException("This is not your bank account"); 
        if (loan.Debt <= 0) throw new ConflictException("The loan has already closed");
       
        await IsUserExists(paymentDto.userId);
        await IsBankAccountExists(paymentDto.bankAccountId);
        
        var payment = paymentDto.paymentAmount ?? loan.MonthlyPayment;
        if (payment > loan.Debt)
        {
            payment = loan.Debt;
        }
        var percents = CountCurrentPercents(loan);
        var paymentWithPercents = (double)payment + percents;

        await MakeTransaction(paymentDto.bankAccountId, paymentDto.userId, (decimal)paymentWithPercents, "REPAY_LOAN");

        loan.Debt -= payment;

        await _context.SaveChangesAsync();
    }

    public async Task<List<LoanDto>> GetUserLoans(Guid userId)
    {
        await IsUserExists(userId);
        
        var loans = await _context
            .Loans
            .Include(l => l.LoanRate)
            .Where(l => l.UserId == userId)
            .ToListAsync();

        var loansDtos = _mapper.Map<List<LoanDto>>(loans);

        foreach (var loanDto in loansDtos)
        {
            var loanEntity = loans.FirstOrDefault(l => l.Id == loanDto.id);
            loanDto.interestRate = loanEntity.LoanRate.InterestRate;
            loanDto.loanRateName = loanEntity.LoanRate.Name;
        }

        return loansDtos;
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

    private async Task MakeTransaction(Guid baId, Guid userId, decimal amount, string transactionType)
    {
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.CoreServiceUrl + "/api/bank-accounts/" + baId;
            if (transactionType == "TAKE_LOAN" || transactionType == "DEPOSIT")
            {
                url += "/deposit";
            }
            else
            {
                url += "/withdraw";
            }
            var body = new CreateTransactionDto
            {   
                amount = amount,
                transactionType = transactionType,
                userId = userId
            };
            var strBody = JsonSerializer.Serialize(body);
            var content = new StringContent(strBody, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(url, content);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(await response.Content.ReadAsStringAsync());
            }
        }
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
}