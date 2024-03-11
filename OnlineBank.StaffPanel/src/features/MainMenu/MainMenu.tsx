import React, { useCallback, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { block } from 'bem-cn';
import Icon from '@ant-design/icons';
import { ItemType } from 'antd/lib/menu/hooks/useItems';
import { Menu } from 'antd';

import { IRoute } from '../../shared/types';
import { useLayoutConfig } from '../../shared/hooks/useLayoutConfig/useLayoutConfig';

import './MainMenu.scss';

interface IProps {
    routes: Array<IRoute>;
}

const b = block('menu');

const MainMenu: React.FC<IProps> = ({ routes }) => {
    const navigate = useNavigate();
    const location = useLocation();

    const { activeMenuItem } = useLayoutConfig();

    const goHome = useCallback(() => {
        if (routes.length) {
            navigate(routes[0].path);
        }
    }, [ navigate, routes ]);

    useEffect(() => {
        if (location.pathname === '/') {
            goHome();
        }
    }, [ goHome, location.pathname ]);

    const items = routes.reduce(
        (result: ItemType[], item) =>
            item
                ? [
                    ...result,
                    {
                        label: <Link to={item.path}>{item.title}</Link>,
                        icon: <Icon component={item.icon} style={{ fontSize: 24 }} />,
                        key: item.path,
                    },
                ]
                : result,
        [],
    );

    return (
        <div>
            <div className={b()}>
                <Menu className={b('item').toString()} inlineIndent={8} items={items} mode="inline" selectedKeys={[ activeMenuItem ]} theme="light" />
            </div>
        </div>
    );
};

export default MainMenu;
