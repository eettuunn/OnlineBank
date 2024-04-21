export interface IGetCurrentUser {
    email: string;
    id: string,
    userName: string,
    ban: boolean,
    roles: string[],
    phoneNumber: string,
    passport: string,
}
export interface ICredentials {
    token: string;
}
