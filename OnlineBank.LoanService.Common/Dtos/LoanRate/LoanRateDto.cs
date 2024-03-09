namespace OnlineBank.LoanService.Common.Dtos.LoanRate;

public class LoanRateDto
{
    public Guid id { get; set; }
    public string name { get; set; }
    public double interestRate { get; set; }
}