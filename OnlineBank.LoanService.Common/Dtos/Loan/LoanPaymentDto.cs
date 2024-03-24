namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class LoanPaymentDto
{
    public decimal debt { get; set; }
    public DateTime paymentDate { get; set; }
    public bool isExpired { get; set; }
}