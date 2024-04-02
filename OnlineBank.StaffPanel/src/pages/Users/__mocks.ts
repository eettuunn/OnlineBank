import { IAccount, ITransaction, IUser } from './api/types';
import { Role, TransactionType } from './constants';

export const UsersMock: IUser[] = [
    {
        id: '1',
        email: '1212@gmail.com',
        userName: 'Иванов Иван Иванович',
        ban: false,
        roles: [ Role.customer ],
    },
    {
        id: '2',
        email: '1212@gmail.com',
        userName: 'Иванов Иван Иванович',
        ban: false,
        roles: [ Role.customer ],
    },
    {
        id: '3',
        email: '1212@gmail.com',
        userName: 'Иванов Иван Иванович',
        ban: false,
        roles: [ Role.customer ],
    },
    {
        id: '4',
        email: '1212@gmail.com',
        userName: 'Иванов Иван Иванович',
        ban: false,
        roles: [ Role.customer ],
    },
];

export const AccountMock: IAccount[] = [
    {
        id: '1',
        name: 'Счет',
        number: '12233434',
        creationDate: 23233454545,
        isClosed: true,
        balance: {
            amount: 1212,
            currency: 'RUB',
        },
    },
    {
        id: '2',
        name: 'Счет',
        number: '12233434',
        creationDate: 23233454545,
        isClosed: false,
        balance: {
            amount: 1212,
            currency: 'RUB',
        },
    },
    {
        id: '3',
        name: 'Счет',
        number: '12233434',
        creationDate: 23233454545,
        isClosed: true,
        balance: {
            amount: 1212,
            currency: 'RUB',
        },
    },
];

export const TransactionMock : ITransaction[] = [
    {
        id: '1',
        transactionDate: 233434335,
        amount: 12323,
        additionalInformation: 'на подарок',
        transactionType: TransactionType.WITHDRAW,
    },
    {
        id: '2',
        transactionDate: 233434335,
        amount: 12323,
        additionalInformation: 'на подарок',
        transactionType: TransactionType.WITHDRAW,
    },
    {
        id: '3',
        transactionDate: 233434335,
        amount: 12323,
        additionalInformation: 'на подарок',
        transactionType: TransactionType.WITHDRAW,
    },
    {
        id: '4',
        transactionDate: 233434335,
        amount: 12323,
        additionalInformation: 'на подарок',
        transactionType: TransactionType.WITHDRAW,
    },
];
