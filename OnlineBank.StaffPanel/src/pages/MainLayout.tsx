import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons';
import block from 'bem-cn';

import MainMenu from '../features/MainMenu/MainMenu';
import { rootRoutes } from '../Routes';
import UserBlock from '../features/UserBlock/UserBlock';
import { SiderIcon } from '../shared/img/menuicons/SiderIcon';
import { ThemeIcon } from '../shared/img/ThemeIcon';
import useLocalStorage from '../shared/hooks/useLocalStorage/useLocalStorage';

import './MainLayout.scss';

const b = block('app');
const l = block('logo');
const { Content, Sider } = Layout;

const MainLayout: React.FC = () => {
    const [ collapsed, setCollapsed ] = useState(false);

    const [ darkTheme, setDarkTheme ] = useLocalStorage('dark-theme', false);

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
                            icon={<Icon component={SiderIcon} style={{ fontSize: 40 }} />}
                            type="text"
                            onClick={() => setCollapsed(!collapsed)}
                        />
                    </div>
                    <UserBlock collapsed={collapsed} />
                    <MainMenu routes={rootRoutes} />
                </div>
                <div className={b('container-information').toString()}>
                    <Button icon={<Icon component={ThemeIcon} />} shape="circle" type='primary' onClick={() => setDarkTheme(!darkTheme)}/>
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
