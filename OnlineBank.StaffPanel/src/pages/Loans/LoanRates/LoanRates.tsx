import React, { useEffect } from 'react';
import block from 'bem-cn';
import { Button, Card, Layout, List } from 'antd';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { LoanRatesMock } from '../__mocks';

import './LoanRates.scss';

const b = block('loan-rates');
const { Content } = Layout;

const LoanRates: React.FC = () => {
    const { setConfig } = useLayoutConfig();

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Loans, headerTitle: 'Кредитные тарифы' });
    }, [ setConfig ]);

    return (
        <div className={b().toString()}>
            <MainHeader>
                <Button type="primary">Добавить тариф</Button>
            </MainHeader>
            <Content>
                <List
                    dataSource={LoanRatesMock}
                    grid={{ gutter: 16, column: 4 }}
                    renderItem={item => (
                        <List.Item>
                            <Card title={item.name}>Процентная ставка: {item.interestRate}</Card>
                        </List.Item>
                    )}
                />
            </Content>
        </div>
    );
};

export default LoanRates;
