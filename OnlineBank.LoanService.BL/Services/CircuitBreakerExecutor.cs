namespace OnlineBank.LoanService.BL.Services;

public class CircuitBreakerExecutor
{
    private const int ErrorsLimit = 30;
    private readonly TimeSpan _openToHalfOpenWaitTime = TimeSpan.FromSeconds(1);
    private int _errorsCount;
    private CircuitBreakerState _state = CircuitBreakerState.Closed;
    private Exception _lastException;
    private DateTime _lastStateChangeDateUtc;

    private void Reset()
    {
        _errorsCount = 0;
        _lastException = null;
        _state = CircuitBreakerState.Closed;
    }

    private bool IsClosed => _state == CircuitBreakerState.Closed;

    public async Task<T> ExecuteAction<T>(Func<Task<T>> func)
    {
        if (IsClosed)
        {
            try
            {
                var result = await func();
                Reset();
                return result;
            }
            catch (Exception e)
            {
                TrackException(e);
                throw;
            }
        }
        else
        {
            if (_state == CircuitBreakerState.HalfOpen || IsTimeExpired())
            {
                Console.WriteLine("////////////////////");
                Console.WriteLine("HalfOpen");
                Console.WriteLine("////////////////////");
                _state = CircuitBreakerState.HalfOpen;
                try
                {
                    var result = await func();
                    Reset();
                    return result;
                }
                catch (Exception e)
                {
                    Reopen(e);
                    throw;
                }
            }

            throw _lastException;
        }
    }

    private void Reopen(Exception e)
    {
        Console.WriteLine("Reopen");
        _state = CircuitBreakerState.Open;
        _lastStateChangeDateUtc = DateTime.UtcNow;
        _errorsCount = 0;
        _lastException = e;
    }

    private bool IsTimeExpired()
    {
        return _lastStateChangeDateUtc + _openToHalfOpenWaitTime < DateTime.UtcNow;
    }
    
    private void TrackException(Exception e)
    {
        Console.WriteLine("TrackExeption");
        _errorsCount++;

        if (_errorsCount >= ErrorsLimit)
        {
            _lastException = e;
            _state = CircuitBreakerState.Open;
            _lastStateChangeDateUtc = DateTime.UtcNow;
        }
    }
}

public enum CircuitBreakerState
{
    Closed,
    Open,
    HalfOpen
}