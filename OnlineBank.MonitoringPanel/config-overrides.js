/* eslint-disable @typescript-eslint/no-var-requires */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */

module.exports = function override(config) {

    const oldRules = config.module.rules[0];
    config.module.rules[0] = {
        ...oldRules,
        exclude: [/@babel(?:\/|\\{1,2})runtime/, /node_modules/],
    };

    config.module.rules[1].oneOf.splice(2, 0, {
        test: /\.less$/i,
        exclude: /\.module\.(less)$/,
        use: [
            { loader: 'style-loader' },
            { loader: 'css-loader' },
            {
                loader: 'less-loader',
                options: {
                    lessOptions: {
                        sourceMap: true,
                        javascriptEnabled: true,
                    },
                },
            },
        ],
    });
    return config;
};

