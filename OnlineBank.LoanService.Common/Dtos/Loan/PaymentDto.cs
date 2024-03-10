using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class PaymentDto
{
    public decimal? paymentAmount { get; set; }
    
    [Required]
    public Guid bankAccountId { get; set; }
    
    [Required]
    public Guid userId { get; set; }
}