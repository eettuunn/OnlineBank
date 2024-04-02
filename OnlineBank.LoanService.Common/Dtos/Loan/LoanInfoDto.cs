namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class LoanInfoDto
{
    public LoanListElementDto loanInfo { get; set; }
    
    public List<LoanPaymentDto> loanPayments { get; set; }
}