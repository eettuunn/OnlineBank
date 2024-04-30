import { ColumnGroupType, ColumnType } from 'antd/lib/table';

export const columnsLog: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[] = [

    {
        key: 'url',
        title: 'URL',
        dataIndex: 'url',
    },
    {
        key: 'method',
        title: 'Метод',
        dataIndex: 'method',
    },
    {
        key: 'protocol',
        title: 'Протокол',
        dataIndex: 'protocol',
    },
    {
        key: 'status',
        title: 'Статус',
        dataIndex: 'status',
    },
    {
        key: 'spentTimeInMs',
        title: 'Время в миллисекундах',
        dataIndex: 'spentTimeInMs',
    },
    {
        key: 'apiName',
        title: 'API',
        dataIndex: 'apiName',
    },
];
