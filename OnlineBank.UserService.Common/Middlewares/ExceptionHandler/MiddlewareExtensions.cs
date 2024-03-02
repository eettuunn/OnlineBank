using Common.Middlewares.ExceptionHandler;
using Microsoft.AspNetCore.Builder;

namespace OnlineBank.Common.Middlewares.ExceptionHandler;

public static class MiddlewareExtensions
{
    public static void UseExceptionMiddleware(this WebApplication app)
    {
        app.UseMiddleware<ExceptionMiddlewareService>();
    }
}