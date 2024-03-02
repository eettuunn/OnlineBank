using OnlineBank.UserService.Common.Dtos.User;

namespace OnlineBank.UserService.Common.Interfaces;

public interface IUserService
{
    Task<List<UserDto>> GetUsers();
}