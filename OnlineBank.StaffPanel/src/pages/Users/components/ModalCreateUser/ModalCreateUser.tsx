import React, { useCallback, useState } from 'react';
import { Button, Col, Form, Input, Modal, Row, Select, Spin } from 'antd';
import PhoneInput from 'antd-phone-input';
import block from 'bem-cn';

import { ICreateUser } from '../../api/types';
import eventEmitter from '../../../../shared/helpers/eventEmmiter';
import { Role, RoleRus } from '../../constants';

import './ModalCreateUser.scss';

const b = block('create-record-modal');

interface IModalProps {
    onSave: (values: ICreateUser) => Promise<{ data: unknown } | { error: unknown }>;
    modal: { visible: boolean; setVisible: React.Dispatch<React.SetStateAction<boolean>> };
    isLoading?: boolean
}

const ModalCreateUser: React.FC<IModalProps> = ({ onSave, modal, isLoading }) => {
    const [ form ] = Form.useForm();
    const { visible, setVisible } = modal;

    const [ phoneNumber, setPhoneNumber ] = useState<string>('');

    const onOk = useCallback(async () => {
        try {
            const values = (await form.validateFields()) as ICreateUser;
            values.phoneNumber = phoneNumber;
            const res = await onSave(values);
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
    }, [ form, onSave, phoneNumber, setVisible ]);

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
                    <Form.Item label="Полное имя" name="userName" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                        <Input placeholder="Полное имя" />
                    </Form.Item>
                    <Form.Item label="Email" name="email" rules={[ { required: true, type: 'email', message: 'Пожалуйста, введите значение' } ]}>
                        <Input placeholder="Email" />
                    </Form.Item>
                    <Row gutter={12}>
                        <Col span={12}>
                            <Form.Item label="Номер телефона" name="phoneNumber" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                                <PhoneInput onChange={(value) => {
                                    setPhoneNumber(`${value.countryCode as unknown as string}${value.areaCode as unknown as string}${value.phoneNumber as unknown as string}`);
                                }}/>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label="Роль" name={[ 'roles' ]} rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                                <Select
                                    mode="multiple"
                                    options={[
                                        { value: Role.customer, label: RoleRus[Role.customer] },
                                        { value: Role.staff, label: RoleRus[Role.staff] },
                                    ]}
                                    placeholder="Роль"
                                />
                            </Form.Item>
                        </Col>
                    </Row>
                    <Row gutter={12}>
                        <Col span={12}>
                            <Form.Item dependencies={[ 'repeat-password' ]} label="Пароль" name="passport" rules={[ { required: true, message: 'Пожалуйста, введите значение' } ]}>
                                <Input placeholder="Пароль" type="password"/>
                            </Form.Item>
                        </Col>
                        <Col span={12}>
                            <Form.Item label="Повторите пароль" name="repeat-password" rules={[ { required: true, message: 'Повторите пароль' },
                                ({ getFieldValue }) => ({
                                    validator(_, value) {
                                        if (!value || getFieldValue('passport') === value) {
                                            return Promise.resolve();
                                        }
                                        return Promise.reject(new Error('Введенный пароль должен совпадать'));
                                    },
                                })  ]}>
                                <Input placeholder="Повторите пароль" type="password" />
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Spin>
        </Modal>
    );
};

export default ModalCreateUser;
