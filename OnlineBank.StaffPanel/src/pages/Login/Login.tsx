import React, { useEffect, useMemo } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { block } from 'bem-cn';
import { Button, Layout } from 'antd';

import './Login.scss';
import { useAuth } from '../../shared/hooks/useAuth/useAuth';
import { urlAuth } from '../../shared/constants';
import { getStorageValue } from '../../shared/hooks/useLocalStorage/useLocalStorage';

const b = block('login');

const Login: React.FC = () => {
    const { isAuth, login } = useAuth();

    const { search } = useLocation();
    const authData = useMemo(() =>
        ({
            token: search?.split('&')[0]?.substring(7),
            id: search?.split('&')[1]?.substring(3),
        }), [ search ]);

    useEffect(() => {
        if (!getStorageValue('access')) {
            login(authData);
        }
    }, [ authData, login ]);

    if (isAuth) {
        return <Navigate to="/" />;
    }

    return (
        <Layout>
            <div className={b().toString()}>
                Необходимо авторизоваться
                <Button onClick={() => {
                    console.log(urlAuth);
                    if (urlAuth)
                        location.href = `${urlAuth}?url=http://localhost:3001/login`;
                }
                }>Перейти на страницу авторизации</Button>
            </div>
        </Layout>
    );
};

export default Login;
