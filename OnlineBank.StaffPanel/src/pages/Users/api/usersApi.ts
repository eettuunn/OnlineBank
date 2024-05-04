import { createApi, retry } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IRequestParams } from '../../../shared/types';

import { ICreateUser, IUser, IUserInfo } from './types';

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: retry(axiosBaseQuery('/user_api')),
    tagTypes: [ 'Users' ],
    endpoints: builder => ({
        getUsers: builder.query<IUser[], IRequestParams>({
            query: params => ({
                url: '/user',
                method: 'get',
                params,
            }),
            providesTags: [ 'Users' ],
        }),
        getUserInfo: builder.query<IUserInfo, string>({
            query: id => ({
                url: `/user/${id}`,
                method: 'get',
            }),
        }),
        getUserLoanRate: builder.query<number, string>({
            query: id => ({
                url: `/user/${id}/loan_rating`,
                method: 'get',
            }),
        }),
        blockUser: builder.mutation<IUser, number>({
            query: id => ({
                url: `/user/${id}/ban`,
                method: 'put',
            }),
            invalidatesTags: [ 'Users' ],
        }),
        unblockUser: builder.mutation<IUser, number>({
            query: id => ({
                url: `/user/${id}/unban`,
                method: 'put',
            }),
            invalidatesTags: [ 'Users' ],
        }),
        createUser: builder.mutation<string, ICreateUser>({
            query: data => ({
                url: '/user',
                method: 'post',
                data,
            }),
            invalidatesTags: [ 'Users' ],
        }),
    }),
});

export const { useGetUsersQuery, useBlockUserMutation, useCreateUserMutation, useUnblockUserMutation, useGetUserInfoQuery, useGetUserLoanRateQuery } = usersApi;
