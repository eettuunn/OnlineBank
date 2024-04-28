namespace OnlineBank.MonitoringService.Models.Dtos;

public class RequestsListDto
{
    public double ErrorsPercent { get; set; }
    public List<RequestDto> Requests { get; set; } = new();
}