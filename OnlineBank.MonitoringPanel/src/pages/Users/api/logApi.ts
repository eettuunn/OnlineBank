import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from '../../../shared/api/query';

import { ILogResponse, IRequestParams } from './types';

export const monitoringApi = createApi({
    reducerPath: 'monitoringApi',
    baseQuery: axiosBaseQuery('/monitoring_api'),
    tagTypes: [ 'Monitoring' ],
    endpoints: builder => ({
        getLog: builder.query<ILogResponse, IRequestParams>({
            query: params => ({
                url: '/',
                method: 'get',
                params,
            }),
            providesTags: [ 'Monitoring' ],
        }),
    }),
});

export const { useGetLogQuery } = monitoringApi;
