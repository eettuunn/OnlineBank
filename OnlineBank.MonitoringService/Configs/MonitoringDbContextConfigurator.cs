using Microsoft.EntityFrameworkCore;

namespace OnlineBank.MonitoringService.Configs;

public static class MonitoringDbContextConfigurator
{
    public static void ConfigureMonitoringServiceDb(this WebApplicationBuilder builder)
    {
        var connection = builder.Configuration.GetConnectionString("PostgresMonitoringService");
        builder.Services.AddDbContext<MonitoringServiceDbContext>(options => options.UseNpgsql(connection));
    }

    public static void ConfigureMonitoringServiceDb(this WebApplication app)
    {
        using (var scope = app.Services.CreateScope())
        {
            var dbContext = scope.ServiceProvider.GetService<MonitoringServiceDbContext>();
            dbContext?.Database.Migrate();
        }
    }
}