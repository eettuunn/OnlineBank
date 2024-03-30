namespace OnlineBank.LoanService.Common.Dtos.Integrations;

public class CreateTransactionMessage
{
    public decimal amount { get; set; }
    public string transactionType { get; set; }
    public string currencyCode { get; set; }
    public Guid bankAccountId { get; set; }
    public Guid? fromBankAccountId { get; set; }
    public Guid? toBankAccountId { get; set; }
    public Guid authenticatedUserId { get; set; }
}