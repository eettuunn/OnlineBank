import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';

import { IUser } from './types';

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: axiosBaseQuery('/user_api'),
    tagTypes: [ 'Users' ],
    endpoints: builder => ({
        getUsers: builder.query<IUser[], undefined>({
            query: () => ({
                url: '/user',
                method: 'get',
            }),
            providesTags: [ 'Users' ],
            keepUnusedDataFor: 0,
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
        createUser: builder.mutation<string, IUser>({
            query: data => ({
                url: '/user',
                method: 'post',
                data,
            }),
            invalidatesTags: [ 'Users' ],
        }),
    }),
});

export const { useGetUsersQuery, useBlockUserMutation, useCreateUserMutation, useUnblockUserMutation } = usersApi;