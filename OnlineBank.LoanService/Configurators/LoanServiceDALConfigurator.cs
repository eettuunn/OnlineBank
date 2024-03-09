using Microsoft.EntityFrameworkCore;
using OnlineBank.LoanService.DAL;

namespace OnlineBank.LoanService.Configurators;

public static class LoanServiceDALConfigurator
{
    public static void ConfigureLoanServiceDAL(this WebApplicationBuilder builder)
    {
        var connection = builder.Configuration.GetConnectionString("PostgresLoanService");
        builder.Services.AddDbContext<LoanServiceDbContext>(options => options.UseNpgsql(connection));
    }

    public static void ConfigureLoanServiceDAL(this WebApplication app)
    {
        using (var scope = app.Services.CreateScope())
        {
            var dbContext = scope.ServiceProvider.GetService<LoanServiceDbContext>();
            dbContext?.Database.Migrate();
        }
    }
}