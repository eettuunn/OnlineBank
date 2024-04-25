namespace OnlineBank.MonitoringService.Models.Dtos;

public class RequestDto
{
    public string Url { get; set; }
    public string Method { get; set; }
    public string Protocol { get; set; }
    public int Status { get; set; }
    public double SpentTimeInMs { get; set; }
    public string ApiName { get; set; }
}