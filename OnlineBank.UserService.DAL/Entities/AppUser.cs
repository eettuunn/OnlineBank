using Microsoft.AspNetCore.Identity;

namespace OnlineBank.UserService.DAL.Entities;

public class AppUser : IdentityUser
{
    public string Passport { get; set; }
    
    public bool Ban { get; set; }
}