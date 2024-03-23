using Microsoft.AspNetCore.Authorization;
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

    /// <summary>
    /// Get list of all users
    /// </summary>
    [HttpGet]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task<List<UserDto>> GetUsers()
    {
        return await _userService.GetUsers();
    }

    /// <summary>
    /// Get user info by id
    /// </summary>
    [HttpGet]
    [Route("{userId}")]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task<UserInfoDto> GetUserInfo(Guid userId)
    {
        return await _userService.GetUserInfo(userId);
    }
    

    /// <summary>
    /// Create new user
    /// </summary>
    [HttpPost]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task<IActionResult> CreateUser([FromBody] CreateUserDto createUserDto)
    {
        if (ModelState.IsValid)
        {
            await _userService.CreateUser(createUserDto);
            return Ok();
        }

        return BadRequest(ModelState);
    }

    /// <summary>
    /// Ban user
    /// </summary>
    [HttpPut]
    [Route("{userId}/ban")]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task BanUser(Guid userId)
    {
        await _userService.BanUser(userId);
    }
    
    /// <summary>
    /// Unban user
    /// </summary>
    [HttpPut]
    [Route("{userId}/unban")]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task UnbanUser(Guid userId)
    {
        await _userService.UnbanUser(userId);
    }

    /// <summary>
    /// Generate user token by his Id
    /// </summary>
    [HttpGet]
    [Route("{userId}/token")]
    [Authorize]
    [Authorize(Roles = "Staff")]
    public async Task<string> GenerateUserToken(Guid userId)
    {
        return await _tokenService.CreateToken(userId);
    }

    /// <summary>
    /// Login with that returns Id and generated token
    /// </summary>
    [HttpPost]
    [Route("login")]
    public async Task<ActionResult<LoginResponseDto>> Login([FromBody] LoginCredentialsDto loginCredentialsDto)
    {
        if(ModelState.IsValid)
            return await _userService.Login(loginCredentialsDto);

        return BadRequest(ModelState);
    }
    
    /// <summary>
    /// Check if user exists
    /// </summary>
    [HttpGet]
    [Route("{userId}/exist")]
    public async Task<bool> CheckUser(Guid userId)
    {
        return await _userService.CheckIfUserExists(userId);
    }
}