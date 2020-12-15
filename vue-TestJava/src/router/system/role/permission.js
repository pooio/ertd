/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:23:40
 * @Description: 角色管理
 * @Version: 1.0
 */
export default {
    path: '/system-role-permission',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/role/permission'),
    meta: {
        title: '角色权限管理',
        requireAuth: true, // 判断是否需要登录
    },
}