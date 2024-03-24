using System.ComponentModel.DataAnnotations;

namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class CreateLoanDto
{
    [Required]
    [Range(1, int.MaxValue, ErrorMessage = "Months amount must be greater, then 0")]
    public int months { get; set; }
    
    [Required]
    public decimal loanAmount { get; set; }
    
    [Required]
    public string currencyCode { get; set; }
        
    [Required]
    public Guid loanRateId { get; set; }
    
    [Required]
    public Guid bankAccountId { get; set; }
}