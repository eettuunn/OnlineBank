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
import { Status, columnsAccount } from '../constants';
import './User.scss';
import { useGetUserAccountsQuery } from '../api/accountsApi';
import { useGetUserInfoQuery } from '../api/usersApi';
import { dateParse } from '../../../shared/helpers/dateParse';

const b = block('user-list');
const { Content } = Layout;
const { Panel } = Collapse;

const User: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();
    const { userId } = useParams();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    const { isLoading: isLoadingUser, data: dataUser } = useGetUserInfoQuery(userId as string);
    const { isLoading: isLoadingAccounts, data: dataAccounts } = useGetUserAccountsQuery(userId as string);

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Информация о пользователе' });
    }, [ setConfig ]);

    const prepareTableData = columnsAccount.map((el) => {
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

    const newCloumns = [ ...prepareTableData ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <UserBlockInfo user={dataUser ?? UsersMock[0]}/>
            </MainHeader>
            <Content>

                <Collapse bordered={true} className={b('collapse-loans').toString()} defaultActiveKey={[ 'loans' ]}>
                    <Panel header={<Divider orientation="left">Кредиты</Divider>} key="loans">
                        {/* <BaseTable
                            cursorPointer
                            columns={newCloumns}
                            // isLoading={isLoadingUsers || isFetchingUsers}
                            dataSource={UsersMock as Record<any, any>[]}
                            // onRow={onRow}
                        />, */}
                    </Panel>
                </Collapse>

                {/* <Collapse bordered={false} defaultActiveKey={[ 'accounts' ]}>
                    <Panel header={<Divider orientation="left">Счета</Divider>} key="accounts"> */}
                <BaseTable
                    cursorPointer
                    columns={newCloumns}
                    dataSource={dataAccounts?.bankAccounts as unknown as Record<string, unknown>[]}
                    isLoading={isLoadingAccounts}
                    pageInfo={dataAccounts?.pageInfo}
                    onRow={onRow}
                />
                {/* </Panel>
                </Collapse> */}

            </Content>
        </div>
    );
};

export default User;
