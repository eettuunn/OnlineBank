using OnlineBank.UserService.Common.Dtos;
using OnlineBank.UserService.Common.Dtos.User;

namespace OnlineBank.UserService.Common.Interfaces;

public interface IUserService
{
    Task<List<UserDto>> GetUsers();
    Task CreateUser(CreateUserDto createUserDto);
}