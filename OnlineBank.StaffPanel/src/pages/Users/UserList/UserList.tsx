import React, { useCallback, useEffect, useState } from 'react';
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
import { IUser } from '../api/types';
import BlockingModal from '../components/BlockingModal/BlockingModal';
import { FormBlockingMode } from '../types';
import { useBlockUserMutation, useCreateUserMutation, useGetUsersQuery, useUnblockUserMutation } from '../api/usersApi';

import './UserList.scss';

const b = block('user-list');
const { Content } = Layout;

const UserList: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);
    const [ visible, setVisible ] = useState(false);

    const [ initialValues, setInitialValues ] = useState<IUser | object>({});

    const [ formBlockingMode, setFormBlockingMode ] = useState<FormBlockingMode>(FormBlockingMode.Blocking);
    const [ showBlockingModal, setShowBlockingModal ] = useState(false);

    const { isLoading: isLoadingUsers, isFetching: isFetchingUsers, data: dataUsers } = useGetUsersQuery(undefined);
    const [ create, { isLoading: isLoadingCreate, data: newUserData } ] = useCreateUserMutation();
    const [ blockUser, { isLoading: isLoadingBlock } ] = useBlockUserMutation();
    const [ unblockUser, { isLoading: isLoadingUnblock } ] = useUnblockUserMutation();

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Пользователи' });
    }, [ setConfig ]);

    /**
     * Метод создания пользователя
     */
    const onCreateUser = useCallback(
        async (values: IUser) => {
            const result = await create({
                email: values.email,
                userName: values.userName,
                roles: [ values.roles[0] ], //todo исправить роли
                ban: false,
            });
            return result;
        },
        [ create ],
    );

        /**
     * Метод блокировки пользователя
     */
    const onBlockUser = useCallback(
        async (values: IUser) => {
            const result = await blockUser(values.id as unknown as number);
            return result;
        },
        [ blockUser ],
    );

    /**
     * Метод разблокировки пользователя
     */
    const onUnblockUser = useCallback(
        async (values: IUser) => {
            const result = await unblockUser(values.id as unknown as number);
            return result;
        },
        [ unblockUser ],
    );

    /**
    * подготовка отображения в таблице не изменяя данных
    */
    const prepareTableData = columnsUser.map((el) => {
        if (el.key === 'roles') {
            return {
                ...el,
                width: '400px',
                render: (value: any, record: Record<string, unknown>) => RoleRus[record.roles as Role],
            };
        } else if (el.key === 'ban') {
            return {
                ...el,
                width: '300px',
                render: (value: any, record: Record<string, unknown>) =>
                    !record.ban ? <span style={{ color: '#5E8C4E' }}>{Status.Active}</span> : <span style={{ color: '#919191' }}>{Status.Inactive}</span>,
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
                            setInitialValues(record);
                            setShowBlockingModal(true);
                            setFormBlockingMode(!record.ban ? FormBlockingMode.Blocking : FormBlockingMode.Unblocking);
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
            navigate(`${record.id as string}`);
            setInitialValues(record);
        },
    });

    const newCloumns = [ ...prepareTableData, ...columnsAction ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <Button type="primary" onClick={() => {
                    setInitialValues({});
                    setVisible(true);
                }}>
                    Добавить пользователя
                </Button>
            </MainHeader>
            <Content>
                <BaseTable
                    cursorPointer
                    columns={newCloumns}
                    dataSource={dataUsers as Record<any, any>[]}
                    isLoading={isLoadingUsers || isFetchingUsers}
                    onRow={onRow}
                />
            </Content>
            <ModalCreateUser isLoading={isLoadingCreate} modal={{
                visible: visible,
                setVisible: setVisible,
            }} onSave={onCreateUser}/>
            <BlockingModal
                formMode={formBlockingMode}
                initialValues={initialValues}
                isLoading={isLoadingBlock || isLoadingUnblock}
                modal={{ visible: showBlockingModal, setVisible: setShowBlockingModal }}
                onSave={formBlockingMode === FormBlockingMode.Blocking ? onBlockUser : onUnblockUser}
            />
        </div>
    );
};

export default UserList;
