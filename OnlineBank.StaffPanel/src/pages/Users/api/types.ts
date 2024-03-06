import { Role } from '../constants';

export interface IUser {
    id?: string,
    email: string,
    userName: string,
    ban: boolean,
    roles: Role[]
}

export interface IAccount {
    id: string,
    name: string,
    number: string,
    creationDate: number,
    isClosed: boolean,
    balance: number
}

export enum TransactionType {
    WITHDRAW,
    DEPOSIT,
    REPAY_LOAN,
    TAKE_LOAN,
}

export interface ITransaction {
    id: string,
    transactionDate: number,
    amount: number,
    additionalInformation: string,
    type: TransactionType
}
