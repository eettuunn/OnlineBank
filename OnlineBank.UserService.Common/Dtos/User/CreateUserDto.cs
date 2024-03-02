using System.ComponentModel.DataAnnotations;
using OnlineBank.UserService.Common.Enums;

namespace OnlineBank.UserService.Common.Dtos;

public class CreateUserDto
{
    [Required(ErrorMessage = "email is required")]
    [EmailAddress]
    [RegularExpression(@"[a-zA-Z]+\w*@[a-zA-Z]+\.[a-zA-Z]+", ErrorMessage = "Invalid email address")]
    public string email { get; set; }
    
    public string? phoneNumber { get; set; }
    
    [Required]
    public string passport { get; set; }
    
    [Required]
    public string userName { get; set; }
    
    [Required]
    public List<UserRole> roles { get; set; }
}