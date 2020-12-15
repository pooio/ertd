/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:57:13
 * @Description: 区域管理
 * @Version: 1.0
 */
export default {
    path: '/system-area',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/area'),
    meta: {
        title: '区域管理',
        requireAuth: true, // 判断是否需要登录
    },
}