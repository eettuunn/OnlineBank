using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.DAL.Entities;

public class LoanPaymentEntity
{
    [Required]
    public Guid Id { get; set; }
    
    [Required]
    public DateTime PaymentDate { get; set; }
    
    [Required]
    public decimal Debt { get; set; }
    
    [Required]
    public bool IsExpired { get; set; }
}