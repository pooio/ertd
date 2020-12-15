<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-19 09:24:18
 * @LastEditTime: 2020-02-24 23:29:07
 -->
<template>
    <div class="wrapper">
        <v-head></v-head>
        <v-sidebar></v-sidebar>
        <div class="content-box" :class="{'content-collapse':collapse}">
            <v-tags></v-tags>
            <div class="content contentNational" v-if="ThemeData=='zhongqiu'">
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view></router-view>
                    </keep-alive>
                </transition>
                <el-backtop target=".content"></el-backtop>
            </div>
            <div class="content contentFestival" v-else-if="ThemeData=='national'">
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view></router-view>
                    </keep-alive>
                </transition>
                <el-backtop target=".content"></el-backtop>
            </div>
            <div class="content contentQing" v-else-if="ThemeData=='qingming'">
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view></router-view>
                    </keep-alive>
                </transition>
                <el-backtop target=".content"></el-backtop>
            </div>

            <div class="content contentChun" v-else-if="ThemeData=='spring-festival'">
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view></router-view>
                    </keep-alive>
                </transition>
                <el-backtop target=".content"></el-backtop>
            </div>
            <div class="content" v-else>
                <transition name="move" mode="out-in">
                    <keep-alive :include="tagsList">
                        <router-view></router-view>
                    </keep-alive>
                </transition>
                <el-backtop target=".content"></el-backtop>
            </div>
        </div>
    </div>
</template>

<script>
import vHead from './Header.vue';
import vSidebar from './Sidebar.vue';
import vTags from './Tags.vue';
import bus from './bus';
export default {
    data() {
        return {
            tagsList: [],
            collapse: false,
            ThemeData: '',
            // 用户id
            userId: sessionStorage.getItem('userId'),
        };
    },
    components: {
        vHead,
        vSidebar,
        vTags
    },
    created() {
        this.ThemeData =
            sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null'
                ? 'default'
                : sessionStorage.getItem('Theme');
        bus.$on('Theme', (msg) => {
            console.log(msg);
            this.ThemeData = msg;
        });
        bus.$on('collapse-content', (msg) => {
            this.collapse = msg;
        });
        // 只有在标签页列表里的页面才使用keep-alive，即关闭标签之后就不保存到内存中了。
        bus.$on('tags', (msg) => {
            let arr = [];
            for (let i = 0, len = msg.length; i < len; i++) {
                msg[i].name && arr.push(msg[i].name);
            }
            this.tagsList = arr;
        });
    },
    methods: {
        async getBtnList(val) {
            const res = await this.$axios.post('sys/menu/getUserRoleBtn', {});
            if (res.data.code == 0) {
                sessionStorage.setItem('btnContext', res.data.data.split(','));
            }
        }
    },
    watch: {
        $route: {
            immediate: true, //开启首次监听
            handler: function (val) {
                //调用后端方法
                this.getBtnList(val);
            },
            //深度监听
            deep: true
        }
    },
};
</script>

<style  scoped>
/* .content {
    background: url('../../assets/img/quyubg@2x.png') no-repeat;
    background-size: cover;
} */
/* zhongqiu */
.contentFestival {
    background: url('../../assets/img/quyubg@2x.png') no-repeat;
    background-size: cover;
}
.contentNational {
    background: url('../../assets/img/bg@2x.png') no-repeat;
    background-size: cover;
}
.contentQing {
    background: url('../../assets/img/bgQing@2x.png') no-repeat;
    background-size: cover;
}
.contentChun {
    background: url('../../assets/img/bgChun@2x.png') no-repeat;
    background-size: cover;
}
</style>
