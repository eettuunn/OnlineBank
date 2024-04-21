import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';
import { IRequestParams } from '../../../shared/types';

import { IUser } from './types';

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: axiosBaseQuery('/user_api'),
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
    }),
});

export const { useGetUsersQuery } = usersApi;
