import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import ruRU from 'antd/lib/locale/ru_RU';
import 'moment/locale/ru';

import { store } from './redux/store';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { ProvideLayoutConfig } from './shared/hooks/useLayoutConfig/useLayoutConfig';
import { ProvideAuth } from './shared/hooks/useAuth/useAuth';

import './theme.dark.less';
import './theme.light.less';
import './index.less';

const rootComponent = (
    <React.StrictMode>
        <Provider store={store}>
            <BrowserRouter>
                <ProvideAuth>
                    <ProvideLayoutConfig>
                        <ConfigProvider locale={ruRU}>
                            <App />
                        </ConfigProvider>
                    </ProvideLayoutConfig>
                </ProvideAuth>
            </BrowserRouter>
        </Provider>
    </React.StrictMode>
);

ReactDOM.render(rootComponent, document.getElementById('root'));

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
