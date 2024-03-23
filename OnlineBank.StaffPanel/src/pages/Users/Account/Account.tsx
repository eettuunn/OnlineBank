import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Button, Layout } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';
import { ArrowLeftOutlined } from '@ant-design/icons';

import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { TransactionType, TransactionTypeRus, columnsTransaction } from '../constants';
import AccountBlockInfo from '../components/AccountBlockInfo/AccountBlockInfo';
import { useGetAccountInfoQuery, useGetAccountTransactionQuery } from '../api/accountsApi';
import { useAppSelector } from '../../../redux/hooks';
import { dateParse } from '../../../shared/helpers/dateParse';

const b = block('account');
const { Content } = Layout;

const Account: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();
    const { accountId, userId } = useParams();

    const pagination = useAppSelector(store => store.pagination[location.pathname] ?? store.pagination.empty);

    const { data: dataAccount } = useGetAccountInfoQuery(accountId as string);
    const { isLoading: isLoadingTransactions, data: dataTransactions } = useGetAccountTransactionQuery({ id: accountId as string, params: pagination });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: `Счет №${dataAccount?.number as string}`,
            backButton: (
                <div className={b('block-back-button').toString()}>
                    <Button
                        className={b('back-button').toString()}
                        type="link"
                        onClick={() => {
                            navigate(`/users/${userId as string}`);
                        }}
                    >
                        <ArrowLeftOutlined /> К владельцу счета
                    </Button>
                </div>
            ) });
    }, [ dataAccount, navigate, userId ]);

    const prepareTableData = columnsTransaction.map((el) => {

        if (el.key === 'transactionDate') {
            return {
                ...el,
                width: '300px',
                render: (value: any, record: Record<string, unknown>) => <span>{dateParse(record.transactionDate as string)}</span>,
            };
        } else if (el.key === 'transactionType') {
            return {
                ...el,
                width: '400px',
                render: (value: any, record: Record<string, TransactionType>) => <span style={{ fontWeight: '500' }}>{TransactionTypeRus[TransactionType[record.transactionType]]}</span>,
            };
        } else if (el.key === 'amount') {
            return {
                ...el,
                width: '200px',
                render: (value: any, record: Record<string, unknown>) => <span style={{ fontWeight: '500' }}>{record.amount}</span>,
            };
        } else return el;
    });

    const newCloumns = [ ...prepareTableData ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <AccountBlockInfo account={dataAccount}/>
            </MainHeader>
            <Content>
                <Title level={3}>Операции</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={dataTransactions?.data as Record<any, any>[]}
                    isLoading={isLoadingTransactions}
                    pageInfo={dataTransactions?.pageInfo}
                />
            </Content>
        </div>
    );
};

export default Account;
