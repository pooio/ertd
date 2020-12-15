/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:23:40
 * @Description: 系统配置
 * @Version: 1.0
 */
export default {
    path: '/system-config',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/sysconfig'),
    meta: {
        title: '系统配置',
        requireAuth: true, // 判断是否需要登录
    },
}