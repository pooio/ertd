/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:25:02
 * @Description: 用户管理
 * @Version: 1.0
 */
export default {
    path: '/system-user',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/user'),
    meta: {
        title: '用户管理',
        requireAuth: true, // 判断是否需要登录
    },
}