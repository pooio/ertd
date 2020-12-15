<!--
 * @Author: flynn.yang
 * @Date: 2020-06-28 13:40:36
 * @LastEditors: flynn.yang
 * @LastEditTime: 2020-06-30 14:53:55
 * @Description: MetaShare
 * @Version: 1.0
--> 
<template>
    <div class="box">
        <el-tabs v-model="activeName" @tab-click="handleClick" class="box">
            <el-tab-pane label="流程表单" name="first" class="box">
                <launchTab v-if="activeName=='first'"></launchTab>
            </el-tab-pane>
            <el-tab-pane label="待我审批" name="second" class="box">
                <pending v-if="activeName=='second'"></pending>
            </el-tab-pane>
            <el-tab-pane label="我已审批" name="third" class="box">
                <processed v-if="activeName=='third'"></processed>
            </el-tab-pane>
            <el-tab-pane label="我发起的" name="four" class="box">
                <applayForSelf v-if="activeName=='four'"></applayForSelf>
            </el-tab-pane>
        </el-tabs>
        <el-button
            type="primary"
            @click="$router.push({path:'/'})"
            id="system-permission-back"
            icon="el-icon-back"
            class="permission-back"
        >返回</el-button>
    </div>
</template>


<script>
import processed from './workSpaceChildren/processed';
import applayForSelf from './workSpaceChildren/applayforself.vue';
import pending from './workSpaceChildren/pending.vue';
import launchTab from './workSpaceChildren/launchTab.vue';
export default {
    components: {
        processed,
        applayForSelf,
        pending,
        launchTab
    },
    data() {
        return {
            activeName:  this.$route.query.data
        };
    },
    methods: {
        // tab页签点击的回调
        handleClick(data) {}
    }
};
</script>
<style lang="less" scoped>
.box /deep/ .el-tabs__content{
    height: 100%;
}
.box {
    position: relative;
}
.permission-back {
    position: absolute;
    right: 18px;
    top: 0px;
}
</style>