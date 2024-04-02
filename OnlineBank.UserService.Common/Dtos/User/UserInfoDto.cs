namespace OnlineBank.UserService.Common.Dtos.User;

public class UserInfoDto
{
    public Guid id { get; set; }
    public string email { get; set; }
    public string userName { get; set; }
    public List<string> roles { get; set; }
    public bool ban { get; set; }
    public string phoneNumber { get; set; }
    public string passport { get; set; }
    public double loanRating { get; set; }
}