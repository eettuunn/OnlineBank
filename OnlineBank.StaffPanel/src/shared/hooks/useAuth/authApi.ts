import { createApi, retry } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../api/query';

import { ICredentials } from './types';

/**
 * Api для авторизации
 */
export const authApi = createApi({
    reducerPath: 'authApi',
    baseQuery: retry(axiosBaseQuery('/user_api/user')),
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
    }),
});

export const { useLoginMutation } = authApi;
