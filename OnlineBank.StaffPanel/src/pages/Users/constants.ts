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

export const columnsAccounts: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [
    {
        key: 'number',
        title: '№',
        dataIndex: 'number',
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

