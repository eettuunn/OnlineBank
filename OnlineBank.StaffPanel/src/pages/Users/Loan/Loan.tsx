/* eslint-disable react/no-multi-comp */
import React, { useEffect } from 'react';
import block from 'bem-cn';
import { Button, Layout } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import Title from 'antd/lib/typography/Title';
import { ArrowLeftOutlined } from '@ant-design/icons';

import BaseTable from '../../../features/BaseTable/BaseTable';
import { Paths } from '../../../shared/constants';
import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { columnsLoanPayments } from '../constants';
import { dateParse } from '../../../shared/helpers/dateParse';
import { useGetLoanInfoQuery } from '../../Loans/api/loansApi';
import MainHeader from '../../../features/MainHeader/MainHeader';
import LoanBlockInfo from '../components/LoanBlockInfo/LoanBlockInfo';

const b = block('account');
const { Content } = Layout;

const Loan: React.FC = () => {
    const { setConfig } = useLayoutConfig();
    const navigate = useNavigate();
    const { loanId, userId } = useParams();

    const { data: loanData, isLoading } = useGetLoanInfoQuery(loanId as string);

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Users, headerTitle: `Кредит от ${dateParse(loanData?.loanInfo.startDate as string)}`,
            backButton: (
                <div className={b('block-back-button').toString()}>
                    <Button
                        className={b('back-button').toString()}
                        type="link"
                        onClick={() => {
                            navigate(`/users/${userId as string}`);
                        }}
                    >
                        <ArrowLeftOutlined /> К заемщику
                    </Button>
                </div>
            ) });
    }, [ loanData?.loanInfo.startDate, navigate, userId ]);

    const prepareTableData = columnsLoanPayments.map((el) => {
        if (el.key === 'paymentDate') {
            return {
                ...el,
                width: '300px',
                render: (value: any, record: Record<string, unknown>) => <span>{dateParse(record.paymentDate as string)}</span>,
            };

        } else if (el.key === 'isExpired') {
            return {
                ...el,
                width: '100px',
                render: (value: any, record: Record<string, boolean>) =>
                    !record.isExpired ? <span style={{ color: '#5E8C4E', fontWeight: '500' }}>-</span> : <span style={{ color: '#EB5757', fontWeight: '500' }}>Просрочен</span>,
            };
        } else if (el.key === 'debt') {
            return {
                ...el,
                width: '100px',
                render: (value: any, record: Record<string, number>) => record.debt.toFixed(2),
            };
        } else return el;
    });

    const newCloumns = [ ...prepareTableData ];

    return (
        <div className={b().toString()}>
            <MainHeader>
                <LoanBlockInfo loan={loanData?.loanInfo}/>
            </MainHeader>
            <Content>
                <Title level={3}>Операции</Title>
                <BaseTable
                    columns={newCloumns}
                    dataSource={loanData?.loanPayments as Record<any, any>[]}
                    isLoading={isLoading}
                />
            </Content>
        </div>
    );
};

export default Loan;
