using Common.Exceptions;
using Microsoft.AspNetCore.Http;

namespace OnlineBank.Common.Middlewares.ExceptionHandler;

public class RandomErrorMiddleware
{
    private readonly RequestDelegate _next;

    public RandomErrorMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context)
    {
        if (DateTime.UtcNow.Minute % 2 == 0)
        {
            var rand = new Random();
            var randNum = rand.Next(1, 11);
            if (randNum != 10)
            {
                throw new InternalServerErrorException("Random server error");
            }
        }
        else
        {
            var rand = new Random();
            var randNum = rand.Next(1, 3);
            if (randNum == 1)
            {
                throw new InternalServerErrorException("Random server error");
            }
        }
        await _next(context);
    }
}