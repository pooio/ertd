<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改角色的表单
 * @Version: 1.0
 -->
<template>
    <div class="box">
        <el-tabs type="border-card" class>
            <el-tab-pane class="box" v-has="'system-role-menu'">
                <span slot="label" id="system-role-menu">
                    <i class="el-icon-set-up"></i> 权限分配
                </span>
                <Menu v-bind:roleId="roleId" v-if="menuFlag"></Menu>
            </el-tab-pane>
            <el-tab-pane class="box" v-has="'system-role-data'">
                <span slot="label" id="system-role-data">
                    <i class="el-icon-menu"></i> 数据访问权限设置
                </span>
                <Data v-bind:roleId="roleId" v-if="dataFlag"></Data>
            </el-tab-pane>
            <el-tab-pane class="box" v-has="'system-role-user'">
                <span slot="label" id="system-role-user">
                    <i class="el-icon-user-solid"></i> 用户信息
                </span>
                <Allocation v-bind:roleId="roleId" v-if="allocationFlag"></Allocation>
            </el-tab-pane>
        </el-tabs>
        <el-button
            size="mini"
            type="primary"
            @click="$router.push({path:'system-role'})"
            id="system-permission-back"
            icon="el-icon-back"
            class="permission-back"
        >返回</el-button>
    </div>
</template>

<script>
import Menu from './children/menu';
import Data from './children/data';
import Allocation from './children/allocation';
export default {
    components: { Menu, Data, Allocation },
    data() {
        return {
            roleId: '',
            menuFlag: false,
            dataFlag: false,
            allocationFlag: false
        };
    },
    created() {
        this.roleId = this.$route.query.roleId;
        this.menuFlag = true;
        this.dataFlag = true;
        this.allocationFlag = true;
        console.log();
    },
    methods: {}
};
</script>

<style lang="less" scoped>
.box .el-tabs {
    height: calc(100% - 2px);
    overflow: hidden;
}
.el-tabs--border-card /deep/ .el-tabs__content {
    height: calc(100% - 70px);
}
.box {
    position: relative;
}
.permission-back {
    position: absolute;
    right: 18px;
    top: 6px;
}
</style>
