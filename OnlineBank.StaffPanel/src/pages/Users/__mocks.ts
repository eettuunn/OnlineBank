import { IUser } from './api/types';
import { Role } from './constants';

export const UsersMock: IUser[] = [
    {
        id: '1',
        email: '1212@gmail.com',
        fullName: 'Иванов Иван Иванович',
        isLocked: false,
        role: Role.customer,
    },
    {
        id: '2',
        email: '1212@gmail.com',
        fullName: 'Иванов Иван Иванович',
        isLocked: false,
        role: Role.customer,
    },
    {
        id: '3',
        email: '1212@gmail.com',
        fullName: 'Иванов Иван Иванович',
        isLocked: false,
        role: Role.customer,
    },
    {
        id: '4',
        email: '1212@gmail.com',
        fullName: 'Иванов Иван Иванович',
        isLocked: false,
        role: Role.customer,
    },
];
