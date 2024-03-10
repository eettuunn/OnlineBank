namespace OnlineBank.LoanService.Common.Dtos.Integrations;

public class CreateTransactionDto
{
    public decimal amount { get; set; }
    public string transactionType { get; set; }
    public Guid userId { get; set; }
}