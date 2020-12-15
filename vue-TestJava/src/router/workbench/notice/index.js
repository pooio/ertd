/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 公告
 * @Version: 1.0
 */
export default {
    path: '/notice',
    component: () =>
        import ( /* webpackChunkName: "notice" */ '@/views/workbench/notice'),
    meta: {
        title: '公告',
        requireAuth: true, // 判断是否需要登录
    },
}