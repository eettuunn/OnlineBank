using OnlineBank.LoanService.Configs;

namespace OnlineBank.LoanService.Common.Interfaces;

public interface ILoanRatingHelper
{
    Task<double> GetUserLoanRating(Guid userId);
    Task UpdateUserLoanRating(bool bigger, Guid userId);
}