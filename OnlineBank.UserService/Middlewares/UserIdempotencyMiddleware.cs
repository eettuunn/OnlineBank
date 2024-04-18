using Common.Exceptions;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using OnlineBank.UserService.DAL;
using OnlineBank.UserService.DAL.Entities;

namespace OnlineBank.UserService.Middlewares;

public class UserIdempotencyMiddleware
{
    private readonly RequestDelegate _next;

    public UserIdempotencyMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context, UserServiceDbContext dbContext)
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
                    throw new NotModifiedException("Not modified any resources, when executing User API request");
                }

                var newIdempotency = new UserIdempotencyEntity()
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