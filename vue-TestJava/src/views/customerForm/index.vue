<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-06 10:48:50
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 14:53:38
 * @Description: 自定义表单
 * @Version: 1.0
 -->

<template>
    <div class="box">
        <!-- 表格区域 -->
        <el-row :gutter="10">
            <el-col :span="4">
                <el-input
                    v-model="searchInfo.businessName"
                    placeholder="请输入表单名称"
                    clearable
                    id="form-list-name"
                    @keyup.enter.native="handleToSearch"
                ></el-input>
            </el-col>
            <el-col :span="20">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                    id="form-list-search"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="handleToReset"
                    id="form-list-reset"
                >重置</el-button>
                <el-button
                    type="primary"
                    icon="el-icon-plus"
                    title="新增表单"
                    style="margin-left:20px"
                    @click="$router.push({path:'form-add'})"
                    id="form-list-add"
                    v-has="'add-form-list'"
                >新增</el-button>
            </el-col>
        </el-row>

        <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
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
            <el-table-column prop="businessName" label="表单名称" :resizable="false">
                <template slot-scope="scope">
                    <span class="bulletinTitle" style="cursor: pointer;" @click="handleToSend(scope.row)" id="form-list-businessName">{{scope.row.businessName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="formIdentification" label="表单编号" :resizable="false"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" :resizable="false">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                </template>
            </el-table-column>

            <el-table-column prop="processDefinitionKey" label="是否绑定流程" :resizable="false">
                <template slot-scope="scope">
                    <span>{{ scope.row.processDefinitionKey!=null?'绑定流程':'未绑定流程'}}</span>
                </template>
            </el-table-column>


            <el-table-column prop="postStatus" label="发布" :resizable="false" width="120px">
                <template slot-scope="scope">
                    <!-- <span>{{ scope.row.postStatus==0?"未发布":"已发布" }}</span> -->
                    <el-tooltip :content="scope.row.postStatus==0?'未发布':'已发布'" placement="right">
                        <el-switch v-model="scope.row.postStatus" 
                            :active-value="1" 
                            :inactive-value="0" 
                            id="form-index-postStatus"
                            :disabled="switchIsDisabled"
                            @change="publishForm(scope.row)"></el-switch>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="操作" :resizable="false" width="300px">
                <template slot-scope="scope">
                    <el-button
                        size="mini"
                        type="primary"
                        title="编辑表单信息"
                        @click="$router.push({path:'form-edit',query:{data:JSON.stringify(scope.row)}})"
                        icon="el-icon-edit"
                        id="form-list-edit"
                        v-has="'edit-form-list'"
                        v-if="scope.row.postStatus==0"
                    >编辑</el-button>
                    <el-button
                        size="mini"
                        type="success"
                        title="预览表单内容"
                        @click="viewForm(scope.row)"
                        icon="el-icon-view"
                        id="form-list-edit-write"
                        v-has="'edit-form-list-write'"
                        v-if="scope.row.formJson!=null"
                    >预览</el-button>
                    <el-button
                        size="mini"
                        type="danger"
                        title="删除数据"
                        @click="handleToDel(scope.row.id)"
                        icon="el-icon-delete"
                        id="form-list-delete"
                        v-has="'del-form-list'"
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
        <el-dialog title="详情" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
            <el-form :model="content" ref="ruleForm" label-width="80px">
                <el-form-item label="表单名称" prop="key">
                    <span>{{ content.businessName }}</span>
                </el-form-item>
                <el-form-item label="表单编号" prop="name">
                    <span>{{ content.formIdentification }}</span>
                </el-form-item>
                <el-form-item label="创建时间" prop="description">
                    <span>{{ content.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <span>{{ content.description }}</span>
                </el-form-item>
            </el-form>
        </el-dialog>
        <el-dialog title="预览" :visible.sync="formContVisible" width="900px">
            <formFill v-bind:data="viewData" v-if="formContVisible"></formFill>
        </el-dialog>
    </div>
</template>

<script>
import formFill from './children/formFill.vue';
export default {
    components: { formFill },
    data() {
        return {
            // 查询参数
            formContVisible: false,
            dialogVisible: false,
            addDialogFormVisible: false,
            editDialogFormVisible: false,
            processDialogFormVisible: false,
            loading: true,
            // 数据总数
            total: 0,
            //表单列表数据
            tableData: [],
            editData: {},
            formId: "",
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 表单名称
                businessName: ''
            },
            content: {},
            viewData: {},
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('edit-form-list-publish');
        this.getModalList();
    },
    methods: {
        async getModalList() {
            const res = await this.$axios.post('form/getCustomFormList', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
            }
            this.loading = false;
        },
        addModal() {
            this.addDialogFormVisible = true;
        },
        editModal(data) {
            this.editData = data;
            this.editDialogFormVisible = true;
        },
        dialogForm(data) {
            this.addDialogFormVisible = data;
            this.getModalList();
        },
        dialogFormEdit(data) {
            this.editDialogFormVisible = data;
            this.getModalList();
        },
        dialogFormProcess(data) {
            this.processDialogFormVisible = data;
            this.getModalList();
        },
        // 重置
        handleToReset() {
            this.searchInfo.businessName = '';
            this.getModalList();
        },
        // 点击按钮搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getModalList();
        },
        getSysInfoListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.businessName = '';
            this.getModalList();
        },
        handleSizeChange(pageSize) {
            this.pageNum = 1;
            this.searchInfo.pageSize = pageSize;
            this.getModalList();
        },

        handleCurrentChange(pageNum) {
            this.searchInfo.pageNum = pageNum;
            this.getModalList();
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
                    const res = await this.$axios.post('form/deleteCustomForm', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getModalList();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    this.$message.info('已取消删除');
                });
        },
        /**
         * @Author: carl.chai
         * @description: 点击按钮绑定流程
         * @param {id}
         * @return:
         * @Version: 1.0
         */
        bindProcess(id) {
            this.formId = id;
            this.processDialogFormVisible = true;
        },
        viewForm(data) {
            if(data.formJson!=null && JSON.parse(data.formJson).fields!=null && JSON.parse(data.formJson).fields.length>0){
                this.viewData = data;
                this.formContVisible = true;
            }else{
                this.$message.error('表单还未完成设计');
            }
        },
        publishForm(data) {
            var str = data.postStatus==0?"撤消发布":"发布";
            var url = data.postStatus==0?"form/ToUnPublishCustomForm":"form/ToPublishCustomForm";
            if(data.formJson!=null && JSON.parse(data.formJson).fields!=null && JSON.parse(data.formJson).fields.length>0){
                this.$confirm('确定'+str+'吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(async () => {
                        const res = await this.$axios.post(url, { id: data.id });
                        if (res.data.code == 0) {
                            this.$message.success(str+'成功');
                            this.getModalList();
                        } else {
                            this.$message.error(`${res.data.data}`);
                            data.postStatus = data.postStatus==0?1:0
                        }
                    })
                    .catch(() => {
                        data.postStatus = data.postStatus==0?1:0
                    });
            }else{
                this.$message.error('表单还未完成设计');
            }
        },
        handleToSend(data) {
            this.content = data;
            this.dialogVisible = true;
        },
        switchDisabled(item){
            var permissionList = sessionStorage.getItem('btnContext');
            if(permissionList!=null){
                if(permissionList.split(',').indexOf(item)>-1){
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
    margin: 10px;
    text-align: center;
}
.el-switch /deep/ span{
    font-size: 12px;
}
</style>
