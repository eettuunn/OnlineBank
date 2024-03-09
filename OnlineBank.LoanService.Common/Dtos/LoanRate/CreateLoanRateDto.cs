using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.Common.Dtos.LoanRate;

public class CreateLoanRateDto
{
    [Required]
    public string name { get; set; }
    
    [Required]
    [Range(0.0, Double.MaxValue, ErrorMessage = "Interest rate must be greater, then 0")]
    public double interestRate { get; set; }
}