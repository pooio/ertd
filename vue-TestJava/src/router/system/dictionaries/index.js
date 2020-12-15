/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:57:32
 * @Description: 字典管理
 * @Version: 1.0
 */
export default {
    path: '/system-dictionaries',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/dictionaries'),
    meta: {
        title: '字典管理',
        requireAuth: true, // 判断是否需要登录
    },
}