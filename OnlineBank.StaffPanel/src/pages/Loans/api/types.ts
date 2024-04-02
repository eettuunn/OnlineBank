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

export interface ICreateLoanRate {
    name: string,
    interestRate: number
}

export type ILoanPayment = {
    id: string,
    debt: number,
    paymentDate: string,
    isExpired: boolean
};

export interface ILoanResponse {
    loanInfo: ILoan,
    loanPayments: ILoanPayment[]
}
