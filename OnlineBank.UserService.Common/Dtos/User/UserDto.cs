namespace OnlineBank.UserService.Common.Dtos.User;

public class UserDto
{
    public Guid id { get; set; }
    public string email { get; set; }
    public string userName { get; set; }
    public List<string> roles { get; set; }
    public bool ban { get; set; }
}