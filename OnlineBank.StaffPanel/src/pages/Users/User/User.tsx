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
import { UsersMock } from '../__mocks';
import { columnsAccounts } from '../constants';
import './User.scss';

const b = block('user-list');
const { Content } = Layout;

const User: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();

    const [ indexRow, setIndexRow ] = useState<undefined | number>(undefined);

    // const { isLoading, data: dataEnginesWithTestsQuery, isError: isErrorEngine, refetch } = useGetEnginesWithTestsQuery({ search, ...pagination });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: 'Информация о пользователе' });
    }, [ setConfig ]);

    /**
    * подготовка отображения в таблице не изменяя данных
    */
    const prepareTableData = columnsAccounts;

    const columnsAction = [
        {
            key: 'action',
            title: '',
            dataIndex: 'action',
            width: '160px',
            className: 'actions',
        },
    ];

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

    const newCloumns = [ ...prepareTableData, ...columnsAction ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <UserBlockInfo user={UsersMock[0]}/>
            </MainHeader>
            <Content>
                <Row gutter={10}>
                    <Col span={12}>
                        <Title level={3}>Счета</Title>
                        <BaseTable
                            cursorPointer
                            columns={newCloumns}
                            // isLoading={isLoadingUsers || isFetchingUsers}
                            dataSource={UsersMock as Record<any, any>[]}
                            onRow={onRow}
                        />
                    </Col>
                    <Col span={12}>
                        <Title level={3}>Кредиты</Title>
                        <BaseTable
                            cursorPointer
                            columns={newCloumns}
                            // isLoading={isLoadingUsers || isFetchingUsers}
                            dataSource={UsersMock as Record<any, any>[]}
                            onRow={onRow}
                        />
                    </Col>
                </Row>
            </Content>
        </div>
    );
};

export default User;
