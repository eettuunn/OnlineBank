import React, { Suspense } from 'react';
import { Spin } from 'antd';

import { IRoute } from '../../shared/types';

const Account = React.lazy(() => import('./Account/Account'));
const UserList = React.lazy(() => import('./UserList/UserList'));
const User = React.lazy(() => import('./User/User'));
const Loan = React.lazy(() => import('./Loan/Loan'));

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
                <Account/>
            </Suspense>
        ),
    },
    {
        path: ':userId/loan/:loanId',
        element: (
            <Suspense fallback={<Spin className="main-loader" />}>
                <Loan/>
            </Suspense>
        ),
    },
];
