import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IResponseLists } from '../../../shared/types';

import { ICreateLoanRate, ILoan, ILoanRate, ILoanResponse } from './types';

export const loansApi = createApi({
    reducerPath: 'loansApi',
    baseQuery: axiosBaseQuery('/loan_api'),
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
        createLoanRate: builder.mutation<string, ICreateLoanRate>({
            query: data => ({
                url: '/rate',
                method: 'post',
                data,
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
