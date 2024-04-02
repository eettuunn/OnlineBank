import React, { useEffect } from 'react';
import block from 'bem-cn';
import { Collapse, Divider, Layout, Statistic } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';

import UserBlockInfo from '../components/UserBlockInfo/UserBlockInfo';
import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Status, columnLoans, columnsAccount } from '../constants';
import './User.scss';
import { useGetUserAccountsQuery } from '../api/accountsApi';
import { useGetUserInfoQuery, useGetUserLoanRateQuery } from '../api/usersApi';
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

    const pagination = useAppSelector(store => store.pagination[location.pathname] ?? store.pagination.empty);

    const { data: dataUser } = useGetUserInfoQuery(userId as string);
    const { isLoading: isLoadingAccounts, data: dataAccounts } = useGetUserAccountsQuery({ id: userId as string, params: pagination });
    const { isLoading: isLoadingUserRate, data: dataUserRate } = useGetUserLoanRateQuery(userId as string);
    const { isLoading: isLoadingLoans, data: dataLoans } = useGetUsersLoansQuery(userId as string);

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Информация о пользователе' });
    }, [ setConfig ]);

    const prepareTableDataAccounts = columnsAccount.map((el) => {
        if (el.key === 'creationDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, unknown>) => <span>{dateParse(record.creationDate as string)}</span>,
            };
        } else if (el.key === 'isClosed') {
            return {
                ...el,
                width: '100px',
                render: (value: any, record: Record<string, boolean>) =>
                    !record.isClosed ? <span style={{ color: '#5E8C4E', fontWeight: '500' }}>{Status.Active}</span> : <span style={{ color: '#EB5757', fontWeight: '500' }}>{Status.Inactive}</span>,
            };
        } else if (el.key === 'balance') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, { amount: number, currency: string }>) =>
                    <span>{record.balance.amount} {record.balance.currency}</span>,
            };
        } else return el;
    });

    const prepareTableDataLoans = columnLoans.map((el) =>{
        if (el.key === 'startDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, unknown>) => <span>{dateParse(record.startDate as string)}</span>,
            };
        } else if (el.key === 'endDate') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, unknown>) => <span>{dateParse(record.endDate as string)}</span>,
            };
        } else return el;
    },
    );

    const onRow = (record: Record<string, unknown>, rowIndex: number | undefined) => ({
        onMouseEnter: (event: React.MouseEvent) => {
        },
        onMouseLeave: (event: React.MouseEvent) => {
        },
        onClick: (event: React.MouseEvent) => {
            navigate(`account/${record.id as string}`);
        },
    });

    const onRowLoans = (record: Record<string, unknown>, rowIndex: number | undefined) => ({
        onMouseEnter: (event: React.MouseEvent) => {
        },
        onMouseLeave: (event: React.MouseEvent) => {
        },
        onClick: (event: React.MouseEvent) => {
            navigate(`loan/${record.id as string}`);
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
                        <Statistic className={b('rate').toString()} loading={isLoadingUserRate} suffix="/ 100" title="Кредитный рейтинг" value={dataUserRate}/>
                        <BaseTable
                            cursorPointer
                            columns={newColumnsLoans}
                            dataSource={dataLoans as unknown as Record<string, unknown>[]}
                            isLoading={isLoadingLoans}
                            onRow={onRowLoans}
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
