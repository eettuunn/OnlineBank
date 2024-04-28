namespace OnlineBank.UserService.DAL.Entities;

public class UserIdempotencyEntity
{
    public Guid Id { get; set; }
    public string Key { get; set; }
}