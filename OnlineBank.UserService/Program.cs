using System.Text;
using System.Text.Json.Serialization;
using Common.Policies.Ban;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.IdentityModel.Tokens;
using OnlineBank.Common.Middlewares.ExceptionHandler;
using OnlineBank.UserService.BL;
using OnlineBank.UserService.BL.Services;
using OnlineBank.UserService.Common.Configs;
using OnlineBank.UserService.Common.Interfaces;
using OnlineBank.UserService.Configurators;
using OnlineBank.UserService.DAL;
using OnlineBank.UserService.DAL.Entities;

var builder = WebApplication.CreateBuilder(args);


builder.Services.AddControllers()
    .AddJsonOptions(opts =>
    {
        var enumConverter = new JsonStringEnumConverter();
        opts.JsonSerializerOptions.Converters.Add(enumConverter);
    });;
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(builder =>
    {
        builder.AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials()
            .WithOrigins("*");
    });
});

builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<ITokenService, TokenService>();
builder.Services.AddSingleton<IAuthorizationHandler, BanPolicyHandler>();
builder.Services.AddAutoMapper(typeof(UserServiceMapper));

builder.Services.AddAuthentication(opt => {
        opt.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
        opt.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
    })
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = false,
            ValidateIssuerSigningKey = true,
            ValidIssuer = JwtConfig.Issuer,
            ValidAudience = JwtConfig.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(JwtConfig.Key))
        };
    });
builder.Services.AddAuthorization(options =>
{
    options.DefaultPolicy =
        new AuthorizationPolicyBuilder
                (JwtBearerDefaults.AuthenticationScheme)
            .RequireAuthenticatedUser()
            .Build();
    options.AddPolicy(
        "Ban",
        policy => policy.Requirements.Add(new BanPolicy()));
});
builder.Services.AddIdentity<AppUser, IdentityRole>(options =>
    {
        options.Password.RequiredLength = 6;
        options.Password.RequireDigit = false;
        options.Password.RequiredUniqueChars = 0;
        options.Password.RequireLowercase = true;
        options.Password.RequireUppercase = false;
        options.Password.RequireNonAlphanumeric = false;

        options.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(5);
        options.Lockout.MaxFailedAccessAttempts = 5;
        options.Lockout.AllowedForNewUsers = true; 
        
        options.User.AllowedUserNameCharacters =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@+";
        options.User.RequireUniqueEmail = true;
    })
    .AddEntityFrameworkStores<UserServiceDbContext>()
    .AddUserManager<UserManager<AppUser>>()
    .AddSignInManager<SignInManager<AppUser>>()
    .AddDefaultTokenProviders();

builder.ConfigureUserServiceDAL();
var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();

app.ConfigureUserServiceDAL();

app.UseHttpsRedirection();

app.UseExceptionMiddleware();

app.UseAuthorization();

app.UseAuthorization();

app.MapControllers();

app.Run();