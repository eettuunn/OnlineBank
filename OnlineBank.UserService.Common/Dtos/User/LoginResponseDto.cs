namespace OnlineBank.UserService.Common.Dtos.User;

public class LoginResponseDto
{
    public Guid id { get; set; }
    
    public string token { get; set; }
}