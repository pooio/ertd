/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 10:48:50
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-19 15:23:33
 * @Description: vue配置文件
 * @Version: 1.0
 */
const path = require('path')

const minify = process.env.NODE_ENV === 'development' ? false : {
    collapseWhitespace: true,
    removeComments: true,
    removeRedundantAttributes: true,
    removeScriptTypeAttributes: true,
    removeStyleLinkTypeAttributes: true,
    useShortDoctype: true,
    minifyCSS: true,
    minifyJS: true
}

function resolve(dir) {
    return path.join(__dirname, dir)
}
// const CompressionPlugin = require("compression-webpack-plugin")
// 导入compression-webpack-plugin
const CompressionWebpackPlugin = require('compression-webpack-plugin');
// 设置压缩的文件类型
const productionGzipExtensions = /\.(js|css|json|txt|html|ico|svg)(\?.*)?$/i;
module.exports = {
    // baseUrl: './',
    publicPath: './',
    assetsDir: 'static',
    // 代码压缩是否加密
    productionSourceMap: false,
    pages: {
        index: {
            entry: 'src/main.js',
            template: 'public/index.html',
            filename: 'index.html',
            chunks: ['chunk-vendors', 'chunk-common', 'index'],
            minify
        },
        preview: {
            entry: 'src/views/customerForm/preview/main.js',
            template: 'public/preview.html',
            filename: 'preview.html',
            chunks: ['chunk-vendors', 'chunk-common', 'preview'],
            minify
        }
    },
    // devServer: {
    //     proxy: {
    //         '/api': {
    //             target: 'http://jsonplaceholder.typicode.com',
    //             changeOrigin: true,
    //             pathRewrite: {
    //                 '/api': ''
    //             }
    //         }
    //     }
    // }
    chainWebpack(config) {
        config.entry('main').add('babel-polyfill')
        // set svg-sprite-loader
        config.module
            .rule('svg')
            .exclude.add(resolve('src/assets/icons'))
            .end()
        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/assets/icons'))
            .end()
            .use('svg-sprite-loader')
            .loader('svg-sprite-loader')
            .options({
                symbolId: 'icon-[name]'
            })
            .end()
    },
    configureWebpack: {
        externals: {
            vue: 'Vue',
            'vue-router': 'VueRouter',
            axios: 'axios',
            'element-ui': 'ELEMENT'
        }
    },
    transpileDependencies: [
        'vue-echarts',
        'resize-detector'
    ]
    // configureWebpack: config => {
    //     if (process.env.NODE_ENV === 'production') {
    //         return {
    //             plugins: [
    //                 new CompressionWebpackPlugin({
    //                     filename: '[path].gz[query]',
    //                     algorithm: 'gzip',
    //                     test: productionGzipExtensions,
    //                     threshold: 2048,
    //                     minRatio: 0.8
    //                 })
    //             ]
    //         }
    //     }
    // }
}