import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons/lib/components/Icon';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import BaseTable from '../../../features/BaseTable/BaseTable';
import { columnsUser } from '../constants';
import { BlockIcon } from '../../../shared/img/BlockIcon';

import './UserList.scss';

const b = block('user-list');
const { Content } = Layout;

const UserList: React.FC = () => {
    const { setConfig } = useLayoutConfig();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    // const { isLoading: isLoadingUsers, isFetching: isFetchingUsers, data: dataUsers } = useGetUsersQuery(pagination);

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Log, headerTitle: 'Мониторинг' });
    }, [ setConfig ]);

    /**
    * подготовка отображения в таблице не изменяя данных
    */
    const prepareTableData = columnsUser.map((el) => {
        if (el.key === 'roles') {
            return {
                ...el,
                width: '300px',
                render: (value: any, record: Record<string, []>) => record.roles.map(role =>
                    <span className={b(`role-span ${role === 'staff' ? 'staff' : ''}`).toString()} key={role}>{role}</span>,
                ),
            };
        } else return el;
    });

    const columnsAction = [
        {
            key: 'action',
            title: '',
            dataIndex: 'action',
            width: '50px',
            className: 'actions',
            render: (value: any, record: Record<string, unknown>, index: number) =>
                indexRow === index ? (
                    <Button
                        className={b(record.ban ? 'block-btn' : 'unblock-btn').toString()}
                        icon={<Icon component={BlockIcon} style={{ fontSize: 18 }} />}
                        size="small"
                        type="link"
                        onClick={(event) => {
                            event.stopPropagation();
                        }}
                    />
                ) : null,
        },
    ];

    const onRow = (record: Record<string, unknown>, rowIndex: number | undefined) => ({
        onMouseEnter: () => {
            setIndexRow(rowIndex);
        },
        onMouseLeave: () => {
            setIndexRow(undefined);
        },
        onClick: () => {
        },
    });

    const newCloumns = [ ...prepareTableData, ...columnsAction ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <Button type="primary" onClick={() => {
                }}>
                    Добавить пользователя
                </Button>
            </MainHeader>
            <Content>
                <BaseTable
                    cursorPointer
                    columns={newCloumns}
                    dataSource={[] as Record<any, unknown>[]}
                    isLoading={false}
                    onRow={onRow}
                />
            </Content>
        </div>
    );
};

export default UserList;
