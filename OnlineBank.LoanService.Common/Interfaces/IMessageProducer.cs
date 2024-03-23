namespace OnlineBank.LoanService.Common.Interfaces;

public interface IMessageProducer
{
    void SendMessage<T>(T message);
}