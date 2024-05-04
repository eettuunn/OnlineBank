import { createApi, retry } from '@reduxjs/toolkit/query/react';

import { IRequestParams, IResponseLists } from '../../../shared/types';
import { axiosBaseQuery } from '../../../shared/api/query';

import { IAccount, IAccountsResponse, ITransaction } from './types';

export const accountsApi = createApi({
    reducerPath: 'accountsApi',
    baseQuery: retry(axiosBaseQuery('/api')),
    tagTypes: [ 'Accounts', 'Transactions', 'Account' ],
    endpoints: builder => ({
        getUserAccounts: builder.query<IAccountsResponse, { id: string, params: IRequestParams }>({
            query: ({ id, params }) => ({
                url: `/bank-accounts/owner/${id}/`,
                method: 'get',
                params,
            }),
            providesTags: [ 'Accounts' ],
        }),
        getAccountTransaction: builder.query<IResponseLists<ITransaction[]>,  { id: string, params: IRequestParams }>({
            query: ({ id, params }) => ({
                url: `/transactions/bank-account/${id}`,
                method: 'get',
                params,
            }),
            providesTags: [ 'Transactions' ],
        }),
        getAccountInfo: builder.query<IAccount, string>({
            query: id => ({
                url: `/bank-accounts/${id}`,
                method: 'get',
            }),
            providesTags: [ 'Account' ],
        }),
    }),
});

export const { useGetUserAccountsQuery, useGetAccountTransactionQuery, useGetAccountInfoQuery } = accountsApi;
