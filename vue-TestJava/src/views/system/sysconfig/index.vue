<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:04:34
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:05:38
 * @Description: 系統配置
 * @Version: 1.0
-->
<template>
    <div class="box">
        <!-- 表格区域 -->
        <el-row :gutter="10">
            <!-- 下拉框 -->
            <el-col :span="4">
                <el-select v-model="searchInfo.configType" placeholder="请选择配置类型" clearable style="width:100%" @change="changeValue" id="system-config-configType">
                    <el-option v-for="(item,index) in moduleList" :key="index" :label="item.value" :value="item.key"></el-option>
                </el-select>
            </el-col>
            <el-col :span="20">
                <!-- <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="system-config-search">搜索</el-button> -->
                <!-- <el-button type="primary" plain  title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="system-config-reset">重置</el-button> -->
                <el-button type="primary" title="新增配置" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo,moduleList)" v-has="'system-config-add'" id="system-config-add">新增</el-button>
            </el-col>
        </el-row>
        <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border>
            <el-table-column :resizable="false" type="index" label="序号" width="50"></el-table-column>
            <el-table-column prop="configType" label="类型" :resizable="false">
                <template slot-scope="scope">
                    <span v-for="item in moduleList" :key="item.key">{{scope.row.configType == item.key ? item.value : ''}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="configContent" label="内容" :resizable="false">
                <template slot-scope="scope">
                    <span v-if="scope.row.configType != 8">{{scope.row.configContent}}</span>
                    <el-image v-else style="width: 100px; height: 100px" :src="scope.row.configContent" fit="fill"></el-image>
                </template>
            </el-table-column>
            <el-table-column :resizable="false" label="状态" width="120">
                <template slot-scope="scope">
                    <el-tooltip :content="scope.row.inUse==0?'禁用':'启用'" placement="right">
                        <el-switch v-model="scope.row.inUse" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" id="system-config-inUse" :disabled="switchIsDisabled"></el-switch>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column :resizable="false" label="操作" width="220px">
                <template slot-scope="scope">
                    <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row,moduleList)" v-has="'system-config-edit'" id="system-config-edit">编辑</el-button>
                    <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-config-del'" id="system-config-delete">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页区域 -->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        <!-- 新增配置的子组件 -->
        <Add ref="add" @getTableData="getTableData"></Add>
        <!-- 编辑配置的子组件 -->
        <Edit ref="edit" @getTableData="getTableData"></Edit>
    </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
export default {
    components: { Add, Edit },
    data() {
        return {
            // 查询参数
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 类型
                configType: ''
            },
            //配置数据列表
            tableData: [],
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-config-state');
        // 获取配置类型
        this.getModuleList();
        // 获取系统配置列表
        this.getTableData();
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取配置类型
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getModuleList() {
            const res = await this.$axios.post('sys/sysConfig/getSelectList');
            if (res.data.code == 0) {
                this.moduleList = res.data.data;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取系统配置列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTableData() {
            const res = await this.$axios.post('sys/sysConfig/list', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
            }
            console.log(res);
        },
        /**
         * @Author: Misaka.chen
         * @description: 字典是否禁用
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async handleToChange(row) {
            let info = {
                id: row.id,
                inUse: row.inUse == 1 ? 1 : 0
            };
            const res = await this.$axios.post('sys/sysConfig/setInUse', info);
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
                row.inUse = row.inUse == 1 ? 0 : 1;
            }
        },
        /**
         * @Author: flynn.yang
         * @description: 重置
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToReset() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.configType = '';
            this.date = [];
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮删除表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToDel(id) {
            this.$confirm('确定删除吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/sysConfig/del', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.msg}`);
                    }
                })
                .catch(() => {
                    this.$message.info('已取消删除');
                });
        },
        /**
         * @Author: Misaka.chen
         * @description: 分页--每页显示几条数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(pageSize) {
            this.searchInfo.pageSize = pageSize;
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 分页--当前显示第几页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(pageNum) {
            this.searchInfo.pageNum = pageNum;
            this.getTableData();
        },
        // 点击搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getTableData();
        },
        // 配置类型change事件
        changeValue() {
            this.handleToSearch();
        },
        switchDisabled(item) {
            var permissionList = sessionStorage.getItem('btnContext');
            if (permissionList != null) {
                if (permissionList.split(',').indexOf(item) > -1) {
                    this.switchIsDisabled = false;
                }
            }
        }
    }
};
</script>

<style lang="less" scoped>
.el-table {
    margin-top: 20px;
}
.el-pagination {
    text-align: center;
    margin-top: 20px;
}
</style>

