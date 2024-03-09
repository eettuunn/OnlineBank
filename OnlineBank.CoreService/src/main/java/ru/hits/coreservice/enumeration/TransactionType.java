package ru.hits.coreservice.enumeration;

public enum TransactionType {
    WITHDRAW,
    DEPOSIT,
    REPAY_LOAN,
    TAKE_LOAN;

    public static TransactionType fromDepositTransactionType(DepositTransactionType depositTransactionType) {
        switch (depositTransactionType) {
            case DEPOSIT:
                return DEPOSIT;
            case TAKE_LOAN:
                return TAKE_LOAN;
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип операции: " + depositTransactionType);
        }
    }

    public static TransactionType fromWithdrawTransactionType(WithdrawTransactionType withdrawTransactionType) {
        switch (withdrawTransactionType) {
            case WITHDRAW:
                return WITHDRAW;
            case REPAY_LOAN:
                return REPAY_LOAN;
            default:
                throw new IllegalArgumentException("Неподдерживаемый тип операции: " + withdrawTransactionType);
        }
    }
}
