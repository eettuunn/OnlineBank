import React, { PropsWithChildren } from 'react';
import { Layout, Spin, Typography } from 'antd';
import block from 'bem-cn';

import { useLayoutConfig } from '../../shared/hooks/useLayoutConfig/useLayoutConfig';

import './MainHeader.scss';

const { Title } = Typography;
const { Header } = Layout;

const b = block('main-header');

const MainHeader: React.FC = ({ children }: PropsWithChildren<unknown>) => {
    const { headerTitle, backButton, centerComponent, isLoading } = useLayoutConfig();
    return (
        <Header style={{ height: 'auto', minWidth: 625 }}>
            <Spin spinning={isLoading}>
                {backButton}
                <Title className={b('title').toString()}>{headerTitle || ''}</Title>
                {centerComponent}
                {children}
            </Spin>
        </Header>
    );
};

export default MainHeader;
