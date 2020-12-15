/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 数据大屏
 * @Version: 1.0
 */
export default {
    path: '/designer',
    component: () =>
        import ( /* webpackChunkName: "charts" */ '@/views/designer'),
    meta: {
        title: '报表配置',
        requireAuth: true, // 判断是否需要登录
    },
}