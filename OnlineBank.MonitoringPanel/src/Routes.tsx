import { TeamOutlined } from '@ant-design/icons';

import { Paths } from './shared/constants';
import { IRoute } from './shared/types';
import { LogRoutes } from './pages/Users/LogRoutes';

export const rootRoutes: Array<IRoute> = [
    {
        path: Paths.Log,
        title: 'Мониторинг',
        children: LogRoutes,
    },
];
