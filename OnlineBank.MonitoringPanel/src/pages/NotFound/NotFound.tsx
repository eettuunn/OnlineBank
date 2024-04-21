import React from 'react';
import { Button, Result } from 'antd';
import { useNavigate } from 'react-router-dom';

const NotFound: React.FC = () => {
    const navigate = useNavigate();

    return (
        <Result
            extra={
                <Button
                    type="primary"
                    onClick={() => {
                        navigate('/');
                    }}
                >
                    Вернуться на главную страницу
                </Button>
            }
            status="404"
            subTitle="Извините, такой страницы нет."
            title="404"
        />
    );
};

export default NotFound;
