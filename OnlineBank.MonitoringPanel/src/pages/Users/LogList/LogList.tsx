import React, { useEffect, useState } from 'react';
import block from 'bem-cn';
import { Button, Layout, Tabs, TabsProps, Typography } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import BaseTable from '../../../features/BaseTable/BaseTable';
import { useGetLogQuery } from '../api/logApi';
import { ApiName } from '../api/types';
import { columnsLog } from '../constants';

import './LogList.scss';

const { Title } = Typography;
const b = block('user-list');
const { Content } = Layout;

const LogList: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const [ apiName, setApiName ] = useState('CoreService');

    const { isFetching: isFetchingCore, data: dataCore, refetch: refetchCore } = useGetLogQuery({ apiName: ApiName.CoreService });
    const { isFetching: isFetchingUser, data: dataUser, refetch: refetchUser } = useGetLogQuery({ apiName: ApiName.UserService });
    const { isFetching: isFetchingLoan, data: dataLoan, refetch: refetchLoan } = useGetLogQuery({ apiName: ApiName.LoanService });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Log, headerTitle: 'Мониторинг' });
    }, [ setConfig ]);

    const prepareTableData = columnsLog.map((el) => {
        if (el.key === 'roles') {
            return {
                ...el,
                width: '300px',
                render: (value: any, record: Record<string, []>) => record.roles.map(role =>
                    <span className={b(`role-span ${role === 'staff' ? 'staff' : ''}`).toString()} key={role}>{role}</span>,
                ),
            };
        } else return el;
    });

    const newCloumns = prepareTableData;

    const items: TabsProps['items'] = [
        {
            key: ApiName.CoreService,
            label: 'Core service',
            children: <>
                <Title level={4}>Процент ошибок: {dataCore?.errorsPercent}</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={dataCore?.requests as unknown as Record<string, string>[]}
                    isLoading={isFetchingCore}
                /></>,
        },
        {
            key: ApiName.LoanService,
            label: 'Loan service',
            children: <>
                <Title level={4}>Процент ошибок: {dataLoan?.errorsPercent}</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={dataLoan?.requests as unknown as Record<string, string>[]}
                    isLoading={isFetchingLoan}
                />
            </>,
        },
        {
            key: ApiName.UserService,
            label: 'User service',
            children: <>
                <Title level={4}>Процент ошибок: {dataUser?.errorsPercent}</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={dataUser?.requests as unknown as Record<string, string>[]}
                    isLoading={isFetchingUser}
                /></>,
        },
    ];

    const onClickRefetch = () => {
        switch (apiName) {
            case ApiName.CoreService:
                refetchCore();
                break;
            case ApiName.UserService:
                refetchUser();
                break;
            case ApiName.LoanService:
                refetchLoan();
                break;
            default:
                break;
        }
    };

    return (
        <div className={b().toString()}>
            <MainHeader />
            <Content>
                <Tabs
                    defaultActiveKey={ApiName.CoreService}
                    items={items}
                    tabBarExtraContent={<Button icon={<ReloadOutlined/>} onClick={onClickRefetch}/>}
                    onChange={key => setApiName(key)}/>
            </Content>
        </div>
    );
};

export default LogList;
