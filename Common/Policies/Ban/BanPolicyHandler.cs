using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;

namespace Common.Policies.Ban;

public class BanPolicyHandler : AuthorizationHandler<BanPolicy>
{
    private readonly IHttpContextAccessor _httpContextAccessor;

    public BanPolicyHandler(IHttpContextAccessor httpContextAccessor)
    {
        _httpContextAccessor = httpContextAccessor;
    }

    protected override async Task HandleRequirementAsync(AuthorizationHandlerContext context, BanPolicy requirement)
    {
        if (_httpContextAccessor != null)
        {
            var isBan = _httpContextAccessor.HttpContext.User.FindFirst("ban")?.Value;

            if (isBan == "True")
            {
                var ex = new Exception(string.Format("{0} - {1}", "you are in ban", "403"));
                ex.Data.Add("you are in ban", "403");
                throw ex;
            }

            context.Succeed(requirement);
        }
        else
        {
            var ex = new Exception(string.Format("{0} - {1}", "bad request", "400"));
            ex.Data.Add("bad request", "400");
            throw ex;
        }
    }
}