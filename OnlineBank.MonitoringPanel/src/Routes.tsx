import { Paths } from './shared/constants';
import { IRoute } from './shared/types';
import { LogRoutes } from './pages/Users/LogRoutes';
import { ChartRoutes } from './pages/Charts/ChartRoutes';

export const rootRoutes: Array<IRoute> = [
    {
        path: Paths.Log,
        title: 'Мониторинг',
        children: LogRoutes,
    },
    {
        path: Paths.Chart,
        title: 'График',
        children: ChartRoutes,
    },
];
