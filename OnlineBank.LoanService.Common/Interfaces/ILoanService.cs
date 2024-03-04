using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;

namespace OnlineBank.LoanService.Common.Interfaces;

public interface ILoanService
{
    Task TakeOutLoan(CreateLoanDto createLoanDto);
}