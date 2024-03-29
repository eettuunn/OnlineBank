using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;

namespace OnlineBank.LoanService.Common.Interfaces;

public interface ILoanService
{
    Task TakeOutLoan(CreateLoanDto createLoanDto, Guid userId);
    Task MakeLoanPayment(Guid loanId, CreatePaymentDto createPaymentDto, Guid userId);
    Task<List<LoanListElementDto>> GetUserLoans(Guid userId);
    Task<LoanInfoDto> GetLoanInfo(Guid loanId, Guid userId);
}