import React, { useCallback, useEffect, useState } from 'react';
import { Button, Pagination, Row, Select, Typography } from 'antd';

import { changePagination } from '../../../../redux/reducers/pagination.slice';
import { useAppDispatch, useAppSelector } from '../../../../redux/hooks';
import { IComponentPaginationProps } from '../../types';

import type { PaginationProps } from 'antd';

import '../../BaseTable.scss';

const { Text } = Typography;

const MainPagination: React.FC<IComponentPaginationProps> = ({ pagesCount = 1, pageNumber = 1, pageSize = 10 }) => {
    const pagination = useAppSelector(state => state.pagination);
    const { pathname } = location;
    const [ total, setTotal ] = useState<number>(pagesCount);

    const [ newPageSize, setPageSize ] = useState<number>(pagination[pathname]?.pageSize ?? pageSize);
    const [ newCurrentPage, setCurrentPage ] = useState<number>(pagination[pathname]?.pageNumber ?? pageNumber);
    console.log(newPageSize, newCurrentPage);

    const dispatch = useAppDispatch();

    const onChangeCurrentPage = (page: number, pageSize: number) => {
        setCurrentPage(page);
        setPageSize(pageSize);
        dispatch(changePagination({ path: pathname, paginationData: { pageSize: pageSize, pageNumber: page } }));
    };

    useEffect(() => {
        setTotal(pagesCount * pageSize);
    }, [ pageSize, pagesCount ]);

    const onChangePageSize = useCallback(
        (value: number) => {
            if (pagesCount && newCurrentPage > Math.ceil(pagesCount)) {
                setCurrentPage(Math.ceil(pagesCount));
                dispatch(changePagination({ path: pathname, paginationData: { pageSize: value, pageNumber: Math.ceil(pagesCount) } }));
            } else {
                dispatch(changePagination({ path: pathname, paginationData: { pageSize: value, pageNumber: newCurrentPage } }));
            }
            setPageSize(value);
        },
        [ dispatch, newCurrentPage, pathname, pagesCount ],
    );

    return (
        <Row align="middle">
            <Text>Страница:</Text>
            <Pagination
                current={newCurrentPage}
                pageSize={newPageSize}
                showSizeChanger={false}
                showTitle={false}
                showTotal={(total, range) => (
                    <>
                        <Text>
                            {range[0]}-{range[1]} из
                        </Text>{' '}
                        <Text strong>{total}</Text>
                    </>
                )}
                total={total}
                onChange={onChangeCurrentPage}
            />
            <Select
                defaultValue={newPageSize}
                options={[
                    {
                        value: 5,
                        label: '5',
                    },
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
                ]}
                // suffixIcon={<Icon component={ArrowIcon} style={{ rotate: '-90deg', pointerEvents: 'none' }} />}
                onChange={onChangePageSize}
            />
        </Row>
    );
};

export default MainPagination;
