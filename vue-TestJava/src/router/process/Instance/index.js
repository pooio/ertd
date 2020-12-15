/*
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:32
 * @LastEditTime: 2020-02-25 00:37:32
 */

export default {
    path: '/process-Instance',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/process/Instance'),
    meta: {
        title: '流程实例',
        requireAuth: true, // 判断是否需要登录
    },
}