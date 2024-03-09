using Common.Exceptions;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Interfaces;
using OnlineBank.LoanService.DAL;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.BL.Services;

public class LoanService : ILoanService
{
    private readonly LoanServiceDbContext _context;

    public LoanService(LoanServiceDbContext context)
    {
        _context = context;
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
        
        //todo: create transaction
    }



    private async Task IsBankAccountExists(Guid baId)
    {
        using (var client = new HttpClient())
        {
            var url = "http://localhost:8080/integration/bank-accounts/" + baId + "/check-existence";
            var response = await client.GetAsync(url);

            if (response.IsSuccessStatusCode)
            {
                var content = await response.Content.ReadAsStringAsync();
                var exists = Boolean.Parse(content);

                if (!exists) throw new CantFindByIdException("bank account", baId);

                return;
            }

            throw new Exception(response.StatusCode.ToString());
        }
    }
    
    private async Task IsUserExists(Guid userId)
    {
        using (var client = new HttpClient())
        {
            var url = "http://localhost:5259/user_api/user/" + userId + "/exist";
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
}