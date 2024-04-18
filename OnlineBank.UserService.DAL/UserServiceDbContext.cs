using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using OnlineBank.UserService.DAL.Entities;

namespace OnlineBank.UserService.DAL;

public class UserServiceDbContext : IdentityDbContext<AppUser>
{
    public DbSet<UserIdempotencyEntity> IdempotencyKeys { get; set; }
    public UserServiceDbContext(DbContextOptions<UserServiceDbContext> options) : base(options){}
}