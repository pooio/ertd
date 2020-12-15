/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:25:10
 * @Description: 菜单管理
 * @Version: 1.0
 */
export default {
    path: '/system-menu',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/menu'),
    meta: {
        title: '菜单管理',
        requireAuth: true, // 判断是否需要登录
    },
}