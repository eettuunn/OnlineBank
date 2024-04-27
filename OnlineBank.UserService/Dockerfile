FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

ENV ASPNETCORE_ENVIRONMENT=Development
ENV ASPNETCORE_URLS http://*:80

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["OnlineBank.UserService/OnlineBank.UserService.csproj", "OnlineBank.UserService/"]
COPY ["OnlineBank.UserService.BL/OnlineBank.UserService.BL.csproj", "OnlineBank.UserService.BL/"]
COPY ["Common/Common.csproj", "Common/"]
COPY ["OnlineBank.UserService.Common/OnlineBank.UserService.Common.csproj", "OnlineBank.UserService.Common/"]
COPY ["OnlineBank.UserService.DAL/OnlineBank.UserService.DAL.csproj", "OnlineBank.UserService.DAL/"]
RUN dotnet restore "OnlineBank.UserService/OnlineBank.UserService.csproj"
COPY . .
WORKDIR "/src/OnlineBank.UserService"
RUN dotnet build "OnlineBank.UserService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "OnlineBank.UserService.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "OnlineBank.UserService.dll"]
