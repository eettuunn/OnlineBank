export interface ILoanRate {
    id: string,
    name: string,
    interestRate: number
}

export interface ILoan {
    id: string,
    startDate: string,
    endDate: string,
    debt: number,
    monthlyPayment: number,
    bankAccountId: string,
    interestRate: number,
    loanRateName: string,
}
