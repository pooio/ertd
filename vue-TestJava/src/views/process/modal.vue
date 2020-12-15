<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 00:31:56
 * @LastEditTime: 2020-07-13 11:38:57
 -->
 
<template>
    <div class="box">
        <div style="height:100%;">
            <!-- 搜索、新增 -->
            <el-row :gutter="10">
                <el-col :span="4">
                    <el-input v-model="searchInfo.processName" placeholder="请输入流程名称" clearable id="process-modal-processName" @keyup.enter.native="processSearch"></el-input>
                </el-col>
                <el-col :span="20">
                    <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="process-modal-search">搜索</el-button>
                    <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="getSysInfoListRe" id="process-modal-reset">重置</el-button>
                    <el-button type="primary" icon="el-icon-plus" title="新增流程模型" style="margin-left:20px" @click="addModal" v-has="'add-process-modal'" id="process-modal-add">新增</el-button>
                    <el-button type="success" title="导入流程模型" icon="el-icon-upload2" @click="ImportModal" v-has="'import-process-modal'" id="process-modal-import">导入</el-button>
                </el-col>
            </el-row>
            <!-- 表格区域 -->
            <el-table :data="tableData" style="width: 100%" border v-loading="loading" height="calc(100% - 110px)">
                <el-table-column :resizable="false" :label="$t('i18n.number')" align="center" type="index" width="50">
                    <template slot-scope="scope">{{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}</template>
                </el-table-column>
                <el-table-column :resizable="false" prop="id" label="ID"></el-table-column>
                <el-table-column prop="key" label="流程标识" :resizable="false"></el-table-column>
                <el-table-column prop="name" label="流程名称" :resizable="false"></el-table-column>
                <el-table-column prop="metaInfo" label="描述" :resizable="false">
                    <template slot-scope="scope">
                        <span>{{ scope.row.metaInfo==null?"-":JSON.parse(scope.row.metaInfo).description }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="version" label="版本" :resizable="false"></el-table-column>

                <el-table-column prop="createTime" label="创建时间" :resizable="false">
                    <template slot-scope="scope">
                        <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="lastUpdateTime" label="更新时间" :resizable="false">
                    <template slot-scope="scope">
                        <span>{{ scope.row.lastUpdateTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="deploymentId" label="部署ID" :resizable="false"></el-table-column>
                <el-table-column label="操作" :resizable="false" width="350px">
                    <template slot-scope="scope">
                        <el-button size="mini" type="primary" title="编辑数据" @click="editModal(scope.row)" icon="el-icon-edit" id="process-modal-edit" v-has="'edit-process-modal'">编辑</el-button>

                        <el-button @click="ReleaseModel(scope.row)" size="mini" type="success" title="发布模型" icon="el-icon-check" id="process-modal-publish" v-has="'release-process-modal'">发布</el-button>
                        <el-button @click="exportModal(scope.row)" size="mini" type="warning" title="导出模型" icon="el-icon-download" id="process-modal-export" v-has="'export-process-modal'">导出</el-button>
                        <el-button size="mini" type="danger" title="删除数据" @click="delectModal(scope.row)" icon="el-icon-delete" id="process-modal-delete" v-has="'del-process-modal'">删除</el-button>
                        <!-- <el-button @click="delectModal(scope.row)" type="text" size="small">删除</el-button> -->
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页区域 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        </div>
        <!-- <div class="process" v-else>
            <iframe :src="ifmURL" frameborder="0"></iframe>
        </div> -->
        <!-- 弹框 -->
        <el-dialog title="新增流程模型" :visible.sync="addDialogFormVisible" width="500px">
            <add @dialog="dialogForm" v-if="addDialogFormVisible"></add>
        </el-dialog>
        <el-dialog title="编辑流程模型" :visible.sync="editDialogFormVisible" width="500px">
            <edit v-bind:editDataProp="editData" @dialogEdit="dialogFormEdit" v-if="editDialogFormVisible"></edit>
        </el-dialog>
        <el-dialog title="导入流程模型" :visible.sync="importDialogFormVisible" width="500px">
            <importModel @dialogImport="dialogFormImport" v-if="importDialogFormVisible"></importModel>
        </el-dialog>
    </div>
</template>

<script>
import add from './modalChildren/add.vue';
import edit from './modalChildren/edit.vue';
import importModel from './modalChildren/import.vue';
export default {
    components: { add, edit, importModel },
    data() {
        return {
            // 查询参数
            addDialogFormVisible: false,
            editDialogFormVisible: false,
            importDialogFormVisible: false,
            editData: '',
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 流程名称
                processName: ''
            },
            // 日期
            date: null,
            // 流程模型数数据列表
            tableData: [],
            loading: true,
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            ifmURL: '',
            isShow: true
        };
    },
    created() {
        // 获取流程模型数据列表
        this.getModalList();
    },
    methods: {
        /**
         * @author: flynn.yang
         * @description:导出
         * @param {type}
         * @return:
         * @Version: 1
         */
        exportModal(data) {
            console.log(data);
            window.open(this.BASE_URL.LIU_CHENG + '/bpm/modeler/model/' + data.id + '/export', '_blank');
        },
        /**
         * @Author: flynn.yang
         * @description:新增回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        dialogForm(data) {
            this.addDialogFormVisible = data;
            this.getModalList();
        },
        /**
         * @Author: flynn.yang
         * @description:编辑回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        dialogFormEdit(data) {
            this.editDialogFormVisible = data;
            this.getModalList();
        },
        /**
         * @Author: flynn.yang
         * @description:导入回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        dialogFormImport(data) {
            this.importDialogFormVisible = data;
            this.getModalList();
        },
        /**
         * @Author: flynn.yang
         * @description: 获取数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getModalList() {
            const res = await this.$axios.post('bpm/modeler/list', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
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
        getModalListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.userName = '';
            this.searchInfo.module = '';
            this.date = [];

            this.getModalList();
        },
        /**
         * @author: flynn.yang
         * @description:新增模型
         * @param {type}
         * @return:
         * @Version: 1
         */
        addModal() {
            this.addDialogFormVisible = true;
        },
        /**
         * @author: flynn.yang
         * @description: 发布流程
         * @param {type}
         * @return:
         * @Version: 1
         */
        ReleaseModel(data) {
            this.$axios
                .post('bpm/modeler/deploy', { id: data.id })
                .then((res) => {
                    if (res.data.code == 0) {
                        this.$message.success('发布成功');
                        this.getModalList();
                    } else {
                        this.$message.error(res.data.msg);
                    }
                    // window.location.href = 'http://117.36.73.46:8082/web/bpm/modelerIndex.html?modelId=' + res.data.data;
                })
                .catch(() => {
                    // this.getModalList();
                });
        },
        /**
         * @author: flynn.yang
         * @description: 删除流程模型
         * @param {type}
         * @return:
         * @Version: 1
         */
        delectModal(data) {
            // this.$message.success('');
            this.$confirm('您确定要删除吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                cancelButtonClass: 'process-modal-cancel',
                confirmButtonClass: 'process-modal-save',
                type: 'warning'
            }).then(() => {
                this.$axios.post('bpm/modeler/delete', { id: data.id }).then((res) => {
                    console.log(res);

                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getModalList();
                    } else {
                        this.$message.error(res.data.data);
                    }
                    // window.location.href = 'http://117.36.73.46:8082/web/bpm/modelerIndex.html?modelId=' + res.data.data;
                });
            });
        },
        /**
         * @author: flynn.yang
         * @description:编辑模型
         * @param {type}
         * @return:
         * @Version: 1
         */
        editModal(data) {
            // window.location.href = this.BASE_URL.LIU_CHENG + '/bpm/modelerIndex.html?modelId=' + data.id;
            this.ifmURL = this.BASE_URL.LIU_CHENG + '/bpm/modelerIndex.html?modelId=' + data.id;
            window.open(this.ifmURL)
            // window.open(`${this.BASE_URL.LIU_CHENG}/bpm/modelerIndex.html?modelId=${data.id}`, '_blank');
        },
        /**
         * @author: flynn.yang
         * @description: 导入模型
         * @param {type}
         * @return:
         * @Version: 1
         */
        ImportModal(data) {
            this.importDialogFormVisible = true;
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
            this.getModalList();
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
            this.getModalList();
        },
        /**
         * @Author: flynn.yang
         * @description:  点击搜索
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getModalList();
        },
        // 重置
        getSysInfoListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.processName = '';
            this.getModalList();
        },
        // 流程名称回车查询
        processSearch() {
            this.handleToSearch();
        }
    }
};
</script>

<style lang="less" scoped>
.el-table {
    margin-top: 20px;
}
.el-pagination {
    margin-top: 20px;
    text-align: center;
}
.box {
    position: relative;
}
.process {
    // position: absolute;
    // top: 0;
    // left: 0;
    // z-index: 9999;
    width: 100%;
    height: 100%;
    iframe {
        width: 100%;
        height: 100%;
    }
}
</style>
