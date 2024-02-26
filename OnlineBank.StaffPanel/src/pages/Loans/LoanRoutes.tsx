import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

export const LoanRoutes: IRoute[] = [
    {
        path: '',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <div>Кредитные тарифы</div>
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
