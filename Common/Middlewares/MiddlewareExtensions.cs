using Common.Middlewares.ExceptionHandler;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;

namespace OnlineBank.Common.Middlewares.ExceptionHandler;

public static class MiddlewareExtensions
{
    public static void UseExceptionMiddleware(this WebApplication app)
    {
        app.UseMiddleware<ExceptionMiddlewareService>();
    }

    public static void UseRandomErrorMiddleware(this WebApplication app)
    {
        app.UseMiddleware<RandomErrorMiddleware>();
    }
}