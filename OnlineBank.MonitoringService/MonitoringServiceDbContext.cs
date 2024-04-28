using Microsoft.EntityFrameworkCore;
using OnlineBank.MonitoringService.Models.Entities;

namespace OnlineBank.MonitoringService;

public class MonitoringServiceDbContext : DbContext
{
    public DbSet<RequestEntity> Requests { get; set; }
    
    public MonitoringServiceDbContext(DbContextOptions<MonitoringServiceDbContext> options) : base(options){}
}