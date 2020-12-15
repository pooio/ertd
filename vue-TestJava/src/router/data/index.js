/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 数据大屏
 * @Version: 1.0
 */
export default {
    path: '/data',
    component: () =>
        import ( /* webpackChunkName: "charts" */ '@/views/data'),
    meta: {
        title: '数据大屏',
        requireAuth: true, // 判断是否需要登录
    },
}