import { configureStore } from '@reduxjs/toolkit';

import { usersApi } from '../pages/Users/api/usersApi';
import { accountsApi } from '../pages/Users/api/accountsApi';

const mw = [ usersApi.middleware, accountsApi.middleware ];
export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
        [accountsApi.reducerPath]: accountsApi.reducer,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
