namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class LoanDto
{
    public Guid id { get; set; }
    
    public DateTime startDate { get; set; }
    
    public DateTime endDate { get; set; }
    
    public decimal debt { get; set; }
    
    public decimal monthlyPayment { get; set; }
    
    public Guid bankAccountId { get; set; }
    
    public double interestRate { get; set; }
    
    public string loanRateName { get; set; } 
}