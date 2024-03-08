package ru.hits.coreservice.enumeration;

public enum TransactionType {
    WITHDRAW,
    DEPOSIT,
    REPAY_LOAN,
    TAKE_LOAN;

    public static TransactionType fromLoanTransactionType(LoanTransactionType loanTransactionType) {
        switch (loanTransactionType) {
            case REPAY_LOAN:
                return REPAY_LOAN;
            case TAKE_LOAN:
                return TAKE_LOAN;
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип операции: " + loanTransactionType);
        }
    }
}
