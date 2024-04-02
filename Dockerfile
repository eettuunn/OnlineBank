FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

ENV ASPNETCORE_ENVIRONMENT=Development

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["OnlineBank.LoanService/OnlineBank.LoanService.csproj", "OnlineBank.LoanService/"]
COPY ["Common/Common.csproj", "Common/"]
COPY ["OnlineBank.LoanService.BL/OnlineBank.LoanService.BL.csproj", "OnlineBank.LoanService.BL/"]
COPY ["OnlineBank.LoanService.Common/OnlineBank.LoanService.Common.csproj", "OnlineBank.LoanService.Common/"]
COPY ["OnlineBank.LoanService.DAL/OnlineBank.LoanService.DAL.csproj", "OnlineBank.LoanService.DAL/"]
COPY ["OnlineBank.UserService.BL/OnlineBank.UserService.BL.csproj", "OnlineBank.UserService.BL/"]
COPY ["OnlineBank.UserService.Common/OnlineBank.UserService.Common.csproj", "OnlineBank.UserService.Common/"]
COPY ["OnlineBank.UserService.DAL/OnlineBank.UserService.DAL.csproj", "OnlineBank.UserService.DAL/"]
RUN dotnet restore "OnlineBank.LoanService/OnlineBank.LoanService.csproj"
COPY . .
WORKDIR "/src/OnlineBank.LoanService"
RUN dotnet build "OnlineBank.LoanService.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "OnlineBank.LoanService.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "OnlineBank.LoanService.dll"]
