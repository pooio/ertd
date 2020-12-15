<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:32:12
 * @LastEditTime: 2020-07-13 11:43:43
 -->
<template>
    <div class="box">
        <!-- 表格区域 -->
        <el-row :gutter="10">
            <el-col :span="4">
                <el-input
                    v-model="searchInfo.processName"
                    placeholder="请输入流程名称"
                    clearable
                    id="process-Instance-processName"
                    @keyup.enter.native="processSearch"
                ></el-input>
            </el-col>
            <el-col :span="4">
                <el-select
                    v-model="searchInfo.type"
                    placeholder="请选择实例状态"
                    style="width:100%"
                    @change="changeValue"
                    id="process-Instance-type"
                >
                    <el-option key="0" value="0" label="在运行实例"></el-option>
                    <el-option key="1" value="1" label="已结束实例"></el-option>
                </el-select>
            </el-col>
            <el-col :span="8">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                    id="process-Instance-search"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="getSysInfoListRe"
                    id="process-Instance-reset"
                >重置</el-button>
            </el-col>
        </el-row>
        <el-table
            :data="tableData"
            style="width: 100%"
            border
            v-loading="loading"
            height="calc(100% - 110px)"
        >
            <el-table-column
                :resizable="false"
                :label="$t('i18n.number')"
                align="center"
                type="index"
                width="50"
            >
                <template
                    slot-scope="scope"
                >{{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}</template>
            </el-table-column>
            <el-table-column prop="processInstanceId" label="流程实例Id"></el-table-column>

            <el-table-column prop="processDefinitionName" label="流程名称">
                <template slot-scope="scope">
                    <span
                        @click="handleOpen(scope.row)"
                        class="bulletinTitle cursorPoint"
                        id="process-Instance-look"
                    >{{scope.row.processDefinitionName}}</span>
                </template>
            </el-table-column>

            <el-table-column prop="name" label="当前环节"></el-table-column>
            <el-table-column label="状态" width="80">
                <template slot-scope="scope">{{scope.row.suspended?'挂起':'激活'}}</template>
            </el-table-column>
            <el-table-column prop="businessKey" label="业务主键" ></el-table-column>
            <el-table-column :resizable="false" label="操作" width="220px">
                <template slot-scope="scope">
                    <el-button
                        v-if="scope.row.suspended"
                        type="primary"
                        icon="el-icon-caret-right"
                        size="small"
                        @click="activation(scope.row)"
                        v-has="'activate-process-Instance'"
                        id="activate-Instance-activation"
                    >激活</el-button>

                    <el-button
                        v-else
                        type="primary"
                        icon="el-icon-remove-outline"
                        size="small"
                        @click="hang(scope.row)"
                        id="process-Instance-hang"
                        v-has="'pending-process-Instance'"
                    >挂起</el-button>

                    <el-button
                        type="danger"
                        icon="el-icon-delete"
                        size="small"
                        @click="remove(scope.row)"
                        id="process-Instance-delete"
                        v-has="'del-process-Instance'"
                    >删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页区域 -->
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchInfo.pageNum"
            :page-size="searchInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
        ></el-pagination>
        <!-- 审批记录 -->
        <history
            @dialogHide="dialogHanderHide"
            :tableDialog="tableDialog"
            :formKey="formKey"
            v-if="approvalVisible"
        ></history>
    </div>
</template>

<script>
import history from './pendingChildren/history';
export default {
    components: {
        history
    },
    data() {
        return {
            // 查询参数
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 操作人
                processName: '',
                // 实例类型
                type: '0'
            },
            approvalVisible: false,
            isShow: '',
            // 日期
            date: null,
            // 流程实例数据列表
            tableData: [],
            loading: true,
            tableDialog: [],
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            formKey:'',//获取form表单传值
        };
    },
    created() {
        // 获取流程实例数据列表
        this.getInstanceList();
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description:查看审批记录
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleOpen(val) {
            this.approvalVisible = true;
            let that = this;
            // 暂无业务不展示form表单
            // that.formKey = val.businessKey; 

            let postDataImg = {
                processInstanceId: val.processInstanceId
            };

            this.$axios
                .post('bpm/instance/history/img', postDataImg, {
                    responseType: 'arraybuffer'
                })
                .then(res => {
                    return (
                        'data:image/png;base64,' + btoa(new Uint8Array(res.data).reduce((data, key) => data + String.fromCharCode(key), ''))
                    );
                })
                .then(data => {
                    //这一次箭头函数是依赖于第一次.then函数处理的data值
                    var oStu = document.getElementById('images');
                    oStu.setAttribute('src', data);
                });
            this.$axios
                .post('bpm/instance/listHistoricTasks', { processInstanceId: val.processInstanceId })
                .then(res => {
                    console.log('555', res);
                    if (res != null) {
                        that.tableDialog = res.data.data;
                    }
                })
                .catch(err => {
                    console.info('报错的信息', err);
                });
        },
        dialogHanderHide() {
            this.approvalVisible = false;
        },
        /**
         * @Author: flynn.yang
         * @description: 激活
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        activation(item) {
            let msg = '即将激活当前数据, 是否继续?';
            this.$confirm(msg, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    var qs = require('querystring');
                    this.$axios.post('bpm/instance/active', { processInstanceId: item.processInstanceId }).then(res => {
                        if (res.data.code == 0) {
                            this.radio = '';
                            console.log('激活', res);
                            this.$message({
                                type: 'success',
                                message: '激活成功！'
                            });
                            this.getInstanceList();
                        } else {
                            this.$message({
                                type: 'error',
                                message: '激活失败！'
                            });
                        }
                    });
                })
                .catch(err => {
                    console.info('报错的信息', err);
                });
        },
        /**
         * @Author: flynn.yang
         * @description: 挂起
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        hang(item) {
            let msg = '即将暂停当前数据, 是否继续?';
            this.$confirm(msg, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    var qs = require('querystring');
                    this.$axios.post('bpm/instance/suspend', { processInstanceId: item.processInstanceId }).then(res => {
                        if (res.data.code == 0) {
                            this.radio = '';
                            this.$message({
                                type: 'success',
                                message: '挂起成功！'
                            });
                            this.getInstanceList();
                        } else {
                            this.$message({
                                type: 'error',
                                message: '挂起失败！'
                            });
                        }
                    });
                })
                .catch(err => {
                    console.info('报错的信息', err);
                });
        },
        /**
         * @Author: flynn.yang
         * @description: 删除
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        remove(item) {
            let msg = '即将删除当前数据, 是否继续?';
            this.$confirm(msg, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    var qs = require('querystring');
                    this.$axios.post('bpm/instance/delete', { processInstanceId: item.processInstanceId }).then(res => {
                        console.info('报错的信息', res);
                        if (res.code == 0) {
                            this.radio = '';
                            this.$message({
                                type: 'success',
                                message: '删除成功'
                            });
                            this.getInstanceList();
                        }
                    });
                })
                .catch(err => {
                    console.info('报错的信息', err);
                });
        },
        /**
         * @Author: flynn.yang
         * @description: 获取数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getInstanceList() {
            const res = await this.$axios.post('bpm/instance/list', this.searchInfo);
            if (res.data.code == 0) {
                console.log(res.data);
                this.tableData = res.data.data.data;
                this.total = res.data.data.total;
            }
            this.loading = false;
            // console.log(res);
        },
        /**
         * @Author: flynn.yang
         * @description: 重置
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        getSysInfoListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.processName = '';
            // this.searchInfo.module = '';
            this.searchInfo.type = '0';
            this.date = [];

            this.getInstanceList();
        },
        /**
         * @Author: flynn.yang
         * @description: 分页--每页显示几条数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(pageSize) {
            this.pageNum = 1;
            this.searchInfo.pageSize = pageSize;
            this.getInstanceList();
        },
        /**
         * @Author: flynn.yang
         * @description: 分页--当前显示第几页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(pageNum) {
            this.searchInfo.pageNum = pageNum;
            this.getInstanceList();
        },
        /**
         * @Author: flynn.yang
         * @description:点击搜索
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getInstanceList();
        },
        // 流程名称回车查询
        processSearch() {
            this.handleToSearch();
        },
        // 审批状态change事件
        changeValue() {
            this.handleToSearch();
        }
    }
};
</script>

<style lang="less" scoped>
@import '../../assets/css/style.css';
.el-table {
    margin-top: 20px;
}
.el-pagination {
    margin-top: 20px;
    text-align: center;
}
.fl {
    float: left;
}
</style>
