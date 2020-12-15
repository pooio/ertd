/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:57:13
 * @Description: 公告管理
 * @Version: 1.0
 */
export default {
    path: '/system-bulletin',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/bulletin'),
    meta: {
        title: '公告管理',
        requireAuth: true, // 判断是否需要登录
    },
}