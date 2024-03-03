import { Role } from '../constants';

export interface IUser {
    id?: string,
    email: string,
    fullName: string,
    isLocked: boolean,
    role: Role
}
