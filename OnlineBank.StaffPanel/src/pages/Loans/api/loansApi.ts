import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IResponseLists } from '../../../shared/types';

import { ILoan } from './types';

export const loansApi = createApi({
    reducerPath: 'loansApi',
    baseQuery: axiosBaseQuery('/loan_api'),
    tagTypes: [ 'Loans' ],
    endpoints: builder => ({
        getUsersLoans: builder.query<IResponseLists<ILoan>, string>({
            query: id => ({
                url: `/loan/${id}`,
                method: 'get',
            }),
            providesTags: [ 'Loans' ],
        }),
    }),
});

export const { useGetUsersLoansQuery } = loansApi;
