import React from 'react';
import block from 'bem-cn';

import './UserBlockInfoText.scss';

const b = block('user-block-info-text');

interface IProps {
    label: string | JSX.Element;
    value?: string | JSX.Element | null;
}

const UserBlockInfoText: React.FC<IProps> = ({ label, value }) => (
    <div className={b().toString()}>
        <span className={b('label').toString()}>{label}</span>
        <span className={b('value').toString()}>{value}</span>
    </div>
);

export default UserBlockInfoText;
