import { ColumnGroupType, ColumnType } from 'antd/lib/table';

export const columnsUser: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [

    {
        key: 'userName',
        title: 'Полное имя',
        dataIndex: 'userName',
    },
    {
        key: 'email',
        title: 'Почта',
        dataIndex: 'email',
    },
    {
        key: 'roles',
        title: 'Роль',
        dataIndex: 'roles',
    },
    {
        key: 'ban',
        title: 'Статус',
        dataIndex: 'ban',
    },
];

export const columnsAccount: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'number',
        title: '№ счета',
        dataIndex: 'number',
    },
    {
        key: 'name',
        title: 'Имя',
        dataIndex: 'name',
    },
    {
        key: 'creationDate',
        title: 'Дата создания',
        dataIndex: 'creationDate',
    },
    {
        key: 'isClosed',
        title: 'Статус',
        dataIndex: 'isClosed',
    },
    {
        key: 'balance',
        title: 'Баланс',
        dataIndex: 'balance',
    },
];

export const columnLoans: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'startDate',
        title: 'Дата начала',
        dataIndex: 'startDate',
    },
    {
        key: 'endDate',
        title: 'Дата окончания',
        dataIndex: 'endDate',
    },
    {
        key: 'debt',
        title: 'Долг, ₽',
        dataIndex: 'debt',
    },
    {
        key: 'monthlyPayment',
        title: 'Ежемесячный платеж, ₽',
        dataIndex: 'monthlyPayment',
    },
    {
        key: 'interestRate',
        title: 'Процентная ставка, %',
        dataIndex: 'interestRate',
    },
    {
        key: 'loanRateName',
        title: 'Тариф',
        dataIndex: 'loanRateName',
    },
];

export const columnsTransaction: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'transactionDate',
        title: 'Дата и время',
        dataIndex: 'transactionDate',
    },
    {
        key: 'amount',
        title: 'Сумма',
        dataIndex: 'amount',
    },
    {
        key: 'additionalInformation',
        title: 'Сообщение',
        dataIndex: 'additionalInformation',
    },
    {
        key: 'transactionType',
        title: 'Тип транзакции',
        dataIndex: 'transactionType',
    },
];

export const columnsLoanPayments: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'paymentDate',
        title: 'Дата платежа',
        dataIndex: 'paymentDate',
    },
    {
        key: 'debt',
        title: 'Долг',
        dataIndex: 'debt',
    },
    {
        key: 'isExpired',
        title: 'Просрочен',
        dataIndex: 'isExpired',
    },
];

export const enum Role {
    staff = 'Staff',
    customer = 'Customer',
}

export const RoleRus = {
    [Role.staff]: 'Сотрудник',
    [Role.customer]: 'Клиент',
};

/**
 * enum  для статуса пользователя
 */
export const enum Status {
    Active = 'Активный',
    Inactive = 'Заблокирован',
}

export enum TransactionType {
    WITHDRAW = 'WITHDRAW',
    DEPOSIT = 'DEPOSIT',
    REPAY_LOAN = 'REPAY_LOAN',
    TAKE_LOAN = 'TAKE_LOAN',
}

export const TransactionTypeRus = {
    [TransactionType.WITHDRAW]: 'СНЯТИЕ',
    [TransactionType.DEPOSIT]: 'ПОПОЛНЕНИЕ',
    [TransactionType.REPAY_LOAN]: 'ВЫПЛАТА ПО КРЕДИТУ',
    [TransactionType.TAKE_LOAN]: 'ВЗЯТИЕ КРЕДИТА',
};
