namespace OnlineBank.UserService.Common.Configs;

public static class JwtConfig
{
    public static string Issuer = "bank_backend";

    public static string Audience = "bank_frontend";

    public static string Key = "TheVeryStrongKeyOrPasswordQwerty123";

    public static int AccessMinutesLifeTime = 240;
}