import React, { Suspense, useEffect } from 'react';
import { Navigate, useRoutes } from 'react-router-dom';
import { Spin } from 'antd';
import { onMessage } from 'firebase/messaging';

import { rootRoutes } from './Routes';
import useEventBus from './shared/hooks/useEventBus/useEventBus';
import { useAuth } from './shared/hooks/useAuth/useAuth';
import { askForNotification, messaging } from './firebase';
import eventEmitter from './shared/helpers/eventEmmiter';

const Login = React.lazy(() => import('./pages/Login/Login'));
const NotFound = React.lazy(() => import('./pages/NotFound/NotFound'));
const MainLayout = React.lazy(() => import('./pages/MainLayout'));

const createRoutes = (isAuth: boolean) => [
    {
        path: '/',
        element:
        isAuth ? (
            <Suspense fallback={<Spin className="main-loader" />}>
                <MainLayout />
            </Suspense>
        ) : <Navigate to='/login'/>,
        children: rootRoutes,
    },
    {
        path: '/login',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <Login />
            </Suspense>
        ),
    },
    {
        path: '*',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <NotFound />
            </Suspense>
        ),
    },
];

const App: React.FC = () => {
    useEventBus();

    const { isAuth } = useAuth();
    const customRouter = useRoutes(createRoutes(isAuth));

    return <Suspense fallback={<Spin />}>{customRouter}</Suspense>;
};

export default App;
