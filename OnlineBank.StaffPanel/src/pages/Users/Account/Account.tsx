import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Col, Layout, Row } from 'antd';
import { useNavigate } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';

import UserBlockInfo from '../components/UserBlockInfo/UserBlockInfo';
import BaseTable from '../../../features/BaseTable/BaseTable';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { AccountMock, TransactionMock, UsersMock } from '../__mocks';
import { columnsAccount, columnsTransaction } from '../constants';
import './Account.scss';
import AccountBlockInfo from '../components/AccountBlockInfo/AccountBlockInfo';

const b = block('user-list');
const { Content } = Layout;

const Account: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    // const { isLoading, data: dataEnginesWithTestsQuery, isError: isErrorEngine, refetch } = useGetEnginesWithTestsQuery({ search, ...pagination });

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
            navigate(`/account/${rowIndex as unknown as string}`);
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
                    // isLoading={isLoadingUsers || isFetchingUsers}
                    dataSource={TransactionMock as Record<any, any>[]}
                    onRow={onRow}
                />
            </Content>
        </div>
    );
};

export default Account;
