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
    login: ({ email }: ICredentials) => void;
    data?: IGetCurrentUser;
    currentRole?: Role;
    isLoginFetching: boolean;
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

    const [ loginQuery, { isLoading: isLoginFetching, data: userData } ] = useLoginMutation();

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

    useEffect(() => {
        if (userData?.token) {
            const data = tokenDecode(storedValue);
            if (data.ban !== 'True' && data.role === Role.staff) {
                setIsAuth(true);
            }
            setStoredValue(userData?.token);
        }
    }, [ setStoredValue, storedValue, userData ]);

    /**
     * локальный вход с помощью логина и пароля
     * @param email
     */

    const login = (data: ICredentials) => {
        loginQuery(data)
            .unwrap()
            .then((res: { token: string; id: string }) => {
                setStoredValue(res.token);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    return {
        isAuth,
        login,
        isLoginFetching,
    };
};
