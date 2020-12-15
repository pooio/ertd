/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 子表
 * @Version: 1.0
 */
export default {
    path: '/sublist/:id/:rowId',
    name: 'sublist',
    component: () =>
        import ( /* webpackChunkName: "customerForm" */ '@/views/customerForm/sublist.vue'),
    meta: {
        title: '关联业务',
        requireAuth: true, // 判断是否需要登录
    },
}