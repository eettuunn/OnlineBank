import React from 'react';
import { Avatar, Button, Tooltip, Typography } from 'antd';
import block from 'bem-cn';
import Icon from '@ant-design/icons/lib/components/Icon';

import { LogoutIcon } from '../../shared/img/menuicons/LogoutIcon';

import './UserBlock.scss';

const { Text } = Typography;
const b = block('user-block');

interface IProps {
    collapsed: boolean;
}

const UserBlock: React.FC<IProps> = ({ collapsed }) => (
    <div className={b()}>
        <div>
            <Tooltip placement="right" title={collapsed ? 'Пользователь' : ''}>
                <Avatar size={40}>{}</Avatar>
            </Tooltip>
        </div>
        <div className={b('info', { collapsed }).toString()}>
            <Text className={b('info-name').toString()}>Пользователь</Text>
        </div>
        <Button
            className={b('button', { collapsed }).toString()}
            icon={<Icon component={LogoutIcon} style={{ fontSize: 20 }} />}
            type="text"
            onClick={() => {
                localStorage.removeItem('access');
                location.reload();
            }}
        />
    </div>
);

export default UserBlock;
