namespace OnlineBank.UserService.Common.Dtos.User;

public class UserDto
{
    public Guid id { get; set; }
    public string email { get; set; }
    public string fullName { get; set; }
    public List<string> roles { get; set; }
}