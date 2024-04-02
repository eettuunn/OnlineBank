export interface ITableColumn {
    key: string;
    title: string;
    dataIndex: string;
    sorter?: boolean | void;
}

export interface IComponentPaginationProps {
    total?: number;
    pageNumber?: number;
    pageSize?: number;
}
