/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 10:48:22
 * @Description: 消息中心
 * @Version: 1.0
 */
export default {
    path: '/message',
    component: () =>
        import ( /* webpackChunkName: "message" */ '@/views/message'),
    meta: {
        title: '消息中心',
        requireAuth: true, // 判断是否需要登录
    },
}