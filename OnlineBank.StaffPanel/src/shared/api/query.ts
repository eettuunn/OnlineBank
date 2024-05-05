import axios, {
    AxiosError,
    AxiosRequestConfig,
    AxiosResponse,
    AxiosResponseHeaders,
    InternalAxiosRequestConfig,
    RawAxiosResponseHeaders,
    ResponseType,
} from 'axios';
import { BaseQueryFn, retry } from '@reduxjs/toolkit/query';

import { apiBaseUrl, apiPrefix } from '../constants';
import eventEmitter from '../helpers/eventEmmiter';
import { getStorageValue } from '../hooks/useLocalStorage/useLocalStorage';

export interface IAxiosParams {
    method: AxiosRequestConfig['method'];
    url: string;
    params?: AxiosRequestConfig['params'];
    data?: AxiosRequestConfig['data'];
    headers?: AxiosRequestConfig['headers'];
    responseType?: ResponseType | undefined;
    notCauseError?: boolean;
}

export interface IQueryErrorResponse {
    error: { status: unknown; data: unknown; headers?: RawAxiosResponseHeaders | AxiosResponseHeaders; notCauseError?: boolean };
}

export interface GenericIdentityFn<Type> {
    data: Type;
    headers?: RawAxiosResponseHeaders | AxiosResponseHeaders;
}

export type BaseQueryFnType = BaseQueryFn<IAxiosParams, unknown, unknown>;

/**
 * Инстанс аксиоса
 */
export const instance = axios.create({ baseURL: apiBaseUrl ? `${apiBaseUrl}` : '' });

/**
 * Интерцептор на запрос
 */
const requestInterceptors = (req: InternalAxiosRequestConfig) => {
    const access = getStorageValue<string>('access');
    if (access) {
        req.headers.set('Authorization', `Bearer ${access}`);
    } else {
        delete req.headers.Authorization;
    }
    return req;
};

/**
 * Интерцептор на успешный ответ сервера
 */
const successInterceptors = (response: AxiosResponse) => response;

instance.interceptors.request.use(requestInterceptors);
instance.interceptors.response.use(successInterceptors);

export const enum Api {
    api = 'api',
    user_api = 'user_api',
    loan_api = 'loan_api',
}

export const enum ErrorPercentName {
    coreErrorPercent = 'coreErrorPercent',
    userErrorPercent = 'userErrorPercent',
    loanErrorPercent = 'loanErrorPercent',
}

export const ApiToServiceName = {
    [Api.api]: ErrorPercentName.coreErrorPercent,
    [Api.user_api]: ErrorPercentName.userErrorPercent,
    [Api.loan_api]: ErrorPercentName.loanErrorPercent,
};

const emitError = (error: AxiosError, baseUrl?: string) => {
    const serviceName = ApiToServiceName[baseUrl?.slice(1) as Api];
    const errorPercent = getStorageValue(serviceName);
    console.log(baseUrl?.slice(1), ': ', serviceName, ' = ', errorPercent);
    switch (error.response?.status) {
        case 400:
            eventEmitter.emit('dataError', error.response?.data);
            retry.fail(baseUrl);
            break;
        case 401:
            eventEmitter.emit('401Error');
            retry.fail(baseUrl);
            break;
        case 403:
            eventEmitter.emit('accessError');
            retry.fail(baseUrl);
            break;
        case 404:
            eventEmitter.emit('notFoundError');
            break;
        case 500:
            eventEmitter.emit('serverError');
            if (Number(errorPercent) > 70) {
                retry.fail(baseUrl);
            }
            break;
        case 503:
            eventEmitter.emit('serverNotAvailableError');
            break;
        case 504:
            eventEmitter.emit('timeoutError');
            break;
        default:
            eventEmitter.emit('unrecognizedError');
            break;
    }
};

/**
 * Фабрика для RTK query
 * @param baseUrl
 */
export const axiosBaseQuery =
    (baseUrl?: string): BaseQueryFn<IAxiosParams, unknown, unknown> =>
        async requestOptions =>
            query(requestOptions, baseUrl ?? apiPrefix);

/**
 * Клиент для запросов
 * @param url
 * @param notCauseError
 * @param requestOptions
 * @param baseUrl
 */
export const query = async <T>(
    { url, notCauseError, ...requestOptions }: IAxiosParams,
    baseUrl = apiPrefix,
): Promise<GenericIdentityFn<T> | IQueryErrorResponse> => {
    try {
        const result = await instance({ url: `${baseUrl}${url}`, ...requestOptions /*, headers: { Authorization : `Bearer ${storedValue}` }*/ });
        return { data: result.data as T, headers: result.headers };
    } catch (axiosError) {
        const err = axiosError as AxiosError;
        if (!notCauseError) {
            emitError(err, baseUrl);
        }

        return {
            error: { status: err.response?.status, data: err.response?.data, headers: err.response?.headers },
        };
    }
};
