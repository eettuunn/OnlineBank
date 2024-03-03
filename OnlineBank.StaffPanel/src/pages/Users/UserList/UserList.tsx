import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons/lib/components/Icon';
import { useNavigate } from 'react-router-dom';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import BaseTable from '../../../features/BaseTable/BaseTable';
import { UsersMock } from '../__mocks';
import { Role, RoleRus, Status, columnsUser } from '../constants';
import ModalCreateUser from '../components/ModalCreateUser/ModalCreateUser';
import { BlockIcon } from '../../../shared/img/BlockIcon';

import './UserList.scss';

const b = block('user-list');
const { Content } = Layout;

const UserList: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);
    const [ visible, setVisible ] = useState(false);

    // const { isLoading, data: dataEnginesWithTestsQuery, isError: isErrorEngine, refetch } = useGetEnginesWithTestsQuery({ search, ...pagination });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Пользователи' });
    }, [ setConfig ]);

    /**
    * подготовка отображения в таблице не изменяя данных
    */
    const prepareTableData = columnsUser.map((el) => {
        if (el.key === 'role') {
            return {
                ...el,
                render: (value: any, record: Record<string, unknown>, index: number) => RoleRus[record.role as Role],
            };
        } else if (el.key === 'isLocked') {
            return {
                ...el,
                width: '118px',
                render: (value: any, record: Record<string, unknown>, index: number) =>
                    !record.isLocked ? <span style={{ color: '#5E8C4E' }}>{Status.Active}</span> : <span style={{ color: '#919191' }}>{Status.Inactive}</span>,
            };
        } else return el;
    });

    const columnsAction = [
        {
            key: 'action',
            title: '',
            dataIndex: 'action',
            width: '160px',
            className: 'actions',
            render: (value: any, record: Record<string, unknown>, index: number) =>
                indexRow === index ? (
                    <Button
                        className={b(!record.isLocked ? 'block-btn' : 'unblock-btn').toString()}
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
        onMouseEnter: (event: React.MouseEvent) => {
            setIndexRow(rowIndex);
        },
        onMouseLeave: (event: React.MouseEvent) => {
            setIndexRow(undefined);
        },
        onClick: (event: React.MouseEvent) => {
            navigate(`${rowIndex as unknown as string}`);
        },
    });

    const newCloumns = [ ...prepareTableData, ...columnsAction ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <Button type="primary" onClick={() => setVisible(true)}>
                    Добавить пользователя
                </Button>
            </MainHeader>
            <Content>
                <BaseTable
                    cursorPointer
                    columns={newCloumns}
                    // isLoading={isLoadingUsers || isFetchingUsers}
                    dataSource={UsersMock as Record<any, any>[]}
                    onRow={onRow}
                />
            </Content>
            <ModalCreateUser isLoading={false} modal={{
                visible: visible,
                setVisible: setVisible,
            }} />
        </div>
    );
};

export default UserList;
