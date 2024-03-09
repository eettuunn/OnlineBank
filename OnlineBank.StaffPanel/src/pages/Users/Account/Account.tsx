import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Layout } from 'antd';
import { useNavigate } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';

import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { AccountMock } from '../__mocks';
import { columnsTransaction } from '../constants';
import './Account.scss';
import AccountBlockInfo from '../components/AccountBlockInfo/AccountBlockInfo';
import { useGetAccountInfoQuery, useGetAccountTransactionQuery } from '../api/accountsApi';

const b = block('user-list');
const { Content } = Layout;

const Account: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    const { isLoading: isLoadingAccount, data: dataAccount } = useGetAccountInfoQuery('77141e72-da79-44c8-b057-ea1ea39bac2a');
    const { isLoading: isLoadingTransactions, data: dataTransactions } = useGetAccountTransactionQuery('77141e72-da79-44c8-b057-ea1ea39bac2a');

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Данные по счету №1233345465' });
    }, [ setConfig ]);

    const prepareTableData = columnsTransaction;

    const onRow = (record: Record<string, unknown>, rowIndex: number | undefined) => ({
        onMouseEnter: (event: React.MouseEvent) => {
            setIndexRow(rowIndex);
        },
        onMouseLeave: (event: React.MouseEvent) => {
            setIndexRow(undefined);
        },
        onClick: (event: React.MouseEvent) => {
        },
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
                    cursorPointer
                    columns={newCloumns}
                    dataSource={dataTransactions as Record<any, any>[]}
                    isLoading={isLoadingTransactions}
                    onRow={onRow}
                />
            </Content>
        </div>
    );
};

export default Account;
