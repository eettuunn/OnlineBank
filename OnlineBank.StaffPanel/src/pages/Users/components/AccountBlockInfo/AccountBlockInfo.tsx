import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Col, Row } from 'antd';
import block from 'bem-cn';

import './AccountBlockInfo.scss';
import UserBlockInfoText from '../UserBlockInfoText/UserBlockInfoText';
import { IAccount } from '../../api/types';

const b = block('account-block-info');

interface IProps {
    account: IAccount | undefined;
}

const AccountBlockInfo: React.FC<IProps> = ({ account }) => (
    <div
        className={b().toString()}
    >
        <Row className={b('inner').toString()}>
            <Col span={6}>
                <UserBlockInfoText label="" value={account?.name} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Дата создания" value={account?.creationDate as unknown as string} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Статус" value={account?.isClosed ? 'Закрыт' : 'Активен'} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Баланс" value={`${account?.balance as unknown as string} ₽`} />
            </Col>
        </Row>
    </div>
);

export default AccountBlockInfo;
