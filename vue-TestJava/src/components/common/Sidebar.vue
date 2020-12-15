<template>
    <div class="sidebar" :class="{borderDiv:!collapse}">
        <!-- <el-menu class="sidebar-el-menu" :default-active="onRoutes" :collapse="collapse" unique-opened router :background-color="BackColor" :text-color="TextColor"> -->
        <el-menu class="sidebar-el-menu" :default-active="$route.path" :collapse="collapse" unique-opened router :background-color="BackColor" :text-color="TextColor">
            <!--  
            
            -->
            <template v-for="item in items">
                <template v-if="item.children">
                    <el-submenu :index="item.url" :key="item.url" :id="item.url">
                        <template slot="title">
                            <!-- <i :class="item.icon"></i> -->
                            <svg-icon :iconClass="item.iconClass == null ? '' : item.iconClass" />&nbsp;&nbsp;
                            <span slot="title">{{ item.name }}</span>
                        </template>
                        <template v-for="subItem in item.children">
                            <el-submenu v-if="subItem.children" :index="subItem.url" :key="subItem.url" :id="subItem.url">
                                <template slot="title">{{ subItem.name }}</template>
                                <el-menu-item v-for="(threeItem, i) in subItem.children" :key="i" :index="threeItem.url">{{threeItem.name}}</el-menu-item>
                            </el-submenu>
                            <el-menu-item v-else :index="subItem.url" :key="subItem.url" :id="subItem.url">
                                <template slot="title">
                                    <!-- <i :class="subItem.icon"></i> -->
                                    <svg-icon :iconClass="subItem.iconClass == null ? '' : subItem.iconClass" />&nbsp;&nbsp;
                                    <span slot="title">{{ subItem.name }}</span>
                                </template>
                            </el-menu-item>
                        </template>
                    </el-submenu>
                </template>
                <template v-else>
                    <el-menu-item :index="item.url" :key="item.url" :id="item.url">
                        <!-- <i :class="item.icon"></i> -->
                        <svg-icon :iconClass="item.iconClass == null ? '' : item.iconClass" />&nbsp;&nbsp;
                        <span slot="title">{{ item.name }}</span>
                    </el-menu-item>
                </template>
            </template>
        </el-menu>
    </div>
</template>

<script>
import bus from '../common/bus';
export default {
    data() {
        return {
            collapse: false,
            ThemeData: '',
            TextColor: '#000000',
            BackColor: '#19202D',
            items: [],
            formManage: []
        };
    },
    computed: {
        // onRoutes() {
        //     return this.$route.path.replace('/', '');
        // }
    },
    created() {
        // 获取左侧菜单
        this.getMenuData();
        // 通过 Event Bus 进行组件间通信，来折叠侧边栏
        bus.$on('collapse', (msg) => {
            this.collapse = msg;
            bus.$emit('collapse-content', msg);
        });
        this.ThemeData =
            sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null'
                ? 'default'
                : sessionStorage.getItem('Theme');
        if (this.ThemeData == 'qingming') {
            this.TextColor = '#FFFFFF';
            this.BackColor = '#7CBBBD';
        } else if (this.ThemeData == 'national') {
            this.TextColor = '#666666';
            this.BackColor = '#FFEAEA';
        } else if (this.ThemeData == 'spring-festival') {
            this.TextColor = '#FFFFFF';
            this.BackColor = '#6E000A';
        } else if (this.ThemeData == 'zhongqiu') {
            this.TextColor = '#999999';
            this.BackColor = '#0B2338';
        } else if (this.ThemeData == 'default') {
            this.TextColor = '#FFFFFF';
            this.BackColor = '#19202D';
        }
        bus.$on('Theme', (msg) => {
            console.log(msg);
            if (msg == 'qingming') {
                this.TextColor = '#FFFFFF';
                this.BackColor = '#7CBBBD';
            } else if (msg == 'national') {
                this.TextColor = '#666666';
                this.BackColor = '#FFEAEA';
            } else if (msg == 'spring-festival') {
                this.TextColor = '#FFFFFF';
                this.BackColor = '#6E000A';
            } else if (msg == 'zhongqiu') {
                this.TextColor = '#999999';
                this.BackColor = '#0B2338';
            } else if (msg == 'default') {
                this.TextColor = '#FFFFFF';
                this.BackColor = '#19202D';
            }
        });
    },
    methods: {
        // 获取菜单树
        async getMenuData() {
            let info = {
                roleId: 1,
                type: 1
            };
            const res = await this.$axios.post('/sys/menu/treeData', info);
            if (res.data.code == 0) {
                sessionStorage.setItem('menu', JSON.stringify(res.data.data));
                // this.items.push(res.data.data)
                this.items = res.data.data;
                sessionStorage.setItem('formManageName', null);
                this.formManage = [];
                this.items.forEach((item) => {
                    if (item.children != null) {
                        this.setItemToFormManage(item.children);
                    }
                    if (item.url.indexOf('/form-manage/') > -1) {
                        this.formManage.push({
                            name: item.name,
                            urlParam: item.url.replace('/form-manage/', '')
                        });
                    }
                });
                sessionStorage.setItem('formManageName', JSON.stringify(this.formManage));
            }
        },
        setItemToFormManage(data) {
            data.forEach((item) => {
                if (item.children != null) {
                    this.setItemToFormManage(item.children);
                }
                if (item.url.indexOf('/form-manage/') > -1) {
                    this.formManage.push({
                        name: item.name,
                        urlParam: item.url.replace('/form-manage/', '')
                    });
                }
            });
        }
    }
};
</script>

<style scoped>
.borderDiv {
    border-right: solid 1px #e6e6e6;
}

.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: 70px;
    /* height: 100%; */
    height: calc(100% - 70px);
    width: 200px;
    bottom: 0;
    overflow-y: auto;
    overflow-x: hidden;
}

.sidebar::-webkit-scrollbar {
    width: 0;
}

.sidebar-el-menu:not(.el-menu--collapse) {
    width: 200px;
}

.sidebar > ul {
    height: 100%;
}

.iconfont {
    display: inline-block;
    vertical-align: middle;
    margin-right: 5px;
    width: 24px;
    text-align: center;
    font-size: 18px;
}

/* .el-menu {
    height: 100%;
    width: 200px;

    background-image: url('../../assets/img/cedaohang@2x.png') no-repeat;
    background-color: rgba(225, 225, 225, 0.1) !important;
} */
.el-menu-item,
.el-submenu__title {
    background-color: rgba(225, 225, 225, 0.1);
}
</style>