/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 14:09:45
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-07 10:49:34
 * @Description: 自定义表单
 * @Version: 1.0
 */
export default {
    path: 'form-edit',
    component: () =>
        import ( /* webpackChunkName: "customerForm" */ '@/views/customerForm/children/editForm.vue'),
    meta: {
        title: '编辑表单',
        requireAuth: true, // 判断是否需要登录
    },
}