import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';

import { IUser } from './types';

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: axiosBaseQuery(),
    tagTypes: [ 'Users' ],
    endpoints: builder => ({
        getUsers: builder.query<IUser[], undefined>({
            query: () => ({
                url: '/users/',
                method: 'get',
            }),
            providesTags: [ 'Users' ],
            keepUnusedDataFor: 0,
        }),
        blockUser: builder.mutation<IUser, number>({
            query: id => ({
                url: `/users/${id}/lock`,
                method: 'post',
            }),
            invalidatesTags: [ 'Users' ],
        }),
        unblockUser: builder.mutation<IUser, number>({
            query: id => ({
                url: `/users/${id}/lock`,
                method: 'delete',
            }),
            invalidatesTags: [ 'Users' ],
        }),
        createUser: builder.mutation<IUser, string>({
            query: data => ({
                url: '/users/',
                method: 'post',
                data,
            }),
            invalidatesTags: [ 'Users' ],
        }),
    }),
});

export const { useGetUsersQuery, useBlockUserMutation, useCreateUserMutation, useUnblockUserMutation } = usersApi;
