using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Identity;

namespace OnlineBank.UserService.DAL.Entities;

public class AppUser : IdentityUser
{
    public string Passport { get; set; }
    
    public bool Ban { get; set; }
    
    [Range(0, 100, ErrorMessage = "Value for LoanRating must be in range 0 to 100")]
    public double LoanRating { get; set; }
}