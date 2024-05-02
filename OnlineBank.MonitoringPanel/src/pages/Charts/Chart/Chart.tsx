import React, { useEffect } from 'react';
import { Line } from '@ant-design/plots';
import block from 'bem-cn';
import { Layout } from 'antd';

import { useLayoutConfig } from '../../../shared/hooks/useLayoutConfig/useLayoutConfig';
import { Paths } from '../../../shared/constants';
import MainHeader from '../../../features/MainHeader/MainHeader';
import { useGetLogQuery } from '../../Users/api/logApi';
import { ApiName } from '../../Users/api/types';

const b = block('user-list');
const { Content } = Layout;

const Chart: React.FC = () => {
    const { setConfig } = useLayoutConfig();

    const { isFetching: isFetchingCore, data: dataCore, refetch: refetchCore } = useGetLogQuery({ apiName: ApiName.CoreService });
    const { isFetching: isFetchingUser, data: dataUser, refetch: refetchUser } = useGetLogQuery({ apiName: ApiName.UserService });
    const { isFetching: isFetchingLoan, data: dataLoan, refetch: refetchLoan } = useGetLogQuery({ apiName: ApiName.LoanService });

    useEffect(() => {
        setConfig({ activeMenuKey: Paths.Chart, headerTitle: 'График' });
    }, [ setConfig ]);

    const data = [
        { year: '1991', value: 3 },
        { year: '1992', value: 4 },
    ];

    const config = {
        data,
        xField: 'year',
        yField: 'value',
        point: {
            shapeField: 'square',
            sizeField: 4,
        },
        interaction: {
            tooltip: {
                marker: false,
            },
        },
        style: {
            lineWidth: 2,
        },
    };

    return (
        <div className={b().toString()}>
            <MainHeader />
            <Content>
                <Line {...config} />
            </Content>
        </div>
    );
};

export default Chart;
