using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.UserService.Common.Dtos;
using OnlineBank.UserService.Common.Dtos.User;

namespace OnlineBank.UserService.Common.Interfaces;

public interface IUserService
{
    Task<List<UserDto>> GetUsers();
    Task CreateUser(CreateUserDto createUserDto);
    Task BanUser(Guid userId);
    Task UnbanUser(Guid userId);
    Task<LoginResponseDto> Login(LoginCredentialsDto loginCredentialsDto);
    Task<UserInfoDto> GetUserInfo(Guid userId);
    Task<bool> CheckIfUserExists(Guid userId);
    Task EditUserLoanRating(UpdateRatingDto amount, Guid userId);
    Task<double> GetUserLoanRating(Guid userId);
}