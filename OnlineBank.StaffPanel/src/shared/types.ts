import { ComponentType, ReactElement } from 'react';

export interface IRoute {
    path: string;
    element?: ReactElement<any, any>;
    icon?: ComponentType<unknown>;
    title?: string;
    children?: IRoute[];
}

export interface INamedPath extends IRoute {
    path: string;
    title: string;
}

export interface IResponseLists<T> {
    pageInfo: {
        pagesCount: number,
        pageNumber: number,
        pageSize: number,
    }
    bankAccounts: T
}
