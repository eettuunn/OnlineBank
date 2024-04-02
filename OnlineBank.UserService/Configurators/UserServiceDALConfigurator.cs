using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using OnlineBank.UserService.DAL;
using OnlineBank.UserService.DAL.Entities;

namespace OnlineBank.UserService.Configurators;

public static class UserServiceDALConfigurator
{
    public static void ConfigureUserServiceDAL(this WebApplicationBuilder builder)
    {
        var connection = builder.Configuration.GetConnectionString("PostgresUserService");
        builder.Services.AddDbContext<UserServiceDbContext>(options => options.UseNpgsql(connection));
    }

    public static void ConfigureUserServiceDAL(this WebApplication app)
    {
        using (var scope = app.Services.CreateScope())
        {
            var dbContext = scope.ServiceProvider.GetService<UserServiceDbContext>();
            dbContext?.Database.Migrate();

            var _roleManager = scope.ServiceProvider.GetRequiredService<RoleManager<IdentityRole>>();
            var _usermanager = scope.ServiceProvider.GetRequiredService<UserManager<AppUser>>();
            try
            {
                if (dbContext.Database.GetPendingMigrations().Any())
                {
                    dbContext.Database.Migrate();
                }
            }
            catch (Exception e)
            {
                throw new Exception($"Can't initialize DB. Message: {e.Message}");
            }
            
            if (_roleManager.RoleExistsAsync("Customer").GetAwaiter().GetResult()) return;
            
            _roleManager.CreateAsync(new IdentityRole("Customer")).GetAwaiter().GetResult();
            _roleManager.CreateAsync(new IdentityRole("Staff")).GetAwaiter().GetResult();

            var customer = new AppUser
            {
                UserName = "Customer",
                Email = "customer@gmail.com",
                Passport = "1234 123456",
                PhoneNumber = "89999999999",
                LoanRating = 100
            };
            _usermanager.CreateAsync(customer, "a12345").GetAwaiter().GetResult();
            
            var staff = new AppUser
            {
                UserName = "Staff",
                Email = "staff@gmail.com",
                Passport = "4321 654321",
                PhoneNumber = "81111111111",
                LoanRating = 100
            };
            _usermanager.CreateAsync(staff, "a12345").GetAwaiter().GetResult();

            var admin = new AppUser
            {
                Id = "7e58c0d2-7738-4f78-a1bb-9f8c7d7ce0f4",
                UserName = "admin",
                Email = "admin@gmail.com",
                Passport = "1111 111111",
                PhoneNumber = "89999999999",
                LoanRating = 100
            };
            _usermanager.CreateAsync(admin, "a12345").GetAwaiter().GetResult();

            if (dbContext.Users.AnyAsync(u => u.UserName == customer.UserName).GetAwaiter().GetResult())
            {
                var customerUser = dbContext.Users.FirstOrDefaultAsync(u => u.Email == customer.Email).GetAwaiter().GetResult();
                var staffUser = dbContext.Users.FirstOrDefaultAsync(u => u.Email == staff.Email).GetAwaiter().GetResult();
                var adminUser = dbContext.Users.FirstOrDefaultAsync(u => u.Email == admin.Email).GetAwaiter().GetResult();
                
                _usermanager.AddToRoleAsync(staffUser, "Staff").GetAwaiter().GetResult();
                _usermanager.AddToRoleAsync(adminUser, "Staff").GetAwaiter().GetResult();
                _usermanager.AddToRoleAsync(adminUser, "Customer").GetAwaiter().GetResult();
                _usermanager.AddToRoleAsync(customerUser, "Customer").GetAwaiter().GetResult();
            }
            dbContext.SaveChanges();
        }
    }
}