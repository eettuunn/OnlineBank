import React, { useCallback, useState } from 'react';
import { Button, Pagination, Row, Select, Typography } from 'antd';
import Icon from '@ant-design/icons/lib/components/Icon';

import { useAppDispatch } from '../../../../redux/hooks';
import { IComponentPaginationProps } from '../../types';

import type { PaginationProps } from 'antd';

import '../../BaseTable.scss';

const { Text } = Typography;

const itemRender: PaginationProps['itemRender'] = (_, type, originalElement) => {
    if (type === 'prev') {
        // return <Button className="ant-pagination-item-link" icon={<Icon component={ArrowIcon} />} />;
    }
    if (type === 'next') {
        // return <Button className="ant-pagination-item-link" icon={<Icon component={ArrowIcon} />} style={{ rotate: '180deg' }} />;
    }
    return originalElement;
};

const MainPagination: React.FC<IComponentPaginationProps> = ({ total = 0, currentPage = 1, pageSize = 10 }) => {
    const { pathname } = location;

    const [ newPageSize, setPageSize ] = useState<number>(pageSize);
    const [ newCurrentPage, setCurrentPage ] = useState<number>(currentPage);

    const dispatch = useAppDispatch();

    const onChangeCurrentPage = (page: number, pageSize: number) => {
        setCurrentPage(page);
        setPageSize(pageSize);
    };

    const onChangePageSize = useCallback(
        (value: number) => {
            // if (total && newCurrentPage > Math.ceil(total / value)) {
            //     setCurrentPage(Math.ceil(total / value));
            //     dispatch(changePagination({ path: pathname, paginationData: { pageSize: value, currentPage: Math.ceil(total / value) } }));
            // } else {
            //     dispatch(changePagination({ path: pathname, paginationData: { pageSize: value, currentPage: newCurrentPage } }));
            // }
            setPageSize(value);
        },
        [ dispatch, newCurrentPage, pathname, total ],
    );

    return (
        <Row align="middle">
            <Text>Страница:</Text>
            <Pagination
                current={newCurrentPage}
                itemRender={itemRender}
                pageSize={newPageSize}
                showSizeChanger={false}
                showTitle={false}
                // showTotal={(total, range) => (
                //     <>
                //         <Text>
                //             {range[0]}-{range[1]} из
                //         </Text>{' '}
                //         <Text strong>{total}</Text>
                //     </>
                // )}
                total={total}
                onChange={onChangeCurrentPage}
            />
            <Select
                defaultValue={newPageSize}
                options={[
                    {
                        value: 10,
                        label: '10',
                    },
                    {
                        value: 20,
                        label: '20',
                    },
                    {
                        value: 50,
                        label: '50',
                    },
                    {
                        value: 100,
                        label: '100',
                    },
                ]}
                // suffixIcon={<Icon component={ArrowIcon} style={{ rotate: '-90deg', pointerEvents: 'none' }} />}
                onChange={onChangePageSize}
            />
        </Row>
    );
};

export default MainPagination;
