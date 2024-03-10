import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../api/query';

import { ICredentials, IGetCurrentUser } from './types';

/**
 * Api для авторизации
 */
export const authApi = createApi({
    reducerPath: 'authApi',
    baseQuery: axiosBaseQuery('/user_api/user'),
    tagTypes: [ 'Auth', 'Me' ],
    endpoints: builder => ({
        login: builder.mutation<{ token: string; id: string }, ICredentials>({
            query: data => ({
                url: '/login',
                method: 'post',
                data,
            }),
            invalidatesTags: [ 'Auth' ],
        }),
        // logout: builder.mutation({
        //     query: ({ url, refresh }: { url: string; refresh: string }) => ({ url, method: 'post', data: `refresh=${refresh}` }),
        // }),
    }),
});

export const { useLoginMutation } = authApi;
