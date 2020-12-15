/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:23:40
 * @Description: 日志管理
 * @Version: 1.0
 */
export default {
    path: '/system-loginfo',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/loginfo'),
    meta: {
        title: '日志管理',
        requireAuth: true, // 判断是否需要登录
    },
}