import { createApi } from '@reduxjs/toolkit/query/react';

import { axiosBaseQuery } from './query';

export interface IMonitoringData {
    errorsPercent: number
}

export const monitoringApi = createApi({
    reducerPath: 'monitoringApi',
    baseQuery: axiosBaseQuery('/monitoring_api'),
    tagTypes: [ 'coreErrorPercent', 'userErrorPercent', 'loanErrorPercent' ],
    endpoints: builder => ({
        getCoreMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=CoreService',
                method: 'get',
            }),
            providesTags: [ 'coreErrorPercent' ],
        }),
        getUserMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=UserService',
                method: 'get',
            }),
            providesTags: [ 'userErrorPercent' ],
        }),
        getLoanMonitoring: builder.query<IMonitoringData, undefined>({
            query: () => ({
                url: '?apiName=LoanService',
                method: 'get',
            }),
            providesTags: [ 'loanErrorPercent' ],
        }),
    }),
});

export const { useGetCoreMonitoringQuery, useGetLoanMonitoringQuery, useGetUserMonitoringQuery } = monitoringApi;
