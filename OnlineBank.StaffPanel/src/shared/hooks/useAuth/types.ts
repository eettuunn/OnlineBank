import { Role } from '../../../pages/Users/constants';

export interface IGetCurrentUser {
    email: string;
    id: string,
    userName: string,
    ban: boolean,
    roles: Role[],
    phoneNumber: string,
    passport: string,
}
export interface ICredentials {
    token: string;
}
