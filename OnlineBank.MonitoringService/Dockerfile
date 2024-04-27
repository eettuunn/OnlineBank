FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

ENV ASPNETCORE_ENVIRONMENT=Development
ENV ASPNETCORE_URLS http://*:80

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["OnlineBank.MonitoringService/OnlineBank.MonitoringService.csproj", "OnlineBank.MonitoringService/"]
COPY ["Common/Common.csproj", "Common/"]
RUN dotnet restore "OnlineBank.MonitoringService/OnlineBank.MonitoringService.csproj"
COPY . .
WORKDIR "/src/OnlineBank.MonitoringService"
RUN dotnet build "OnlineBank.MonitoringService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "OnlineBank.MonitoringService.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "OnlineBank.MonitoringService.dll"]
