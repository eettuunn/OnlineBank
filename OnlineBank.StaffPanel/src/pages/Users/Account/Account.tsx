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
import { useGetAccountTransactionQuery } from '../api/accountsApi';

const b = block('user-list');
const { Content } = Layout;

const Account: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    const { isLoading: isLoadingTransactions, data: dataTransactions } = useGetAccountTransactionQuery('1fa581fa-b0ea-4409-b799-dfad1abf02f5');

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Данные по счету №1233345465' });
    }, [ setConfig ]);

    /**
    * подготовка отображения в таблице не изменяя данных
    */
    const prepareTableData = columnsTransaction;

    // const columnsAction = [
    //     {
    //         key: 'action',
    //         title: '',
    //         dataIndex: 'action',
    //         width: '160px',
    //         className: 'actions',
    //     },
    // ];

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
                <AccountBlockInfo account={AccountMock[0]}/>
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
