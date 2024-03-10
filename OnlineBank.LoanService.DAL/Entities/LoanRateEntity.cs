using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.DAL.Entities;

public class LoanRateEntity
{
    [Required]
    public Guid Id { get; set; }
    
    [Required]
    public string Name { get; set; }
    
    [Required]
    public double InterestRate { get; set; }
}