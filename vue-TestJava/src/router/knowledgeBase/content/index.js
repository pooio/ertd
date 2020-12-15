/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 10:48:22
 * @Description: 知识库内容
 * @Version: 1.0
 */
export default {
    path: '/kb-content',
    component: () =>
        import ( /* webpackChunkName: "catalog" */ '@/views/knowledgeBase/content'),
    meta: {
        title: '知识库内容',
        requireAuth: true, // 判断是否需要登录
    },
}