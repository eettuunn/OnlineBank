import { configureStore } from '@reduxjs/toolkit';

import { usersApi } from '../pages/Users/api/usersApi';

const mw = [ usersApi.middleware ];
export const store = configureStore({
    reducer: {
        [usersApi.reducerPath]: usersApi.reducer,
    },
    middleware: getDefaultMiddleware => getDefaultMiddleware({ serializableCheck: false }).concat(mw),
});
