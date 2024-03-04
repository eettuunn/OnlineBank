using AutoMapper;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using OnlineBank.LoanService.Common.Dtos.LoanRate;
using OnlineBank.LoanService.Common.Interfaces;
using OnlineBank.LoanService.DAL;
using OnlineBank.LoanService.DAL.Entities;

namespace OnlineBank.LoanService.BL.Services;

public class LoanRateService : ILoanRateService
{
    private readonly LoanServiceDbContext _context;
    private readonly IMapper _mapper;

    public LoanRateService(LoanServiceDbContext context, IMapper mapper)
    {
        _context = context;
        _mapper = mapper;
    }

    public async Task CreateRate(CreateLoanRateDto createLoanRateDto)
    {
        var rateEntity = _mapper.Map<LoanRateEntity>(createLoanRateDto);
        await _context.LoanRates.AddAsync(rateEntity);
        await _context.SaveChangesAsync();
    }

    public async Task<List<LoanRateDto>> GetLoanRates()
    {
        var loanRates = await _context.LoanRates.ToListAsync();
        var loanRatesDto = _mapper.Map<List<LoanRateDto>>(loanRates);

        return loanRatesDto;
    }
}