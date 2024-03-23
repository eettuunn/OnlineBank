using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;

namespace OnlineBank.LoanService.Common.Interfaces;

public interface ILoanService
{
    Task TakeOutLoan(CreateLoanDto createLoanDto, Guid userId);
    Task MakeLoanPayment(Guid loanId, PaymentDto paymentDto, Guid userId);
    Task<List<LoanDto>> GetUserLoans(Guid userId);
}