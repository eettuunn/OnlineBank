using Common.Exceptions;
using Microsoft.AspNetCore.Http;

namespace Common.Middlewares.ExceptionHandler;

public class ExceptionMiddlewareService
{
    private readonly RequestDelegate _next;

    public ExceptionMiddlewareService(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (BadRequestException exception)
        {
            context.Response.StatusCode = StatusCodes.Status400BadRequest;
            await context.Response.WriteAsJsonAsync(new {message = exception.Message});
        }
        catch (CantFindByIdException exception)
        {
            context.Response.StatusCode = StatusCodes.Status404NotFound;
            await context.Response.WriteAsJsonAsync(new { message = exception.Message });
        }
        catch (NotFoundException exception)
        {
            context.Response.StatusCode = StatusCodes.Status404NotFound;
            await context.Response.WriteAsJsonAsync(new { message = exception.Message });
        }
        catch (ConflictException exception)
        {
            context.Response.StatusCode = StatusCodes.Status409Conflict;
            await context.Response.WriteAsJsonAsync(new { message = exception.Message });
        }
        catch (InternalServerErrorException exception)
        {
            context.Response.StatusCode = StatusCodes.Status500InternalServerError;
            await context.Response.WriteAsJsonAsync(new { message = exception.Message });
        }
        catch (NotModifiedException exception)
        {
            context.Response.StatusCode = StatusCodes.Status304NotModified;
            await context.Response.WriteAsJsonAsync(new { message = exception.Message });
        }
    }
}