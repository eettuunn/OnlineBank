using Microsoft.AspNetCore.Mvc;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;
using OnlineBank.LoanService.Common.Interfaces;

namespace OnlineBank.LoanService.Controllers;

[Route("loan_api/loan")]
public class LoanController : ControllerBase
{
    private readonly ILoanService _loanService;

    public LoanController(ILoanService loanService)
    {
        _loanService = loanService;
    }

    /// <summary>
    /// Take out new loan
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> TakeOutLoan([FromBody] CreateLoanDto createLoanDto)
    {
        if (ModelState.IsValid)
        {
            await _loanService.TakeOutLoan(createLoanDto);
            return Ok();
        }

        return BadRequest(ModelState);
    }
    
    /// <summary>
    /// Make loan payment
    /// </summary>
    [HttpPost]
    [Route("{loanId}")]
    public async Task<IActionResult> MakeLoanPayment([FromBody] PaymentDto paymentDto, Guid loanId)
    {
        if (ModelState.IsValid)
        {
            await _loanService.MakeLoanPayment(loanId, paymentDto);
            return Ok();
        }

        return BadRequest(ModelState);
    }
    
    /// <summary>
    /// Get user's loans list
    /// </summary>
    [HttpGet]
    [Route("{userId}")]
    public async Task<List<LoanDto>> GetUserLoans(Guid userId)
    {
        return await _loanService.GetUserLoans(userId);
    }
}