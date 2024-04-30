using Microsoft.EntityFrameworkCore;
using OnlineBank.MonitoringService.Models.Dtos;
using OnlineBank.MonitoringService.Models.Entities;

namespace OnlineBank.MonitoringService.Services;

public class RequestsTracingService
{
    private readonly MonitoringServiceDbContext _dbContext;

    public RequestsTracingService(MonitoringServiceDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task UploadRequest(RequestDto requestDto)
    {
        var request = new RequestEntity
        {
            Id = new Guid(),
            Url = requestDto.Url,
            Method = requestDto.Method,
            Protocol = requestDto.Protocol,
            Status = requestDto.Status,
            SpentTimeInMs = requestDto.SpentTimeInMs,
            ApiName = requestDto.ApiName
        };

        await _dbContext.Requests.AddAsync(request);
        await _dbContext.SaveChangesAsync();
    }

    public async Task<RequestsListDto> GetApiRequests(string apiName)
    {
        var requestsEntities = await _dbContext
            .Requests
            .Where(r => r.ApiName == apiName)
            .ToListAsync();

        var requests = new RequestsListDto();
        var errorsCounter = 0;
        foreach (var r in requestsEntities)
        {
            var requestDto = new RequestDto
            {
                Url = r.Url,
                Method = r.Method,
                Protocol = r.Protocol,
                Status = r.Status,
                SpentTimeInMs = r.SpentTimeInMs,
                ApiName = r.ApiName
            };
            requests.Requests.Add(requestDto);
            
            if (r.Status != 200) errorsCounter++;
        }

        if (errorsCounter != 0)
        {
            requests.ErrorsPercent = errorsCounter / (double)requests.Requests.Count * 100;
        }
        else
        {
            requests.ErrorsPercent = 0;
        }

        return requests;
    }
}