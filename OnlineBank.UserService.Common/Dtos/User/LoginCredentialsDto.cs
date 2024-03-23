using System.ComponentModel.DataAnnotations;

namespace OnlineBank.UserService.Common.Dtos.User;

public class LoginCredentialsDto
{
    [Required]
    public string email { get; set; }
    
    [Required]
    public string password { get; set; }
}