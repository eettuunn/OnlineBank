import React, { Suspense } from 'react';
import { useRoutes } from 'react-router-dom';
import { Spin } from 'antd';

import { rootRoutes } from './Routes';
import useEventBus from './shared/hooks/useEventBus/useEventBus';

const NotFound = React.lazy(() => import('./pages/NotFound/NotFound'));
const MainLayout = React.lazy(() => import('./pages/MainLayout'));

const createRoutes = () => [
    {
        path: '/',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <MainLayout />
            </Suspense>
        ),
        children: rootRoutes,
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

    const customRouter = useRoutes(createRoutes());

    return <Suspense fallback={<Spin />}>{customRouter}</Suspense>;
};

export default App;
