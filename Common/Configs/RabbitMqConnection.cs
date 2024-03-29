namespace Common.Configs;

public class RabbitMqConnection
{
    public required string Hostname { get; set; }
    
    public required string Username { get; set; }
    
    public required string Password { get; set; }

    public required string VirtualHost { get; set; }
    
    public required string Port { get; set; }
}