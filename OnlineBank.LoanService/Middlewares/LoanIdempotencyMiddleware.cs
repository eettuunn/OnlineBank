using Common.Exceptions;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using OnlineBank.LoanService.DAL;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.Middlewares;

public class LoanIdempotencyMiddleware
{
    private readonly RequestDelegate _next;

    public LoanIdempotencyMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context, LoanServiceDbContext dbContext)
    {
        if (context.Request.Method == "POST")
        {
            var idempotencyHeader = context.Request.Headers["Idempotency-Key"];
            if (!idempotencyHeader.IsNullOrEmpty())
            {
                var idempotencyKey = idempotencyHeader[0];
                var keyInDb = await dbContext
                    .IdempotencyKeys
                    .FirstOrDefaultAsync(k => k.Key == idempotencyKey);
                if (keyInDb != null)
                {
                    throw new NotModifiedException("Not modified any resources, when executing Loans API request");
                }

                var newIdempotency = new LoanIdempotencyEntity
                {
                    Id = new Guid(),
                    Key = idempotencyKey
                };
                await dbContext.IdempotencyKeys.AddAsync(newIdempotency);
                await dbContext.SaveChangesAsync();
            }
        }

        await _next(context);
    }
}