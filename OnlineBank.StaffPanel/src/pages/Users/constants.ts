import { ColumnGroupType, ColumnType } from 'antd/lib/table';

export const columnsUser: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'id',
        title: '№',
        dataIndex: 'id',
    },
    {
        key: 'fullName',
        title: 'Полное имя',
        dataIndex: 'fullName',
    },
    {
        key: 'role',
        title: 'Роль',
        dataIndex: 'role',
    },
    {
        key: 'isLocked',
        title: 'Статус',
        dataIndex: 'isLocked',
    },
];

export const columnsAccount: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'number',
        title: '№',
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
        key: 'type',
        title: 'Тип транзакции',
        dataIndex: 'type',
    },
];

export const enum Role {
    staff = 'staff',
    customer = 'customer',
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
    Inactive = 'Неактивный',
}
