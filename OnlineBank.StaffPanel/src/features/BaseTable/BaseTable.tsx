import React, { useMemo } from 'react';
import { Table } from 'antd';
import { ColumnGroupType, ColumnType, TableProps } from 'antd/lib/table';

// import { IResponseData } from '../../shared/types';

import MainPagination from './components/MainPagination/MainPagination';
import { IScroll } from './types';

import './BaseTable.scss';

interface IProps extends TableProps<Record<string, unknown>> {
    columns: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[];
    scroll?: IScroll;
    cursorPointer?: boolean;
    rowKey?: string;
    isLoading?: boolean;
    pageInfo?: {
        pagesCount: number,
        pageNumber: number,
        pageSize: number
    }
}

const BaseTable: React.FC<IProps> = (
    { columns, dataSource, isLoading, onRow, rowKey = 'id', cursorPointer, onChange, pageInfo  },
) =>

    (
        <Table
            bordered
            columns={columns}
            dataSource={(dataSource as Record<string, unknown>[]) || []}
            footer={() => pageInfo ? <MainPagination currentPage={pageInfo?.pageNumber} pageSize={pageInfo?.pageSize} total={pageInfo?.pagesCount} /> : null}
            loading={isLoading}
            pagination={false}
            rowKey={rowKey}
            size="small"
            sortDirections={[ 'descend', 'ascend' ]}
            style={{ cursor: cursorPointer ? 'pointer' : '' }}
            onChange={onChange}
            onRow={onRow}
        />
    )
        ;

export default BaseTable;
