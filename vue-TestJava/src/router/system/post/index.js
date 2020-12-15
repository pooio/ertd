/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:58:02
 * @Description: 岗位管理
 * @Version: 1.0
 */
export default {
    path: '/system-post',
    component: () =>
        import ( /* webpackChunkName: "theme" */ '@/views/system/post'),
    meta: {
        title: '岗位管理',
        requireAuth: true, // 判断是否需要登录
    },
}