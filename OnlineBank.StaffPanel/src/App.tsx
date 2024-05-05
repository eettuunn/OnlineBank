import React, { Suspense, useEffect } from 'react';
import { Navigate, useRoutes } from 'react-router-dom';
import { Spin } from 'antd';
import { onMessage } from 'firebase/messaging';

import { rootRoutes } from './Routes';
import useEventBus from './shared/hooks/useEventBus/useEventBus';
import { useAuth } from './shared/hooks/useAuth/useAuth';
import { askForNotification, messaging } from './firebase';
import eventEmitter from './shared/helpers/eventEmmiter';
import { useGetCoreMonitoringQuery, useGetUserMonitoringQuery, useGetLoanMonitoringQuery } from './shared/api/monitoringApi';
import useLocalStorage from './shared/hooks/useLocalStorage/useLocalStorage';

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
    useEffect(() => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        askForNotification();
        // eslint-disable-next-line @typescript-eslint/no-unsafe-argument
        onMessage(messaging, (payload) => {
            eventEmitter.emit(payload.notification?.title as string, payload.notification?.body);
        });
    }, []);

    const { isAuth } = useAuth();
    const customRouter = useRoutes(createRoutes(isAuth));
    const { data: coreErrorPercentData } = useGetCoreMonitoringQuery(undefined,  { pollingInterval: 10000 });
    const { data: userErrorPercentData } = useGetUserMonitoringQuery(undefined, { pollingInterval: 10000 });
    const { data: loanErrorPercentData } = useGetLoanMonitoringQuery(undefined, { pollingInterval: 10000 });
    const [ , setCoreErrorPercent ] = useLocalStorage('coreErrorPercent', 0);
    const [ , setUserErrorPercent ] = useLocalStorage('userErrorPercent', 0);
    const [ , setLoanErrorPercent ] = useLocalStorage('loanErrorPercent', 0);

    useEffect(() => {
        setCoreErrorPercent(coreErrorPercentData?.errorsPercent);
    }, [ coreErrorPercentData, setCoreErrorPercent ]);

    useEffect(() => {
        setUserErrorPercent(userErrorPercentData?.errorsPercent);
    }, [ userErrorPercentData, setUserErrorPercent ]);

    useEffect(() => {
        setLoanErrorPercent(loanErrorPercentData?.errorsPercent);
    }, [ loanErrorPercentData, setLoanErrorPercent ]);

    return <Suspense fallback={<Spin />}>{customRouter}</Suspense>;
};

export default App;
