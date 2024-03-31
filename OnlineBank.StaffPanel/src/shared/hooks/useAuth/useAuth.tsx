/* eslint-disable @typescript-eslint/no-unsafe-argument */
import React, { createContext, useContext, useEffect, useState } from 'react';

import useLocalStorage from '../useLocalStorage/useLocalStorage';
import { Role } from '../../../pages/Users/constants';

import { ICredentials, IGetCurrentUser } from './types';
import { useLoginMutation } from './authApi';
import { tokenDecode } from './tokenDecode';

/**
 * Интерфейс авторизации
 * @property isAuth boolean,
 * @property user string,
 * @property login function,
 */
interface IAuth {
    isAuth: boolean;
    isLoading?: boolean;
    isSuccess?: boolean;
    isFetching?: boolean;
    isError?: boolean;
    user?: string;
    login: (data: ICredentials) => void;
    data?: IGetCurrentUser;
    currentRole?: Role;
    isLoginFetching?: boolean;
}

/**
 * Контекст авторизации
 */
const authContext = createContext<IAuth>({
    isAuth: false,
    isLoading: true,
    isFetching: true,
    isSuccess: true,
    isError: false,
    user: '',
    login: () => {},
    data: undefined,
    currentRole: undefined,
    isLoginFetching: false,
});

export const ProvideAuth: React.FC = ({ children }) => {
    const auth = useProvideAuth();
    return <authContext.Provider value={auth}>{children}</authContext.Provider>;
};

/**
 * Хук контекста авторизации
 * @returns {IAuth}
 */
export const useAuth = (): IAuth => useContext(authContext);

/**
 * Логика авторизации
 */
const useProvideAuth = (): IAuth => {
    const [ storedValue, setStoredValue ] = useLocalStorage('access', '');
    const [ isAuth, setIsAuth ] = useState(false);

    useEffect(() => {
        if (storedValue) {
            const data = tokenDecode(storedValue);
            if (data.ban !== 'True' && data.role === Role.staff) {
                setIsAuth(true);
            }
        } else {
            setIsAuth(false);
            window.localStorage.clear();
        }
    }, [ storedValue ]);

    // useEffect(() => {
    //     if (data.token) {
    //         const data = tokenDecode(storedValue);
    //         if (data.ban !== 'True' && data.role === Role.staff) {
    //             setIsAuth(true);
    //         }
    //         setStoredValue(userData?.token);
    //     }
    // }, [ setStoredValue, storedValue, userData ]);

    const login = (data: ICredentials) => {
        setStoredValue(data.token);
    };

    return {
        isAuth,
        login,
    };
};
