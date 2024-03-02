using AutoMapper;
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
    private readonly RoleManager<IdentityRole> _roleManager;

    public UserService(UserManager<AppUser> userManager, IMapper mapper, RoleManager<IdentityRole> roleManager)
    {
        _userManager = userManager;
        _mapper = mapper;
        _roleManager = roleManager;
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
}