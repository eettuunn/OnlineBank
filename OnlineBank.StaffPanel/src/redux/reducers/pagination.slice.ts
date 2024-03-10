import { PayloadAction, createSlice } from '@reduxjs/toolkit';

import { getStorageValue, setStorageValue } from '../../shared/hooks/useLocalStorage/useLocalStorage';

interface IPaginationData {
    pageSize: number;
    pageNumber: number;
}

interface IInitialState {
    [path: string]: IPaginationData;
}

const initDefaultPage = {
    empty: { pageSize: 10, pageNumber: 1 },
};

const initialState: IInitialState = getStorageValue<{ [path: string]: IPaginationData }>('pagination') ?? { ...initDefaultPage };
const paginationSlice = createSlice({
    name: 'paginationSlice',
    initialState,
    reducers: {
        changePagination: (state, action: PayloadAction<{ path: string; paginationData: IPaginationData }>) => {
            state[action.payload.path] = action.payload.paginationData;

            for (const path in state) {
                if (path.split('/')[1] === action.payload.path.split('/')[1]) {
                    if (!action.payload.path.includes(path)) {
                        delete state[path];
                    }
                }
            }

            setStorageValue('pagination', state);
        },
    },
});

export const { changePagination } = paginationSlice.actions;

export default paginationSlice.reducer;
