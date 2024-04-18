namespace OnlineBank.LoanService.DAL.Entities;

public class LoanIdempotencyEntity
{
    public Guid Id { get; set; }
    public string Key { get; set; }
}