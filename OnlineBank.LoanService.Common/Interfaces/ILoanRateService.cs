using OnlineBank.LoanService.Common.Dtos.LoanRate;

namespace OnlineBank.LoanService.Common.Interfaces;

public interface ILoanRateService
{
    Task CreateRate(CreateLoanRateDto createLoanRateDto);
    Task<List<LoanRateDto>> GetLoanRates();
}