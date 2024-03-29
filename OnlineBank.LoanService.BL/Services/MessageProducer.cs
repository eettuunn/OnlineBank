using System.Text;
using Newtonsoft.Json;
using OnlineBank.LoanService.Common.Dtos.Integrations;
using OnlineBank.LoanService.Common.Interfaces;
using RabbitMQ.Client;
using JsonSerializer = System.Text.Json.JsonSerializer;

namespace OnlineBank.LoanService.BL.Services;

public class MessageProducer : IMessageProducer
{
    private readonly IConnection _connection;

    public MessageProducer(IConnection connection)
    {
        _connection = connection;
    }

    public void SendMessage<T>(T message)
    {
        using var channel = _connection.CreateModel();

        channel.QueueDeclare("transactions", durable: true, exclusive: false);

        var jsonStr = JsonSerializer.Serialize(message);
        var body = Encoding.UTF8.GetBytes(jsonStr);
        
        channel.BasicPublish("transactionsExchange", "transactions", body: body);
    }
}