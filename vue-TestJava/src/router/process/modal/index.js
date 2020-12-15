/*
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:32
 * @LastEditTime: 2020-02-25 00:37:12
 */

export default {
    path: '/process-modal',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/process/modal'),
    meta: {
        title: '流程模型',
        requireAuth: true, // 判断是否需要登录
    },
}