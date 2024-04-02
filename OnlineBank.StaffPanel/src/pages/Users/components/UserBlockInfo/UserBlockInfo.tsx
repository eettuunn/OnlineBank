import React from 'react';
import { Col, Row } from 'antd';
import block from 'bem-cn';

import './UserBlockInfo.scss';
import UserBlockInfoText from '../UserBlockInfoText/UserBlockInfoText';
import { IUserInfo } from '../../api/types';
import { Role, RoleRus, Status } from '../../constants';

const b = block('user-block-info');

interface IProps {
    user: IUserInfo | undefined;
}

const UserBlockInfo: React.FC<IProps> = ({ user }) => (
    <div
        className={b().toString()}
    >
        <Row className={b('inner').toString()}>
            <Col span={4}>
                <UserBlockInfoText label="Полное имя" value={user?.userName} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Email" value={user?.email} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Паспорт" value={user?.passport} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Номер телефона" value={user?.phoneNumber} />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Роль" value={
                    // eslint-disable-next-line react/jsx-no-useless-fragment
                    <>
                        {user?.roles.map(role =>
                            <span className={b(`role-span ${role === Role.staff ? 'staff' : ''}`).toString()} key={role}>{RoleRus[role ]}</span>,
                        )}
                    </>
                } />
            </Col>
            <Col span={4}>
                <UserBlockInfoText label="Статус" value={!user?.ban ? <span style={{ color: '#5E8C4E', fontWeight: '500' }}>{Status.Active}</span> : <span style={{ color: '#EB5757', fontWeight: '500' }}>{Status.Inactive}</span>} />
            </Col>
        </Row>
    </div>
);

export default UserBlockInfo;
