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
