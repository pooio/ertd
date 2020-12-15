/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:00:14
 * @Description: 国际化
 * @Version: 1.0
 */
export default {
    path: '/i18n',
    component: () =>
        import ( /* webpackChunkName: "i18n" */ '@/views/i18n'),
    meta: {
        title: '国际化',
        requireAuth: true, // 判断是否需要登录
    },
}