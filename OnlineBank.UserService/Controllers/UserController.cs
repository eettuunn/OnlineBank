using Microsoft.AspNetCore.Mvc;
using OnlineBank.UserService.Common.Dtos;
using OnlineBank.UserService.Common.Dtos.User;
using OnlineBank.UserService.Common.Interfaces;

namespace OnlineBank.UserService.Controllers;

[Route("user_api/user")]
public class UserController : ControllerBase
{
    private readonly IUserService _userService;
    private readonly ITokenService _tokenService;

    public UserController(IUserService userService, ITokenService tokenService)
    {
        _userService = userService;
        _tokenService = tokenService;
    }

    [HttpGet]
    public async Task<List<UserDto>> GetUsers()
    {
        return await _userService.GetUsers();
    }

    [HttpPost]
    public async Task<IActionResult> CreateUser([FromBody] CreateUserDto createUserDto)
    {
        if (ModelState.IsValid)
        {
            await _userService.CreateUser(createUserDto);
            return Ok();
        }

        return BadRequest(ModelState);
    }

    [HttpPut]
    [Route("{userId}/ban")]
    public async Task BanUser(Guid userId)
    {
        await _userService.BanUser(userId);
    }
    
    [HttpPut]
    [Route("{userId}/unban")]
    public async Task UnbanUser(Guid userId)
    {
        await _userService.UnbanUser(userId);
    }

    [HttpGet]
    [Route("{userId}/token")]
    public async Task<string> GenerateUserToken(Guid userId)
    {
        return await _tokenService.CreateToken(userId);
    }
}