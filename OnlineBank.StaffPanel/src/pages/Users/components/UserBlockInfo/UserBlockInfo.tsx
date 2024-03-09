import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Col, Row } from 'antd';
import block from 'bem-cn';

import './UserBlockInfo.scss';
import UserBlockInfoText from '../UserBlockInfoText/UserBlockInfoText';
import { IUser } from '../../api/types';

const b = block('user-block-info');

interface IProps {
    user: IUser | undefined;
}

const UserBlockInfo: React.FC<IProps> = ({ user }) => (
    <div
        className={b().toString()}
    >
        <Row className={b('inner').toString()}>
            <Col span={6}>
                <UserBlockInfoText label="Полное имя" value={user?.userName} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Email" value={user?.email} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Роль" value={user?.roles[0]} />
            </Col>
            <Col span={6}>
                <UserBlockInfoText label="Статус" value={user?.ban ? 'Заблокирован' : 'Активный'} />
            </Col>
        </Row>
    </div>
);

export default UserBlockInfo;
