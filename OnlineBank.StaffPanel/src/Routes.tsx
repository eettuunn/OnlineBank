import { Paths } from './shared/constants';
import { IRoute } from './shared/types';
import { UserRoutes } from './pages/Users/UsersRoutes';
import { LoanRoutes } from './pages/Loans/LoanRoutes';

export const rootRoutes: Array<IRoute> = [
    {
        path: Paths.Users,
        title: 'Пользователи',
        children: UserRoutes,
    },
    {
        path: Paths.Loans,
        title: 'Кредитные тарифы',
        children: LoanRoutes,
    },
];
