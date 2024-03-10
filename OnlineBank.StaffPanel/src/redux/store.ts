import { configureStore } from '@reduxjs/toolkit';

import { usersApi } from '../pages/Users/api/usersApi';
import { accountsApi } from '../pages/Users/api/accountsApi';
import { loansApi } from '../pages/Loans/api/loansApi';

import paginationSlice from './reducers/pagination.slice';

const mw = [ usersApi.middleware, accountsApi.middleware, loansApi.middleware ];
export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
        [accountsApi.reducerPath]: accountsApi.reducer,
        [loansApi.reducerPath]: loansApi.reducer,
        pagination: paginationSlice,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
