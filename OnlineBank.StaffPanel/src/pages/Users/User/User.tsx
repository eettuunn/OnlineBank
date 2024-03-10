import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Col, Collapse, Divider, Layout, Row } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';

import UserBlockInfo from '../components/UserBlockInfo/UserBlockInfo';
import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { AccountMock, UsersMock } from '../__mocks';
import { Status, columnLoans, columnsAccount } from '../constants';
import './User.scss';
import { useGetUserAccountsQuery } from '../api/accountsApi';
import { useGetUserInfoQuery } from '../api/usersApi';
import { dateParse } from '../../../shared/helpers/dateParse';
import { useAppSelector } from '../../../redux/hooks';
import { useGetUsersLoansQuery } from '../../Loans/api/loansApi';

const b = block('user-list');
const { Content } = Layout;
const { Panel } = Collapse;

const User: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();
    const { userId } = useParams();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    const pagination = useAppSelector(store => store.pagination[location.pathname] ?? store.pagination.empty);

    const { isLoading: isLoadingUser, data: dataUser } = useGetUserInfoQuery(userId as string, { pollingInterval: 20000 });
    const { isLoading: isLoadingAccounts, data: dataAccounts } = useGetUserAccountsQuery({ id: userId as string, params: pagination },
        { pollingInterval: 5000 });

    const { isLoading: isLoadingLoans, data: dataLoans } = useGetUsersLoansQuery(userId as string, { pollingInterval: 5000 });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Информация о пользователе' });
    }, [ setConfig ]);

    const prepareTableDataAccounts = columnsAccount.map((el) => {
        if (el.key === 'creationDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, string>) => <span>{dateParse(record.creationDate)}</span>,
            };
        } else if (el.key === 'isClosed') {
            return {
                ...el,
                width: '100px',
                render: (value: any, record: Record<string, unknown>) =>
                    !record.isClosed ? <span style={{ color: '#5E8C4E', fontWeight: '500' }}>{Status.Active}</span> : <span style={{ color: '#EB5757', fontWeight: '500' }}>{Status.Inactive}</span>,
            };
        } else return el;
    });

    const prepareTableDataLoans = columnLoans.map((el) =>{
        if (el.key === 'startDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, string>) => <span>{dateParse(record.startDate)}</span>,
            };
        } else if (el.key === 'endDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, string>) => <span>{dateParse(record.endDate)}</span>,
            };
        } else return el;
    },
    );

    const onRow = (record: Record<string, unknown>, rowIndex: number | undefined) => ({
        onMouseEnter: (event: React.MouseEvent) => {
            setIndexRow(rowIndex);
        },
        onMouseLeave: (event: React.MouseEvent) => {
            setIndexRow(undefined);
        },
        onClick: (event: React.MouseEvent) => {
            navigate(`account/${record.id as string}`);
        },
    });

    const newCloumnsAccounts = [ ...prepareTableDataAccounts ];
    const newColumnsLoans = [ ...prepareTableDataLoans ];
    return (
        <div className={b().toString()}>
            <MainHeader>
                <UserBlockInfo user={dataUser}/>
            </MainHeader>
            <Content>

                <Collapse bordered={true} className={b('collapse-loans').toString()} defaultActiveKey={[ '' ]}>
                    <Panel header={<Divider orientation="left">Кредиты</Divider>} key="loans">
                        <BaseTable
                            columns={newColumnsLoans}
                            dataSource={dataLoans as unknown as Record<string, unknown>[]}
                            isLoading={isLoadingLoans}
                        />
                    </Panel>
                </Collapse>

                <BaseTable
                    cursorPointer
                    columns={newCloumnsAccounts}
                    dataSource={dataAccounts?.data as unknown as Record<string, unknown>[]}
                    isLoading={isLoadingAccounts}
                    pageInfo={dataAccounts?.pageInfo}
                    onRow={onRow}
                />

            </Content>
        </div>
    );
};

export default User;
