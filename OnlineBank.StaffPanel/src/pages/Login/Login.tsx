import React, { useCallback, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { block } from 'bem-cn';
import Icon from '@ant-design/icons';
import { Button, Form, Input, Layout, Typography } from 'antd';

import './Login.scss';
import { useAuth } from '../../shared/hooks/useAuth/useAuth';
import { ICredentials } from '../../shared/hooks/useAuth/types';

const { Text } = Typography;
const b = block('login');

const Login: React.FC = () => {
    const { isAuth, login, isLoginFetching } = useAuth();

    const [ form ] = Form.useForm();
    const [ disabledBtn, setDisabledBtn ] = useState<boolean>(true);

    const onFormChange = useCallback(() => {
        if (form.getFieldValue('email') && form.getFieldError('email').length === 0) {
            setDisabledBtn(false);
        } else {
            setDisabledBtn(true);
        }
    }, [ form ]);

    useEffect(() => {
        setDisabledBtn(isLoginFetching);
    }, [ isLoginFetching ]);

    const onFinish = useCallback(
        (credentials: ICredentials) => {
            login(credentials);
        },
        [ login ],
    );

    if (isAuth) {
        return <Navigate to="/" />;
    }

    return (
        <Layout>
            <div className={b().toString()}>
                <Form autoComplete="off" className={b('form').toString()} form={form} layout="vertical" onFieldsChange={onFormChange} onFinish={onFinish}>
                    <div className={b('form-text-header-container').toString()}>
                        <Text className={b('form-text-header').toString()}>Администрирование банка</Text>
                    </div>
                    <Form.Item
                        className={b('form-item').toString()}
                        label="Email"
                        name="email"
                        rules={[ { required: true, type: 'email', message: 'Введите логин' } ]}
                    >
                        <Input
                            className={b('form-input').toString()}
                            placeholder="email"
                            prefix={<Icon className={b('form-user-icon').toString()}/>}
                        />
                    </Form.Item>
                    <Form.Item className={b('form-item').toString()}>
                        <Button className={b('form-login-button').toString()} disabled={disabledBtn} htmlType="submit" size="large" type="primary">
                            Войти
                        </Button>
                    </Form.Item>
                </Form>
            </div>
        </Layout>
    );
};

export default Login;
