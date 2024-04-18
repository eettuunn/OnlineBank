namespace OnlineBank.LoanService.Common.Dtos.Loan;

public class RequestTracingDto
{
    public string Url { get; set; }
    public string Method { get; set; }
    public string Protocol { get; set; }
    public int Status { get; set; }
    public double SpentTimeInMs { get; set; }
}