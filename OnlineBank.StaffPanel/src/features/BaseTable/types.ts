export interface ITableColumn {
    key: string;
    title: string;
    dataIndex: string;
    sorter?: boolean | void;
}

/**
 * Интерфейс для скролла таблицы
 */
export type IScroll = {
    x?: string | number;
    y?: string | number;
    xMinContent?: string | number;
    yMinContent?: string | number;
};

export interface IComponentPaginationProps {
    total?: number;
    currentPage?: number;
    pageSize?: number;
}
