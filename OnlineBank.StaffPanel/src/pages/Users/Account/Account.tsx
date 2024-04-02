/* eslint-disable react/no-multi-comp */
import React, { useEffect, useMemo, useState } from 'react';
import block from 'bem-cn';
import { Button, Layout } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { StompSessionProvider, useSubscription } from 'react-stomp-hooks';

import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths, apiBaseUrl } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { TransactionType, TransactionTypeRus, columnsTransaction } from '../constants';
import AccountBlockInfo from '../components/AccountBlockInfo/AccountBlockInfo';
import { accountsApi, useGetAccountInfoQuery, useGetAccountTransactionQuery } from '../api/accountsApi';
import { useAppDispatch, useAppSelector } from '../../../redux/hooks';
import { dateParse } from '../../../shared/helpers/dateParse';
import { ITransaction } from '../api/types';
import { getStorageValue } from '../../../shared/hooks/useLocalStorage/useLocalStorage';

const b = block('account');
const { Content } = Layout;

const AccountRaw: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();
    const { accountId, userId } = useParams();
    const dispatch = useAppDispatch();

    const pagination = useAppSelector(store => store.pagination.empty);

    const { data: dataAccount } = useGetAccountInfoQuery(accountId as string);
    const { isLoading: isLoadingTransactions, data: dataTransactions } = useGetAccountTransactionQuery({ id: accountId as string, params: pagination });
    const access = getStorageValue<string>('access');

    const [ newRow, setNewRow ] = useState('');
    useSubscription(`/topic/bank-accounts/${accountId as string}/transactions`, (message) => {setNewRow(message.body);},
        {
            'Authorization': `Bearer ${access ?? ''}`,
        });

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

    const wsValue = useMemo(() => newRow ? JSON.parse(newRow) as ITransaction : undefined, [ newRow ]);

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

    const [ dataSource, setDataSource ] = useState<ITransaction[]>([]);

    useEffect(() => {
        setDataSource(dataTransactions?.data ?? []);
        if (wsValue) {
            dispatch(accountsApi.util.invalidateTags([ 'Account' ]));
            setDataSource([ wsValue, ...dataSource ]);
        }
    }, [ dataTransactions?.data, wsValue ]);

    return (
        <div className={b().toString()}>
            <MainHeader>
                <AccountBlockInfo account={dataAccount}/>
            </MainHeader>
            <Content>
                <Title level={3}>Операции</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={dataSource as Record<any, any>[]}
                    isLoading={isLoadingTransactions}
                    // pageInfo={dataTransactions?.pageInfo}
                />
            </Content>
        </div>
    );
};

const Account: React.FC = () => (
    <StompSessionProvider
        url={`${apiBaseUrl ?? ''}ws`}>
        <AccountRaw/>
    </StompSessionProvider>
);

export default Account;
