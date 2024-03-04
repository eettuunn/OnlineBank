using Common.Exceptions;
using Microsoft.EntityFrameworkCore;
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
            LoanRate = loanRateEntity
        };

        await _context.Loans.AddAsync(loanEntity);
        await _context.SaveChangesAsync();
    }
}