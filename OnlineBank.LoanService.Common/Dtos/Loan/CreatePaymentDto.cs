using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class CreatePaymentDto
{
    public decimal? paymentAmount { get; set; }
    
    public Guid paymentId { get; set; }
    
    [Required]
    public Guid bankAccountId { get; set; }
}