using System.Reflection;
using System.Text;
using System.Text.Json.Serialization;
using Common.Configs;
using Common.Filters;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using OnlineBank.Common.Middlewares.ExceptionHandler;
using OnlineBank.LoanService.BL;
using OnlineBank.LoanService.BL.Services;
using OnlineBank.LoanService.BL.Services.Background;
using OnlineBank.LoanService.Common.Interfaces;
using OnlineBank.LoanService.Configs;
using OnlineBank.LoanService.Configurators;
using OnlineBank.UserService.Common.Configs;
using RabbitMQ.Client;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers()
    .AddJsonOptions(opts =>
    {
        var enumConverter = new JsonStringEnumConverter();
        opts.JsonSerializerOptions.Converters.Add(enumConverter);
    });;
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        In = ParameterLocation.Header,
        Description = "Please enter a valid token",
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        BearerFormat = "JWT",
        Scheme = "Bearer"
    });
    var xmlFilename = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    options.IncludeXmlComments(Path.Combine(AppContext.BaseDirectory, xmlFilename));
    options.OperationFilter<SwaggerFilter>();
});

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(builder =>
    {
        builder.AllowAnyMethod()
            .AllowAnyHeader()
            .WithOrigins("*");
    });
});

builder.Services.Configure<IntegrationApisUrls>(builder.Configuration.GetSection("IntegrationApisUrls"));

builder.Services.AddScoped<ILoanRateService, LoanRateService>();
builder.Services.AddScoped<ILoanService, LoanService>();
builder.Services.AddScoped<ILoanRatingHelper, LoanRatingHelper>();
builder.Services.AddScoped<IMessageProducer, MessageProducer>();
// builder.Services.AddHostedService<LoanPaymentChecker>();
builder.Services.AddAutoMapper(typeof(LoanServiceMapper));

var rabbitMqConnection = builder.Configuration.GetSection("RabbitMqConnection").Get<RabbitMqConnection>();
builder.Services.AddSingleton<IConnection>(x =>
    new ConnectionFactory
    {
        HostName = rabbitMqConnection.Hostname,
        UserName = rabbitMqConnection.Username,
        Password = rabbitMqConnection.Password,
        VirtualHost = rabbitMqConnection.VirtualHost,
        Port = int.Parse(rabbitMqConnection.Port)
    }.CreateConnection()
);

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
});

builder.ConfigureLoanServiceDAL();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();

app.ConfigureLoanServiceDAL();

app.UseHttpsRedirection();

app.UseExceptionMiddleware();
app.UseRandomErrorMiddleware();

app.UseAuthorization();

app.UseAuthorization();

app.MapControllers();

app.Run();