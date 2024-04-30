import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const LogList = React.lazy(() => import('./LogList/LogList'));

export const LogRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <LogList />
            </Suspense>
        ),
        title: '',
    },
];
