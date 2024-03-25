import React from 'react';
import { Col, Row } from 'antd';
import block from 'bem-cn';

import './LoanBlockInfo.scss';
import UserBlockInfoText from '../UserBlockInfoText/UserBlockInfoText';
import { dateParse } from '../../../../shared/helpers/dateParse';
import { ILoan } from '../../../Loans/api/types';

const b = block('loan-block-info');

interface IProps {
    loan: ILoan | undefined;
}

const LoanBlockInfo: React.FC<IProps> = ({ loan }) => (
    <div
        className={b().toString()}
    >
        <Row className={b('inner').toString()}>
            <Col span={4}>
                <UserBlockInfoText label="Дата начала" value={dateParse(loan?.startDate as unknown as string)} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Дата окончания" value={dateParse(loan?.endDate as unknown as string)} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Долг" value={<span style={{ fontWeight: '500' }}>{loan?.debt.toFixed(2)}</span>} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Сумма ежемесячного платежа" value={loan?.monthlyPayment.toFixed(2) as unknown as string} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Кредитный тариф" value={loan?.loanRateName} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Процентная ставка" value={loan?.interestRate as unknown as string} />
            </Col>
        </Row>
    </div>
);

export default LoanBlockInfo;
