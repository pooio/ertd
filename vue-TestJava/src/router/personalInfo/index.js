/*
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:32
 * @LastEditTime: 2020-02-25 00:37:32
 */

export default {
    path: '/personalInfo',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/personalInfo'),
    meta: {
        title: '个人中心',
        requireAuth: true, // 判断是否需要登录
    },
}