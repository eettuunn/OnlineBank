import React, { useState, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons';
import block from 'bem-cn';
import { collection, getDocs } from 'firebase/firestore';

import MainMenu from '../features/MainMenu/MainMenu';
import { rootRoutes } from '../Routes';
import UserBlock from '../features/UserBlock/UserBlock';
import { SiderIcon } from '../shared/img/menuicons/SiderIcon';
import { ThemeIcon } from '../shared/img/ThemeIcon';
import useLocalStorage from '../shared/hooks/useLocalStorage/useLocalStorage';
import { db } from '../firebaseConfig';

import './MainLayout.scss';

const b = block('app');
const l = block('logo');
const { Content, Sider } = Layout;

const MainLayout: React.FC = () => {
    const [ collapsed, setCollapsed ] = useState(false);

    const [ darkTheme, setDarkTheme ] = useLocalStorage('dark-theme', false);

    const fetchPost = async () => {

        await getDocs(collection(db, 'themes'))
            .then((querySnapshot)=>{
                const newData = querySnapshot.docs
                    .map(doc => ({ ...doc.data(), id:doc.id }));
                // setTodos(newData);
                console.log(newData);
            });

    };

    useEffect(()=>{
        fetchPost();
    }, []);

    useEffect(() => {
        if (darkTheme) {
            document.body.classList.remove('light');
            document.body.classList.add('dark');
        } else {
            document.body.classList.remove('dark');
            document.body.classList.add('light');
        }
    }, [ darkTheme ]);

    return (
        <Layout>
            <Sider
                collapsible
                reverseArrow
                className={b('sider').toString()}
                collapsed={collapsed}
                collapsedWidth={88}
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
