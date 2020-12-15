/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 404错误
 * @Version: 1.0
 */
export default {
    path: '*',
    component: () =>
        import ( /* webpackChunkName: "404" */ '@/views/404'),
    meta: {
        title: '404',
        requireAuth: true, // 判断是否需要登录
    },
}