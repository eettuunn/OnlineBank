using AutoMapper;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.BL;

public class LoanServiceMapper : Profile
{
    public LoanServiceMapper()
    {
        CreateMap<CreateLoanRateDto, LoanRateEntity>();
        CreateMap<LoanRateEntity, LoanRateDto>();
        CreateMap<CreateLoanDto, LoanEntity>();
        CreateMap<LoanEntity, LoanListElementDto>();
        CreateMap<LoanPaymentEntity, LoanPaymentDto>();
    }
}