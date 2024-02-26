import { useEffect } from 'react';
import { message, notification } from 'antd';
import { duration } from 'moment';

import eventEmitter from '../../helpers/eventEmmiter';

/**
 * Подписка на ошибки сервера
 */
const useEventBus = (): void => {
    useEffect(() => {
        const authErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Ошибка авторизации' });
        };

        const dataErrorMessage = (error: { code: string; message: string; detail?: string }) => {
            if (error.message || error.detail) {
                notification.error({ duration: 2, placement: 'top', message: error.message || error.detail });
            } else {
                notification.error({ duration: 2, placement: 'top', message: error.message ?? 'Что-то пошло не так' });
            }
        };

        const accessErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Нет доступа' });
        };

        const notFoundErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Не найдено' });
        };

        const serverErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Ошибка сервера' });
        };

        const serverNotAvailableErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Сервер недоступен' });
        };

        const timeoutErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Сервер не ответил вовремя' });
        };

        const unrecognizedErrorMessage = () => {
            notification.error({ duration: 2, placement: 'top', message: 'Неопознанная ошибка' });
        };

        const customMessage = (type: 'success' | 'info' | 'warning' | 'error', text: string) => {
            notification[type]({ duration: 2, placement: 'top', message: text, style: { width: 'auto' } });
        };

        eventEmitter.on('authError', authErrorMessage);
        eventEmitter.on('dataError', dataErrorMessage);
        eventEmitter.on('accessError', accessErrorMessage);
        eventEmitter.on('notFoundError', notFoundErrorMessage);
        eventEmitter.on('serverError', serverErrorMessage);
        eventEmitter.on('serverNotAvailableError', serverNotAvailableErrorMessage);
        eventEmitter.on('timeoutError', timeoutErrorMessage);
        eventEmitter.on('unrecognizedError', unrecognizedErrorMessage);
        eventEmitter.on('customMessage', customMessage);

        return () => {
            eventEmitter.off('authError', authErrorMessage);
            eventEmitter.off('dataError', dataErrorMessage);
            eventEmitter.off('accessError', accessErrorMessage);
            eventEmitter.off('notFoundError', notFoundErrorMessage);
            eventEmitter.off('serverError', serverErrorMessage);
            eventEmitter.off('serverNotAvailableError', serverNotAvailableErrorMessage);
            eventEmitter.off('timeoutError', timeoutErrorMessage);
            eventEmitter.off('unrecognizedError', unrecognizedErrorMessage);
            eventEmitter.off('customMessage', customMessage);
        };
    }, []);
};

export default useEventBus;
