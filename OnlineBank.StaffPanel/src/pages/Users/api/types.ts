import { IResponseLists } from '../../../shared/types';
import { Role, TransactionType } from '../constants';

export interface IUser {
    id: string,
    email: string,
    userName: string,
    ban: boolean,
    roles: Role[],
}

export interface IUserInfo {
    id: string,
    email: string,
    userName: string,
    ban: boolean,
    roles: Role[],
    phoneNumber: string,
    passport: string,
}

export interface ICreateUser {
    email: string,
    userName: string,
    phoneNumber: string,
    roles: Role[],
    passport: string
}

export interface IAccount {
    id: string,
    name: string,
    number: string,
    creationDate: number,
    isClosed: boolean,
    balance: {
        amount: number,
        currency: string,
    }
}

export type IAccountsResponse = IResponseLists<IAccount[]>;

export interface ITransaction {
    id: string,
    transactionDate: number,
    amount: number,
    additionalInformation: string,
    transactionType: TransactionType
}
