import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from './query';

export interface IMonitoringData {
    errorsPercent: number
}

export const monitoringApi = createApi({
    reducerPath: 'monitoringApi',
    baseQuery: axiosBaseQuery('/monitoring_api'),
    tagTypes: [ 'CoreMonitoring', 'UserMonitoring', 'LoanMonitoring' ],
    endpoints: builder => ({
        getCoreMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=CoreService',
                method: 'get',
            }),
            providesTags: [ 'CoreMonitoring' ],
        }),
        getUserMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=UserService',
                method: 'get',
            }),
            providesTags: [ 'UserMonitoring' ],
        }),
        getLoanMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=LoanService',
                method: 'get',
            }),
            providesTags: [ 'LoanMonitoring' ],
        }),
    }),
});

export const { useGetCoreMonitoringQuery, useGetLoanMonitoringQuery, useGetUserMonitoringQuery } = monitoringApi;
