export interface ILogResponse {
    errorsPercent: number,
    requests: IRequest[]
}

export interface IRequest {
    url: string,
    method: string,
    protocol: string,
    status: number,
    spentTimeInMs: number,
    apiName: string
}

export interface IRequestParams {
    apiName: ApiName | undefined
}

export enum ApiName {
    UserService = 'UserService',
    LoanService = 'LoanService',
    CoreService = 'CoreService',
}
