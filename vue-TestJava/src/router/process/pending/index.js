/*
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:32
 * @LastEditTime: 2020-06-28 13:42:14
 */

export default {
    path: '/workSpace',
    component: () =>
        import( /* webpackChunkName: "theme" */ '@/views/process/workSpace.vue'),
    meta: {
        title: '工作通知',
        requireAuth: true, // 判断是否需要登录
    },
}