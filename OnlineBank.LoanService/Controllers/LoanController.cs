using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using OnlineBank.LoanService.Common.Dtos.Loan;
using OnlineBank.LoanService.Common.Dtos.LoanRate;
using OnlineBank.LoanService.Common.Interfaces;

namespace OnlineBank.LoanService.Controllers;

[Route("loan_api/loan")]
[Authorize]
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
            var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));
            await _loanService.TakeOutLoan(createLoanDto, userId);
            return Ok();
        }

        return BadRequest(ModelState);
    }
    
    /// <summary>
    /// Make loan payment
    /// </summary>
    [HttpPost]
    [Route("{loanId}")]
    public async Task<IActionResult> MakeLoanPayment([FromBody] CreatePaymentDto createPaymentDto, Guid loanId)
    {
        if (ModelState.IsValid)
        {
            var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));
            await _loanService.MakeLoanPayment(loanId, createPaymentDto, userId);
            return Ok();
        }

        return BadRequest(ModelState);
    }
    
    /// <summary>
    /// Get user's loans list
    /// </summary>
    [HttpGet]
    [Route("{userId}")]
    [Authorize(Roles = "Staff")]
    public async Task<List<LoanListElementDto>> GetUserLoans(Guid userId)
    {
        return await _loanService.GetUserLoans(userId);
    }
    
    /// <summary>
    /// Get my loans list
    /// </summary>
    [HttpGet]
    [Authorize]
    public async Task<List<LoanListElementDto>> GetMyLoans()
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));
        return await _loanService.GetUserLoans(userId);
    }

    /// <summary>
    /// Get my loan info
    /// </summary>
    [HttpGet]
    [Route("info/{loanId}")]
    [Authorize]
    public async Task<LoanInfoDto> GetLoanInfo(Guid loanId)
    {
        var userId = Guid.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier));
        return await _loanService.GetLoanInfo(loanId, userId);
    }
}