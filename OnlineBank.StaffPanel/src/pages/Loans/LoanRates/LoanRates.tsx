import React, { useCallback, useEffect, useState } from 'react';
import block from 'bem-cn';
import { Button, Card, Layout, List } from 'antd';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { LoanRatesMock } from '../__mocks';
import { useCreateLoanRateMutation, useGetLoanRatesQuery } from '../api/loansApi';
import ModalCreateRate from '../components/ModalCreateRate/ModalCreateRate';

import './LoanRates.scss';
import { ICreateLoanRate } from '../api/types';

const b = block('loan-rates');
const { Content } = Layout;

const LoanRates: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const [ visible, setVisible ] = useState(false);

    const { isLoading: isLoadingRates, data: dataRates } = useGetLoanRatesQuery(undefined);
    const [ create, { isLoading: isLoadingCreate, data: newLoanRateData } ] = useCreateLoanRateMutation();

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Loans, headerTitle: 'Кредитные тарифы' });
    }, [ setConfig ]);

    const onCreateLoanRate = useCallback(
        async (values: ICreateLoanRate) => {
            const result = await create({
                name: values.name,
                interestRate: values.interestRate,
            });
            return result;
        },
        [ create ],
    );

    return (
        <div className={b().toString()}>
            <MainHeader>
                <Button type="primary" onClick={() => setVisible(true)}>Добавить тариф</Button>
            </MainHeader>
            <Content>
                <List
                    dataSource={dataRates}
                    grid={{ gutter: 16, column: 4 }}
                    renderItem={item => (
                        <List.Item>
                            <Card title={item.name}>Процентная ставка: {item.interestRate}</Card>
                        </List.Item>
                    )}
                />
                <ModalCreateRate isLoading={isLoadingCreate} modal={{
                    visible: visible,
                    setVisible: setVisible,
                }} onSave={onCreateLoanRate}/>
            </Content>
        </div>
    );
};

export default LoanRates;
