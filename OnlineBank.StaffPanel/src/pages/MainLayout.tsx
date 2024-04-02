/* eslint-disable @typescript-eslint/no-unsafe-argument */
import React, { useState, useEffect, useCallback } from 'react';
import { Outlet } from 'react-router-dom';
import { Button, Layout } from 'antd';
import Icon from '@ant-design/icons';
import block from 'bem-cn';
import { setDoc, doc, getDoc } from 'firebase/firestore';

import MainMenu from '../features/MainMenu/MainMenu';
import { rootRoutes } from '../Routes';
import UserBlock from '../features/UserBlock/UserBlock';
import { SiderIcon } from '../shared/img/menuicons/SiderIcon';
import { ThemeIcon } from '../shared/img/ThemeIcon';
import useLocalStorage from '../shared/hooks/useLocalStorage/useLocalStorage';
import { useAuth } from '../shared/hooks/useAuth/useAuth';
import { db } from '../firebase';
import './MainLayout.scss';

const b = block('app');
const l = block('logo');
const { Content, Sider } = Layout;
// const preferencesRef = collection(db, 'preferences');

async function firebaseGetData(id: string) {

    const docRef = doc(db, 'preferences', id);
    const docSnap = await getDoc(docRef);

    if (docSnap.exists()) {
        console.log('Document data:', docSnap.data());
        return docSnap.data().theme as string;
    } else {
        // docSnap.data() will be undefined in this case
        console.log('No such document!');
        return '';
    }
}

async function firebaseSetData(isDarkTheme: boolean, id: string) {
    try {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
        await setDoc(doc(db, 'preferences', id), {
            theme: isDarkTheme,
            id: id,
        });
        console.log('Document written');
    } catch (e) {
        console.error('Error adding document: ', e);
    }
}

const MainLayout: React.FC = () => {
    const [ collapsed, setCollapsed ] = useState(false);

    const [ darkTheme, setDarkTheme ] = useLocalStorage('dark-theme', false);
    const { id } = useAuth();

    useEffect(() => {
        firebaseGetData(id).then(e =>
            e ? setDarkTheme(true) : setDarkTheme(false),
        );
    }, [ id, setDarkTheme ]);

    useEffect(() => {
        if (darkTheme) {
            document.body.classList.remove('light');
            document.body.classList.add('dark');
        } else {
            document.body.classList.remove('dark');
            document.body.classList.add('light');
        }
    }, [ darkTheme, id ]);

    const onChangeTheme = useCallback((isDarkMode: boolean) => {
        setDarkTheme(isDarkMode);
        firebaseSetData(isDarkMode, id);
    }, [ id, setDarkTheme ]);

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
                    <Button icon={<Icon component={ThemeIcon} />} shape="circle" type='primary' onClick={() => onChangeTheme(!darkTheme)}/>
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
