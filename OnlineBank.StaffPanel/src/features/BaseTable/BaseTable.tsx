import React, { useMemo } from 'react';
import { Table } from 'antd';
import { ColumnGroupType, ColumnType, TableProps } from 'antd/lib/table';

// import { IResponseData } from '../../shared/types';

import MainPagination from './components/MainPagination/MainPagination';
import { IScroll } from './types';

import './BaseTable.scss';

interface IProps extends TableProps<Record<string, unknown>> {
    columns: (ColumnGroupType<Record<string, unknown>> | ColumnType<Record<string, unknown>>)[];
    // sourceData: IResponseData<unknown> | undefined;
    scroll?: IScroll;
    cursorPointer?: boolean;
    rowKey?: string;
    isLoading?: boolean;
}

const BaseTable: React.FC<IProps> = (
    // { columns, sourceData, isLoading, onRow, rowKey = 'id', scroll, cursorPointer, onChange }
) =>
// const newScroll: IScroll | undefined = useMemo(() => {
//     if (scroll?.x !== undefined && scroll?.xMinContent !== undefined) {
//         scroll.x = `max(${scroll.x}, ${scroll.xMinContent})`;
//     }
//     if (scroll?.y !== undefined && scroll?.yMinContent !== undefined) {
//         scroll.y = `max(${scroll.y}, ${scroll.yMinContent})`;
//     }
//     return scroll;
// }, [ scroll ]);

    (
    // <Table
    //     bordered
    //     columns={columns}
    //     dataSource={(sourceData?.content as Record<string, unknown>[]) || []}
    //     footer={() => <MainPagination currentPage={sourceData?.currentPage} pageSize={sourceData?.pageSize} total={sourceData?.total} />}
    //     loading={isLoading}
    //     pagination={false}
    //     rowKey={rowKey}
    //     scroll={newScroll ?? { y: 'calc(100vh - 310px)' }}
    //     size="small"
    //     sortDirections={[ 'descend', 'ascend' ]}
    //     style={{ cursor: cursorPointer ? 'pointer' : '' }}
    //     onChange={onChange}
    //     onRow={onRow}
    // />
        <>12</>
    )
    ;

export default BaseTable;
