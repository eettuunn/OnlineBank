import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';

import { IAccount, ITransaction } from './types';

export const accountsApi = createApi({
    reducerPath: 'accountsApi',
    baseQuery: axiosBaseQuery('/core-service/api'),
    tagTypes: [ 'Accounts', 'Transactions' ],
    endpoints: builder => ({
        getUserAccounts: builder.query<IAccount[], string>({
            query: id => ({
                url: `/bank-accounts/owner/${id}/`,
                method: 'get',
            }),
            providesTags: [ 'Accounts' ],
            keepUnusedDataFor: 0,
        }),
        getAccountTransaction: builder.query<ITransaction[], string>({
            query: id => ({
                url: `/transactions/bank-account/${id}`,
                method: 'get',
            }),
            providesTags: [ 'Transactions' ],
            keepUnusedDataFor: 0,
        }),
    }),
});

export const { useGetUserAccountsQuery, useGetAccountTransactionQuery } = accountsApi;
