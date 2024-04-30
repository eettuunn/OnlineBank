import { configureStore } from '@reduxjs/toolkit';

import { monitoringApi } from '../pages/Users/api/logApi';
import { authApi } from '../shared/hooks/useAuth/authApi';

import paginationSlice from './reducers/pagination.slice';

const mw = [ monitoringApi.middleware, authApi.middleware ];
export const store = configureStore({
    reducer: {
        [monitoringApi.reducerPath]: monitoringApi.reducer,
        [authApi.reducerPath]: authApi.reducer,
        pagination: paginationSlice,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
