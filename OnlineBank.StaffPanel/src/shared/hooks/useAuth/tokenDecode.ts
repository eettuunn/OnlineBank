/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { Role } from '../../../pages/Users/constants';

/* eslint-disable @typescript-eslint/no-unsafe-return */
export const tokenDecode = (token: string): { role: Role, ban: string, id: string } => {
    try {
        const t = JSON.parse(atob(token.split('.')[1]));
        const { ban } = t;
        const role = t['http://schemas.microsoft.com/ws/2008/06/identity/claims/role'] as Role;
        const id = t['http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier'] as string;
        return { ban, role, id };
    } catch (e) {
        return { ban: 'True', role: Role.customer, id: '' };
    }
};
