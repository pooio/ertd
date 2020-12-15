/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:10:45
 * @LastEditors: Misaka.chen
 * @LastEditTime: 2020-01-06 13:23:23
 * @Description: 系统首页
 * @Version: 1.0
 */
export default {
    path: '/home',
    component: () =>
        import ( /* webpackChunkName: "home" */ '@/views/home'),
    meta: {
        title: '系统首页',
        requireAuth: true, // 判断是否需要登录
    },
}