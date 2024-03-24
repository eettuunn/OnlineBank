namespace OnlineBank.LoanService.Common.Dtos.Integrations;

public class CreateTransactionMessage
{
    public decimal amount { get; set; }
    public string transactionType { get; set; }
    public Guid userId { get; set; }
    public Guid bankAccountId { get; set; }
    public string currencyCode { get; set; }
}