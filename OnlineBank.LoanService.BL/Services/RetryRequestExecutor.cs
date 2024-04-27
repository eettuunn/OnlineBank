using System.Diagnostics;

namespace OnlineBank.LoanService.BL.Services;

public class RetryRequestExecutor
{
    private readonly CircuitBreakerExecutor _circuitBreakerExecutor;

    public RetryRequestExecutor(CircuitBreakerExecutor circuitBreakerExecutor)
    {
        _circuitBreakerExecutor = circuitBreakerExecutor;
    }

    public async Task<T> ExecuteFunc<T>(Func<Task<T>> func)
    {
        var currentRetry = 0;
        do
        {
            try
            {
                currentRetry++;
                var result = await func();
                Console.WriteLine("///////////////////////////////////////");
                Console.WriteLine("Operation complete");
                Console.WriteLine($"Attempts: {currentRetry}");
                Console.WriteLine("///////////////////////////////////////");
                return result;
            }
            catch (Exception e) when (currentRetry <= 2)
            {
                Console.WriteLine(e.Message);
            }
            catch (Exception e) when (currentRetry > 2)
            {
                try
                {
                    return await _circuitBreakerExecutor.ExecuteAction(func);
                }
                catch (Exception ce)
                {
                    Console.WriteLine(e.Message);
                }
            }

            Thread.Sleep(300);
        } while (true);
        
        
    }
}