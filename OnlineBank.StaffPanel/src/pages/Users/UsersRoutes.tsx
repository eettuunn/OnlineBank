import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const UserList = React.lazy(() => import('./UserList/UserList'));
const User = React.lazy(() => import('./User/User'));

export const UserRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <UserList />
            </Suspense>
        ),
        title: '',
    },
    {
        path: ':userId',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <User/>
            </Suspense>
        ),
    },
    {
        path: ':userId/account/:accountId',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <div>Операции по счету</div>
            </Suspense>
        ),
    },
];
