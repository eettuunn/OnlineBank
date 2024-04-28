using System.Reflection;
using System.Text.Json.Serialization;
using Common.Filters;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using OnlineBank.Common.Middlewares.ExceptionHandler;
using OnlineBank.MonitoringService;
using OnlineBank.MonitoringService.Configs;
using OnlineBank.MonitoringService.Services;

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

builder.Services.AddScoped<RequestsTracingService>();

builder.ConfigureMonitoringServiceDb();

var app = builder.Build();

app.ConfigureMonitoringServiceDb();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors();

app.UseHttpsRedirection();

app.UseExceptionMiddleware();

app.UseAuthorization();

app.UseAuthorization();

app.MapControllers();

app.Run();