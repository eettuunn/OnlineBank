using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.DAL.Entities;

public class LoanEntity
{
    [Key]
    [Required]
    public Guid Id { get; set; }
    
    [Required]
    public DateTime StartDate { get; set; }
    
    [Required]
    public DateTime EndDate { get; set; }
    
    [Required]
    public decimal MonthlyPayment { get; set; }
    
    [Required]
    public decimal Debt { get; set; }
    
    [Required]
    public string CurrencyCode { get; set; }
    
    [Required]
    public Guid UserId { get; set; }
    
    [Required]
    public Guid BankAccountId { get; set; }
    
    [Required]
    public LoanRateEntity LoanRate { get; set; }
}