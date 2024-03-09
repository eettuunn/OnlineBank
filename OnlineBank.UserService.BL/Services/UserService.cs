using AutoMapper;
using Common.Exceptions;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using OnlineBank.UserService.Common.Dtos;
using OnlineBank.UserService.Common.Dtos.User;
using OnlineBank.UserService.Common.Interfaces;
using OnlineBank.UserService.DAL;
using OnlineBank.UserService.DAL.Entities;

namespace OnlineBank.UserService.BL.Services;

public class UserService : IUserService
{
    private readonly UserManager<AppUser> _userManager;
    private readonly IMapper _mapper;
    private readonly ITokenService _tokenService;

    public UserService(UserManager<AppUser> userManager, IMapper mapper, ITokenService tokenService)
    {
        _userManager = userManager;
        _mapper = mapper;
        _tokenService = tokenService;
    }

    public async Task<List<UserDto>> GetUsers()
    {
        var users = await _userManager.Users.ToListAsync();
        var usersDto = _mapper.Map<List<UserDto>>(users);

        foreach (var userDto in usersDto)
        {
            var user = users.Find(u => u.Id == userDto.id.ToString());
            var userRoles = await _userManager.GetRolesAsync(user);
            userDto.roles = userRoles.ToList();
        }

        return usersDto;
    }
    
    public async Task<UserInfoDto> GetUserInfo(Guid userId)
    {
        var userEntity = await _userManager.FindByIdAsync(userId.ToString())
                   ?? throw new CantFindByIdException("user", userId);
        var userInfoDto = _mapper.Map<UserInfoDto>(userEntity);
        var userRoles = await _userManager.GetRolesAsync(userEntity);
        userInfoDto.roles = userRoles.ToList();
        
        return userInfoDto;
    }

    public async Task<bool> CheckIfUserExists(Guid userId)
    {
        var user = await _userManager.FindByIdAsync(userId.ToString());

        return user != null;
    }

    public async Task CreateUser(CreateUserDto createUserDto)
    {
        var newUser = _mapper.Map<AppUser>(createUserDto);
        var result = await _userManager.CreateAsync(newUser);

        var user = await _userManager.FindByEmailAsync(createUserDto.email);
        foreach (var role in createUserDto.roles)
        {
            await _userManager.AddToRoleAsync(user, role.ToString());
        }
    }

    public async Task BanUser(Guid userId)
    {
        var user = await _userManager.FindByIdAsync(userId.ToString()) 
                   ?? throw new CantFindByIdException("User", userId);
        user.Ban = true;
        await _userManager.UpdateAsync(user);
    }

    public async Task UnbanUser(Guid userId)
    {
        var user = await _userManager.FindByIdAsync(userId.ToString())
                    ?? throw new CantFindByIdException("User", userId);
        user.Ban = false;
        await _userManager.UpdateAsync(user);
    }

    public async Task<LoginResponseDto> Login(LoginCredentialsDto loginCredentialsDto)
    {
        var user = await _userManager.FindByEmailAsync(loginCredentialsDto.email)
                   ?? throw new NotFoundException($"Can't find user with email {loginCredentialsDto.email}");
        var token = await _tokenService.CreateToken(Guid.Parse(user.Id));

        var response = new LoginResponseDto
        {
            id = Guid.Parse(user.Id),
            token = token
        };

        return response;
    }
}