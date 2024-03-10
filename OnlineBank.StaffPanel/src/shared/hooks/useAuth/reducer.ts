import { createSlice } from '@reduxjs/toolkit';

import { Role } from '../../../pages/Users/constants';

import { IGetCurrentUser } from './types';
import { authApi } from './authApi';

interface IInitialState {
    data?: IGetCurrentUser;
    currentRole?: Role[];
    isLoading: boolean;
    isSuccess: boolean;
    isError: boolean;
}

const initialState: IInitialState = {
    data: undefined,
    currentRole: undefined,
    isLoading: true,
    isSuccess: false,
    isError: false,
};

/* reducer */
const authSlice = createSlice({
    name: 'authSlice',
    initialState,
    reducers: {},

    // extraReducers: (builder) => {
    //     builder.addMatcher(authApi.endpoints.getMe.matchFulfilled, (state, action) => {
    //         state.isLoading = false;
    //         state.isSuccess = true;
    //         state.isError = false;
    //         state.data = action.payload;
    //         state.currentRole = action.payload.roles;
    //     });

    //     builder.addMatcher(authApi.endpoints.getMe.matchPending, (state, action) => {
    //         state.isLoading = true;
    //         state.isSuccess = false;
    //         state.isError = false;
    //     });

    //     builder.addMatcher(authApi.endpoints.getMe.matchRejected, (state, action) => {
    //         state.isLoading = false;
    //         state.isSuccess = false;
    //         state.isError = true;
    //     });
    // },
});

export default authSlice.reducer;
