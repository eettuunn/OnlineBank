import { configureStore } from '@reduxjs/toolkit';

import { usersApi } from '../pages/Users/api/usersApi';
import { accountsApi } from '../pages/Users/api/accountsApi';
import { loansApi } from '../pages/Loans/api/loansApi';
import { authApi } from '../shared/hooks/useAuth/authApi';
import { monitoringApi } from '../shared/api/monitoringApi';

import paginationSlice from './reducers/pagination.slice';

const mw = [ usersApi.middleware, accountsApi.middleware, loansApi.middleware, authApi.middleware, monitoringApi.middleware ];
export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
        [accountsApi.reducerPath]: accountsApi.reducer,
        [loansApi.reducerPath]: loansApi.reducer,
        [authApi.reducerPath]: authApi.reducer,
        [monitoringApi.reducerPath]: monitoringApi.reducer,
        pagination: paginationSlice,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
