using Microsoft.EntityFrameworkCore;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.DAL;

public class LoanServiceDbContext : DbContext
{
    public DbSet<LoanEntity> Loans { get; set; }
    
    public DbSet<LoanRateEntity> LoanRates { get; set; }
    
    public LoanServiceDbContext(DbContextOptions<LoanServiceDbContext> options) : base(options){}
    
}