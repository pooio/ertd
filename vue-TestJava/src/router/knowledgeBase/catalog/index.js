/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 13:11:03
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 10:48:22
 * @Description: 知识库目录
 * @Version: 1.0
 */
export default {
    path: '/kb-catalog',
    component: () =>
        import ( /* webpackChunkName: "catalog" */ '@/views/knowledgeBase/catalog'),
    meta: {
        title: '知识库目录',
        requireAuth: true, // 判断是否需要登录
    },
}