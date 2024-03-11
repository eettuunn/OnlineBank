import React, { useCallback } from 'react';
import { Button, Modal, Typography } from 'antd';
import block from 'bem-cn';

import { IUser } from '../../api/types';
import eventEmitter from '../../../../shared/helpers/eventEmmiter';
import { FormBlockingMode, IModalProps, textForBlockingModal } from '../../types';

import './BlockingModal.scss';

const { Paragraph } = Typography;
const b = block('blocking-modal');

const BlockingModal: React.FC<IModalProps<IUser | object, FormBlockingMode>> = ({ modal, formMode, initialValues, onSave, isLoading }) => {
    const { visible, setVisible } = modal;

    const onOk = useCallback(async () => {
        try {
            const res = await onSave(initialValues as unknown as IUser);

            if ('error' in res) {
                console.log('error');
                return;
            } else {
                if (formMode === FormBlockingMode.Blocking) {
                    eventEmitter.emit('customMessage', 'success', 'Пользователь успешно заблокирован');
                } else {
                    eventEmitter.emit('customMessage', 'success', 'Пользователь успешно разблокирован');
                }
                setVisible(false);
            }
        } catch (error) {
            return;
        }
    }, [ initialValues, onSave, setVisible, formMode ]);

    return (
        <Modal
            centered
            className={b().toString()}
            footer={[
                <Button
                    className={b('cancel-btn').toString()}
                    key="back"
                    size="large"
                    onClick={() => {
                        setVisible(false);
                    }}
                >
                    Нет
                </Button>,
                <Button
                    className={b('submit-btn').toString()}
                    key="submit"
                    loading={isLoading}
                    size="large"
                    type="primary"
                    onClick={() => {
                        onOk();
                    }}
                >
                    Да
                </Button>,
            ]}
            open={visible}
            title={textForBlockingModal[formMode].title}
            width={720}
            onCancel={() => {
                setVisible(false);
            }}
        >
            <Paragraph className={b('paragraph').toString()}>{textForBlockingModal[formMode].body}</Paragraph>
        </Modal>
    );
};

export default BlockingModal;
