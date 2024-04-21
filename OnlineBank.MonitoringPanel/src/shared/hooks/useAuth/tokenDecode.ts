/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */

/* eslint-disable @typescript-eslint/no-unsafe-return */
export const tokenDecode = (token: string): { role: string, ban: string, id: string } => {
    try {
        const t = JSON.parse(atob(token.split('.')[1]));
        const { ban } = t;
        const role = t['http://schemas.microsoft.com/ws/2008/06/identity/claims/role'];
        const id = t['http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier'] as string;
        return { ban, role, id };
    } catch (e) {
        return { ban: 'True', role: 'customer', id: '' };
    }
};
