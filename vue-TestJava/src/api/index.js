import axios from 'axios'
import router from '../router'
import { Message } from 'element-ui'
import qs from 'qs'
import constants from '../constants/index'
const service = axios.create({
    // 设置超时时间
    timeout: 60000,
    baseURL: constants.baseURL,
})
/**
 * 请求前拦截
 * 用于处理需要在请求前的操作
 */
service.interceptors.request.use(config => {
    let prefix = config.url.substr(0, 8)
    if (config.url == 'upload/fileupload' || config.url == 'bpm/modeler/importFile' || prefix == "generate") {
        config.data = config.data
    } else {
        config.data = qs.stringify(config.data)
    }
    const token = sessionStorage.getItem('token')
    const userId = sessionStorage.getItem('userId')
    const sessionId = sessionStorage.getItem('sessionId')
    if (token) {
        config.headers['Authorization'] = token
        config.headers['USERID'] = userId
        // config.headers['sessionId'] = sessionId
    }
    return config
}, (error) => {
    return Promise.reject(error)
})
/**
 * 请求响应拦截
 * 用于处理需要在请求返回后的操作
 */
service.interceptors.response.use(response => {
    // 否则的话抛出错误
    if (response.data.code == 11001) {
        var dataTheme = (sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null') ? "default" : sessionStorage.getItem('Theme');;

        sessionStorage.clear();
        sessionStorage.setItem('Theme', dataTheme);
        Message.closeAll()
        Message({
            type: 'error',
            message: '登录信息过期，请重新登录'
        })
        router.push({
            path: '/Login',
        })
    } else {
        return Promise.resolve(response)
    }
}, (error) => {
    // 服务器返回不是 2 开头的情况，会进入这个回调
    // 可以根据后端返回的状态码进行不同的操作
    switch (error.response.status) {
        // 401：未登录
        case 401:
            // 弹出错误信息
            Message.closeAll();
            Message({
                type: 'error',
                message: '登录信息过期，请重新登录'
            })
            //跳转登录页
            var dataTheme = (sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null') ? "default" : sessionStorage.getItem('Theme');;

            sessionStorage.clear();
            sessionStorage.setItem('Theme', dataTheme);
            router.replace({
                path: '/login',
                query: {
                    redirect: router.currentRoute.fullPath
                }
            })
            break
        // 403: token过期
        case 403:
            // 弹出错误信息
            Message.closeAll();
            Message({
                type: 'error',
                message: '登录信息过期，请重新登录'
            })
            // 清除token
            // sessionStorage.removeItem('token')
            // sessionStorage.removeItem('Authorization')
            var dataTheme = (sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null') ? "default" : sessionStorage.getItem('Theme');;

            sessionStorage.clear();
            sessionStorage.setItem('Theme', dataTheme);
            // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
            setTimeout(() => {
                router.replace({
                    path: '/login',

                })
            }, 1000)
            break
        // 404请求不存在
        case 404:
            sessionStorage.clear();
            Message.closeAll();
            Message({
                message: '网络请求不存在',
                type: 'error'
            })
            break
        // 其他错误，直接抛出错误提示
        default:
            Message({
                message: error.response.data.message,
                type: 'error'
            })
    }
    // 断网 或者 请求超时 状态
    if (!error.response) {
        // 请求超时状态
        if (error.message.includes('timeout')) {
            Message.error('请求超时，请检查网络是否连接正常')
        } else {
            // 可以展示断网组件
            Message.error('请求失败，请检查网络是否已连接')
        }
        return
    }
    return Promise.reject(error)
})

export default service