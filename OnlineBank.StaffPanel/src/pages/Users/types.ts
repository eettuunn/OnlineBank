import { IUser } from './api/types';

export const enum FormBlockingMode {
    Blocking = 'Blocking',
    Unblocking = 'Unblocking',
}

export const textForBlockingModal = {
    [FormBlockingMode.Blocking]: {
        title: 'Блокировка',
        body: 'Вы уверены, что хотите заблокировать данного пользователя?',
    },
    [FormBlockingMode.Unblocking]: {
        title: 'Разблокировка',
        body: 'Вы уверены, что хотите разблокировать данного пользователя?',
    },
};

export interface IModalProps<T, K> {
    isLoading?: boolean;
    initialValues: T;
    onSave: (values: IUser) => Promise<{ data: IUser } | { error: unknown }>;
    formMode: K;
    modal: { visible: boolean; setVisible: React.Dispatch<React.SetStateAction<boolean>> };
}
