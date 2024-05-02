import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const Chart = React.lazy(() => import('./Chart/Chart'));

export const ChartRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <Chart />
            </Suspense>
        ),
        title: '',
    },
];
