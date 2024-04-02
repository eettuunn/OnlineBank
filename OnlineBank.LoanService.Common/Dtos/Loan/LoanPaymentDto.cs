namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class LoanPaymentDto
{
    public Guid id { get; set; }
    public decimal debt { get; set; }
    public DateTime paymentDate { get; set; }
    public bool isExpired { get; set; }
}