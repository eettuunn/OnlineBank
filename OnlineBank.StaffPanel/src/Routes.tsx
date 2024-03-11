import { BankOutlined, TeamOutlined } from '@ant-design/icons';

import { Paths } from './shared/constants';
import { IRoute } from './shared/types';
import { UserRoutes } from './pages/Users/UsersRoutes';
import { LoanRoutes } from './pages/Loans/LoanRoutes';

export const rootRoutes: Array<IRoute> = [
    {
        path: Paths.Users,
        icon: TeamOutlined,
        title: 'Пользователи',
        children: UserRoutes,
    },
    {
        path: Paths.Loans,
        icon: BankOutlined,
        title: 'Кредитные тарифы',
        children: LoanRoutes,
    },
];
