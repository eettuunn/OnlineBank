import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons';
import block from 'bem-cn';

import MainMenu from '../features/MainMenu/MainMenu';
import { rootRoutes } from '../Routes';
import UserBlock from '../features/UserBlock/UserBlock';

import './MainLayout.scss';

const b = block('app');
const l = block('logo');
const { Content, Sider } = Layout;

const MainLayout: React.FC = () => {
    const [ collapsed, setCollapsed ] = useState(false);

    return (
        <Layout>
            <Sider
                collapsible
                reverseArrow
                className={b('sider').toString()}
                collapsed={collapsed}
                collapsedWidth={88}
                theme="light"
                trigger={null}
                width={320}
                onCollapse={setCollapsed}
            >
                <div>
                    <div className={l({ collapsed }).toString()}>
                        <Icon className={l('icon', { collapsed }).toString()}/>
                        <Button
                            className={l('button').toString()}
                            type="text"
                            onClick={() => setCollapsed(!collapsed)}
                        />
                    </div>
                    <UserBlock collapsed={collapsed} />
                    <MainMenu routes={rootRoutes} />
                </div>
            </Sider>
            <Layout className={b({ collapsed }).toString()}>
                <Content className={b('primary-content').toString()}>
                    <div className="h100"><Outlet /></div>
                </Content>
            </Layout>
        </Layout>
    );
};

export default MainLayout;
