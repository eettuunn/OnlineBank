import { configureStore } from '@reduxjs/toolkit';

import { usersApi } from '../pages/Users/api/usersApi';
import { authApi } from '../shared/hooks/useAuth/authApi';

import paginationSlice from './reducers/pagination.slice';

const mw = [ usersApi.middleware, authApi.middleware ];
export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
        [authApi.reducerPath]: authApi.reducer,
        pagination: paginationSlice,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
