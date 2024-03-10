import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IResponseLists } from '../../../shared/types';

import { ICreateLoanRate, ILoan, ILoanRate } from './types';

export const loansApi = createApi({
    reducerPath: 'loansApi',
    baseQuery: axiosBaseQuery('/loan_api'),
    tagTypes: [ 'Loans', 'LoanRates' ],
    endpoints: builder => ({
        getUsersLoans: builder.query<IResponseLists<ILoan>, string>({
            query: id => ({
                url: `/loan/${id}`,
                method: 'get',
            }),
            providesTags: [ 'Loans' ],
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
    }),
});

export const { useGetUsersLoansQuery, useGetLoanRatesQuery, useCreateLoanRateMutation } = loansApi;
