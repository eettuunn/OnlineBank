using System.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Extensions;
using OnlineBank.LoanService.Common.Dtos.Loan;

namespace Common.Middlewares;

public class RequestsTracingMiddleware
{
    private readonly RequestDelegate _next;

    public RequestsTracingMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task Invoke(HttpContext context)
    {
        var stopwatch = Stopwatch.StartNew();
        try
        {
            await _next(context);
        }
        finally
        {
            stopwatch.Stop();
            var requestTracing = new RequestTracingDto
            {
                Url = context.Request.GetDisplayUrl(),
                Method = context.Request.Method,
                Protocol = context.Request.Protocol,
                Status = context.Response.StatusCode,
                SpentTimeInMs = stopwatch.Elapsed.TotalMilliseconds
            };
        }
    }
}