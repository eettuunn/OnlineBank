import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const UserList = React.lazy(() => import('./UserList/UserList'));

export const LogRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <UserList />
            </Suspense>
        ),
        title: '',
    },
];
