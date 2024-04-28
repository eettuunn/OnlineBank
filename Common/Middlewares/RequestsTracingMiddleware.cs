using System.Diagnostics;
using System.Text;
using System.Text.Json;
using Common.Exceptions;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Http.Extensions;
using OnlineBank.LoanService.Common.Dtos.Loan;

namespace Common.Middlewares;

public class RequestsTracingMiddleware
{
    private readonly RequestDelegate _next;
    private readonly string _monitoringPostUrl;

    public RequestsTracingMiddleware(RequestDelegate next, string monitoringPostUrl)
    {
        _next = next;
        _monitoringPostUrl = monitoringPostUrl;
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
            var url = context.Request.GetDisplayUrl();
            var apiName = url.Contains("user") ? "UserService" : "LoanService";
            var requestTracing = new RequestTracingDto
            {
                Url = url,
                Method = context.Request.Method,
                Protocol = context.Request.Protocol,
                Status = context.Response.StatusCode,
                SpentTimeInMs = stopwatch.Elapsed.TotalMilliseconds,
                ApiName = apiName
            };

            await SendRequestTracingToApi(requestTracing);
        }
    }

    private async Task SendRequestTracingToApi(RequestTracingDto requestTracingDto)
    {
        using (var client = new HttpClient())
        {
            var strBody = JsonSerializer.Serialize(requestTracingDto);
            var content = new StringContent(strBody, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(_monitoringPostUrl, content);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(await response.Content.ReadAsStringAsync());
            }
        }
    }
}