using Microsoft.AspNetCore.Mvc;
using OnlineBank.MonitoringService.Models.Dtos;
using OnlineBank.MonitoringService.Services;

namespace OnlineBank.MonitoringService.Controllers;

[Route("monitoring_api")]
public class RequestsTracingController : ControllerBase
{
    private readonly RequestsTracingService _requestsTracingService;

    public RequestsTracingController(RequestsTracingService requestsTracingService)
    {
        _requestsTracingService = requestsTracingService;
    }

    [HttpPost]
    public async Task UploadRequest([FromBody] RequestDto requestDto)
    {
        await _requestsTracingService.UploadRequest(requestDto);
    }

    [HttpGet]
    public async Task<RequestsListDto> GetApiRequests([FromQuery] string apiName)
    {
        return await _requestsTracingService.GetApiRequests(apiName);
    }
}