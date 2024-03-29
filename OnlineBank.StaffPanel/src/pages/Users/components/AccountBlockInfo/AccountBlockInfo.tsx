import React from 'react';
import { Col, Row } from 'antd';
import block from 'bem-cn';

import './AccountBlockInfo.scss';
import UserBlockInfoText from '../UserBlockInfoText/UserBlockInfoText';
import { IAccount } from '../../api/types';
import { dateParse } from '../../../../shared/helpers/dateParse';
import { Status } from '../../constants';

const b = block('account-block-info');

interface IProps {
    account: IAccount | undefined;
}

const AccountBlockInfo: React.FC<IProps> = ({ account }) => (
    <div
        className={b().toString()}
    >
        <Row className={b('inner').toString()}>
            <Col span={5}>
                <UserBlockInfoText label="Наименование" value={account?.name} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Валюта" value={<span style={{ fontWeight: '500' }}>{account?.balance?.currency}</span>} />
            </Col>
            <Col span={5}>
                <UserBlockInfoText label="Дата создания" value={dateParse(account?.creationDate as unknown as string)} />
            </Col>
            <Col span={5}>
                <UserBlockInfoText label="Статус" value={!account?.isClosed ? <span style={{ color: '#5E8C4E', fontWeight: '500' }}>{Status.Active}</span> : <span style={{ color: '#EB5757', fontWeight: '500' }}>{Status.Inactive}</span>} />
            </Col>
            <Col span={5}>
                <UserBlockInfoText label="Баланс" value={<>{account?.balance?.amount} <span style={{ fontWeight: '500' }}>{account?.balance?.currency as unknown as string}</span></>} />
            </Col>
        </Row>
    </div>
);

export default AccountBlockInfo;
