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
    {
        path: 'rates/:rateId',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <div>Тариф</div>
            </Suspense>
        ),
        title: '',
    },
    {
        path: ':userId/:loanId',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <div>кредит пользователя</div>
            </Suspense>
        ),
        title: '',
    },
];
