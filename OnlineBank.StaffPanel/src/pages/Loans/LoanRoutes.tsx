import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const LoanRates = React.lazy(() => import('./LoanRates/LoanRates'));

export const LoanRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <LoanRates/>
            </Suspense>
        ),
        title: '',
    },
];
