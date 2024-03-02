using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Common.Exceptions;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using OnlineBank.UserService.Common.Configs;
using OnlineBank.UserService.Common.Enums;
using OnlineBank.UserService.Common.Interfaces;
using OnlineBank.UserService.DAL;
using OnlineBank.UserService.DAL.Entities;
using JwtRegisteredClaimNames = Microsoft.IdentityModel.JsonWebTokens.JwtRegisteredClaimNames;

namespace OnlineBank.UserService.BL.Services;

public class TokenService : ITokenService
{
    private readonly UserManager<AppUser> _userManager;

    public TokenService(UserManager<AppUser> userManager)
    {
        _userManager = userManager;
    }

    public async Task<string> CreateToken(Guid userId)
    {
        var user = await _userManager.FindByIdAsync(userId.ToString())
                    ?? throw new CantFindByIdException("user", userId);
        var roles = await _userManager.GetRolesAsync(user);
        
        var newToken = CreateJwtToken(await CreateClaims(user, roles));
        var tokenHandler = new JwtSecurityTokenHandler();
        
        return tokenHandler.WriteToken(newToken);
    }
    
    
    
    private async Task<List<Claim>> CreateClaims(AppUser user, IEnumerable<string> roles)
    {
        var claims = new List<Claim>
        {
            new(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            new(ClaimTypes.NameIdentifier, user.Id),
            new(ClaimTypes.Name, user.UserName),
            new(ClaimTypes.Email, user.Email),
            new("ban", user.Ban.ToString())
        };
        
        foreach (var r in roles)
        {
            claims.Add(new(ClaimTypes.Role, r));
        }
        
        return claims;
    }

    private JwtSecurityToken CreateJwtToken(IEnumerable<Claim> claims)
    {
        return new JwtSecurityToken(
            JwtConfig.Issuer,
            JwtConfig.Audience,
            claims,
            expires: DateTime.UtcNow.AddMinutes(JwtConfig.AccessMinutesLifeTime),
            signingCredentials: new SigningCredentials(new SymmetricSecurityKey(Encoding.ASCII.GetBytes(JwtConfig.Key)),
                SecurityAlgorithms.HmacSha256)
        );
    }
}