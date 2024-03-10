import React, { useCallback, useState } from 'react';
import { Button, Col, Form, Input, Modal, Row, Select, Spin } from 'antd';
import PhoneInput from 'antd-phone-input';
import block from 'bem-cn';

import { ICreateLoanRate } from '../../api/types';
import eventEmitter from '../../../../shared/helpers/eventEmmiter';

import './ModalCreateRate.scss';

const b = block('create-record-modal');

interface IModalProps {
    onSave: (values: ICreateLoanRate) => Promise<{ data: unknown } | { error: unknown }>;
    modal: { visible: boolean; setVisible: React.Dispatch<React.SetStateAction<boolean>> };
    isLoading?: boolean
}

const ModalCreateRate: React.FC<IModalProps> = ({ onSave, modal, isLoading }) => {
    const [ form ] = Form.useForm();
    const { visible, setVisible } = modal;

    const onOk = useCallback(async () => {
        try {
            const values = (await form.validateFields()) as ICreateLoanRate;
            const res = await onSave(values);
            if ('error' in res) {
                console.log('error');
                return;
            } else {
                eventEmitter.emit('customMessage', 'success', 'Тариф создан');
                setVisible(false);
                form.resetFields();
            }
        } catch (error) {
            console.log('catcherror', error);
            return;
        }
    }, [ form, onSave, setVisible ]);

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
                        form.resetFields();
                    }}
                >
                    Отмена
                </Button>,
                <Button key="submit" loading={isLoading} size="large" type="primary" onClick={onOk}>
                    Сохранить
                </Button>,
            ]}
            open={visible}
            title="Создать тариф"
            width={720}
            onCancel={() => {
                setVisible(false);
                form.resetFields();
            }}
        >
            <Spin spinning={isLoading}>
                <Form autoComplete="off" form={form} layout="vertical">
                    <Form.Item label="Название" name="name" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                        <Input placeholder="Название" />
                    </Form.Item>
                    <Form.Item label="Процентная ставка" name="interestRate" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                        <Input placeholder="1 %" />
                    </Form.Item>
                </Form>
            </Spin>
        </Modal>
    );
};

export default ModalCreateRate;
