import { createApi, retry } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IResponseLists } from '../../../shared/types';

import { ICreateLoanRate, ILoan, ILoanRate, ILoanResponse } from './types';

export const loansApi = createApi({
    reducerPath: 'loansApi',
    baseQuery: retry(axiosBaseQuery('/loan_api')),
    tagTypes: [ 'LoanRates' ],
    endpoints: builder => ({
        getUsersLoans: builder.query<IResponseLists<ILoan>, string>({
            query: id => ({
                url: `/loan/${id}`,
                method: 'get',
            }),
        }),
        getLoanRates: builder.query<ILoanRate[], unknown>({
            query: () => ({
                url: '/rate',
                method: 'get',
            }),
            providesTags: [ 'LoanRates' ],
        }),
        createLoanRate: builder.mutation<string, { data: ICreateLoanRate, idempotency_key: string }>({
            query: ({ data, idempotency_key }) => ({
                url: '/rate',
                method: 'post',
                data,
                headers: { 'Idempotency-Key': idempotency_key },
            }),
            invalidatesTags: [ 'LoanRates' ],
        }),
        getLoanInfo: builder.query<ILoanResponse, string>({
            query: id => ({
                url: `/loan/info/${id}`,
                method: 'get',
            }),
        }),
    }),
});

export const { useGetUsersLoansQuery, useGetLoanRatesQuery, useCreateLoanRateMutation, useGetLoanInfoQuery } = loansApi;
