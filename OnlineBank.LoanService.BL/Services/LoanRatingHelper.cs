using System.Text;
using System.Text.Json;
using Microsoft.Extensions.Options;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Interfaces;
using OnlineBank.LoanService.Configs;

namespace OnlineBank.LoanService.BL.Services;

public class LoanRatingHelper : ILoanRatingHelper
{
    private readonly IntegrationApisUrls _integrationApisUrls;

    public LoanRatingHelper(IOptions<IntegrationApisUrls> options)
    {
        _integrationApisUrls = options.Value;
    }

    public async Task<double> GetUserLoanRating(Guid userId)
    {
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.UserServiceUrl + "/user_api/user/" + userId + "/loan_rating";
            var response = await client.GetAsync(url);

            if (response.IsSuccessStatusCode)
            {
                var content = await response.Content.ReadAsStringAsync();
                var amount = double.Parse(content.Replace('.', ','));
                
                return amount;
            }

            throw new Exception(response.StatusCode.ToString());
        }
    }

    public async Task UpdateUserLoanRating(bool bigger, Guid userId)
    {
        var userLoanRating = await GetUserLoanRating(userId);

        double newRating;
        
        if (bigger)
        {
            newRating = userLoanRating + ((100 - userLoanRating) / 20) * Math.Tanh(0.1 * userLoanRating);
        }
        else
        {
            newRating = userLoanRating - (userLoanRating / 20) * Math.Tanh(0.1 * (100 - userLoanRating));
        }
        
        using (var client = new HttpClient())
        {
            var url = _integrationApisUrls.UserServiceUrl + "/user_api/user/" + userId + "/loan_rating";

            var body = new UpdateRatingDto
            {
                amount = newRating
            };
            var strBody = JsonSerializer.Serialize(body);
            var content = new StringContent(strBody, Encoding.UTF8, "application/json");
            var response = await client.PostAsync(url, content);

            if (!response.IsSuccessStatusCode)
            {
                throw new Exception(await response.Content.ReadAsStringAsync());
            }
        }
    }
}