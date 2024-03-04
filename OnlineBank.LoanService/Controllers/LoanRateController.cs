using System.ComponentModel.DataAnnotations;
using System.Runtime.InteropServices;
using Microsoft.AspNetCore.Mvc;
using OnlineBank.LoanService.Common.Dtos.LoanRate;
using OnlineBank.LoanService.Common.Interfaces;

namespace OnlineBank.LoanService.Controllers;

[Route("loan_api/rate")]
public class LoanRateController : ControllerBase
{
    private readonly ILoanRateService _loanRateService;

    public LoanRateController(ILoanRateService loanRateService)
    {
        _loanRateService = loanRateService;
    }

    [HttpPost]
    public async Task<IActionResult> CreateLoanRate([FromBody] CreateLoanRateDto createLoanRateDto)
    {
        if (ModelState.IsValid)
        {
            await _loanRateService.CreateRate(createLoanRateDto);
            return Ok();
        }

        return BadRequest(ModelState);
    }

    [HttpGet]
    public async Task<List<LoanRateDto>> GetLoanRates()
    {
        return await _loanRateService.GetLoanRates();
    }
}