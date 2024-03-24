using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using OnlineBank.LoanService.DAL;

namespace OnlineBank.LoanService.BL.Services.Background;

public class LoanPaymentChecker : BackgroundService
{
    private readonly IServiceScopeFactory _serviceScopeFactory;

    public LoanPaymentChecker(IServiceScopeFactory serviceScopeFactory)
    {
        _serviceScopeFactory = serviceScopeFactory;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        while (!stoppingToken.IsCancellationRequested)
        {
            using (var scope = _serviceScopeFactory.CreateScope())
            {
                var context = scope.ServiceProvider.GetRequiredService<LoanServiceDbContext>();

                var loans = await context.Loans
                    .Include(l => l.Payments)
                    .ToListAsync(cancellationToken: stoppingToken);
                foreach (var loan in loans)
                {
                    var totalPenalties = 0;
                    foreach (var payment in loan.Payments)
                    {
                        if (payment.Debt > 0 && payment.PaymentDate < DateTime.UtcNow)
                        {
                            loan.Debt += 100;
                            totalPenalties += 100;
                        }
                    }

                    var loanDays = (loan.EndDate - loan.StartDate).TotalDays;
                    var newMonthlyPayment = totalPenalties / loanDays;
                    loan.MonthlyPayment += (decimal)newMonthlyPayment;
                    var monthlyPenalties = totalPenalties / loanDays;
                    foreach (var payment in loan.Payments)
                    {
                        if (payment.Debt != 0)
                        {
                            payment.Debt += (decimal)monthlyPenalties;
                        }
                    }
                    
                    if (loan.Payments.FirstOrDefault(p => p.PaymentDate < DateTime.UtcNow && !p.IsExpired && p.Debt > 0) != null)
                    {
                        loan.Payments = loan.Payments.OrderBy(l => l.PaymentDate).ToList();
                        var expiredPayment = loan.Payments.FirstOrDefault(p => !p.IsExpired);
                        expiredPayment.IsExpired = true;
                    }
                }
                

                await context.SaveChangesAsync(stoppingToken);

                await Task.Delay(1 * 1000 * 60 * 60 * 24, stoppingToken);
            }
        }
    }
}