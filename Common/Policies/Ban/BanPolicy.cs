using Microsoft.AspNetCore.Authorization;

namespace Common.Policies.Ban;

public class BanPolicy : IAuthorizationRequirement
{
    public BanPolicy()
    {
        
    }
}