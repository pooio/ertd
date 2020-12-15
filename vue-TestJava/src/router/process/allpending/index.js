/*
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:32
 * @LastEditTime: 2020-02-27 11:11:39
 */

export default {
    path: '/process-pending',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/process/allpending'),
    meta: {
        title: '所有待审批',
        requireAuth: true, // 判断是否需要登录
    },
}