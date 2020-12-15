/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 日历
 * @Version: 1.0
 */
export default {
    path: '/calendar',
    component: () =>
        import ( /* webpackChunkName: "calendar" */ '@/views/workbench/calendar'),
    meta: {
        title: '日历',
        requireAuth: true, // 判断是否需要登录
    },
}