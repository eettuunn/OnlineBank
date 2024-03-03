import React, { useCallback } from 'react';
import { Button, Col, Form, Input, Modal, Row, Select, Spin } from 'antd';
import block from 'bem-cn';

import { IUser } from '../../api/types';
import eventEmitter from '../../../../shared/helpers/eventEmmiter';
import { Role, RoleRus } from '../../constants';

import './ModalCreateUser.scss';

const b = block('create-record-modal');

interface IModalProps {
    onSave?: (values: IUser) => Promise<{ data: unknown } | { error: unknown }>;
    modal: { visible: boolean; setVisible: React.Dispatch<React.SetStateAction<boolean>> };
    isLoading?: boolean
}

const ModalCreateUser: React.FC<IModalProps> = ({ onSave, modal, isLoading }) => {
    const [ form ] = Form.useForm();
    const { visible, setVisible } = modal;

    const onOk = useCallback(async () => {
        try {
            const values = (await form.validateFields()) as IUser;

            // const res = await onSave(values);
            const res = {};
            if ('error' in res) {
                console.log('error');
                return;
            } else {
                eventEmitter.emit('customMessage', 'success', 'Пользователь успешно создан');
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
            title="Добавить пользователя"
            width={720}
            onCancel={() => {
                setVisible(false);
                form.resetFields();
            }}
        >
            <Spin spinning={isLoading}>
                <Form autoComplete="off" form={form} layout="vertical">
                    <Form.Item hidden noStyle name="id">
                        <Input />
                    </Form.Item>
                    <Form.Item label="Полное имя" name="fullName" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                        <Input placeholder="Полное имя" />
                    </Form.Item>
                    <Row gutter={12}>
                        <Col span={12}>
                            <Form.Item label="Email" name="email" rules={[ { required: true, type: 'email', message: 'Пожалуйста, введите значение' } ]}>
                                <Input placeholder="Email" />
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label="Роль" name="role" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                                <Select
                                    options={[
                                        { value: Role.customer, label: RoleRus[Role.customer] },
                                        { value: Role.staff, label: RoleRus[Role.staff] },
                                    ]}
                                    placeholder="Роль"
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Spin>
        </Modal>
    );
};

export default ModalCreateUser;
