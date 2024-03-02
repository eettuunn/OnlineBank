using System.Security.Claims;

namespace OnlineBank.UserService.Common.Interfaces;

public interface ITokenService
{
    Task<string> CreateToken(Guid userId);
}