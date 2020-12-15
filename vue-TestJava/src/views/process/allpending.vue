<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-27 10:48:54
 * @LastEditTime: 2020-07-13 11:43:53
 -->
<template>
    <div class="box">
        <!-- 表格区域 -->
        <el-row :gutter="10">
            <el-col :span="4">
                <el-input
                    v-model="searchInfo.applyUserName"
                    placeholder="请输入当前发起人"
                    clearable
                    id="process-Instance-applyUserName"
                    @keyup.enter.native="enterSearch"
                ></el-input>
            </el-col>
            <el-col :span="20">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                    id="process-pending-search"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="getPandingListRe"
                    id="process-Instance-reset"
                >重置</el-button>
            </el-col>
        </el-row>
        <el-table
            :data="tableData"
            style="width: 100%;"
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
            <el-table-column prop="processDefinitionName" label="流程名称" :resizable="false">
                <template slot-scope="scope">
                    <span
                        @click="editModal(scope.row)"
                        class="bulletinTitle cursorPoint"
                        id="process-pending-look"
                    >{{scope.row.processDefinitionName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="当前环节" :resizable="false"></el-table-column>
            <el-table-column prop="applyUserName" label="当前发起人" :resizable="false"></el-table-column>
            <el-table-column prop="assigneeName" label="当前审批人" :resizable="false"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" :resizable="false">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
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
        <!-- 弹框 -->
        <history @dialogHide="dialogHanderHide" :tableDialog="tableDialog" :formKey="formKey" v-if="isShow"></history>
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
                type: 0,
                // 模块
                applyUserName: '' //搜索发起人
            },
            tableDialog: [],
            isShow: false,
            // 日期
            date: null,
            // 日志数据列表
            tableData: [],
            loading: true,
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            formKey:'',//获取form表单参数
        };
    },
    created() {
        // 获取所有待审批数据列表
        this.getPandingList();
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description: 获取所有待审批数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getPandingList() {
            if (this.date != null) {
                this.searchInfo.startDate = this.date[0];
                this.searchInfo.endDate = this.date[1];
            } else {
                this.searchInfo.startDate = '';
                this.searchInfo.endDate = '';
            }
            const res = await this.$axios.post('bpm/task/todo/listPersonTasks', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data.data;
                this.total = res.data.data.total;
            }
            this.loading = false;
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
                    type: 0
                    // 模块
                },
                tableDialog: [],
                isShow: false,
                // 日期
                date: null,
                // 日志数据列表
                tableData: [],
                loading: true,
                // 数据总数
                total: 0,
                // 下拉框的数据列表
                moduleList: []
            };
        },
        /**
         * @Author: flynn.yang
         * @description: 分页--每页显示几条数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(pageSize) {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = pageSize;
            this.getPandingList();
        },

        /**
         * @Author: flynn.yang
         * @description: 获取所有待审批数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getPandingList() {
            if (this.date != null) {
                this.searchInfo.startDate = this.date[0];
                this.searchInfo.endDate = this.date[1];
            } else {
                this.searchInfo.startDate = '';
                this.searchInfo.endDate = '';
            }
            const res = await this.$axios.post('bpm/task/todo/listPersonTasks', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data.data;
                this.total = res.data.data.total;
            }
            this.loading = false;
        },
        /**
         * @Author: flynn.yang
         * @description: 分页--每页显示几条数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(pageSize) {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = pageSize;
            this.getPandingList();
        },
        /**
         * @Author: flynn.yang
         * @description: 分页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(pageNum) {
            this.searchInfo.pageNum = pageNum;
            this.getPandingList();
        },
        /**
         * @Author: flynn.yang
         * @description: 搜索
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getPandingList();
        },
        /**
         * @Author: flynn.yang
         * @description: 重置
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        getPandingListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.applyUserName = '';
            this.date = [];
            this.getPandingList();
        },
        /**
         * @Author: flynn.yang
         * @description: 历史
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        editModal(val) {
            this.isShow = true;
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
            this.$axios.post('bpm/instance/listHistoricTasks', { processInstanceId: val.executionId }).then(res => {
                this.tableDialog = res.data.data;
            });
        },
        /**
         * @Author: flynn.yang
         * @description: 弹框关闭
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        dialogHanderHide() {
            this.isShow = false;
        },
        //input回车搜索
        enterSearch() {
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
</style>
