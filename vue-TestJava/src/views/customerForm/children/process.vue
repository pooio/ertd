<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 15:25:05
 * @LastEditTime: 2020-06-28 14:56:06
 -->

<template>
    <!-- 新增表格数据的dialog -->
    <div class="box">
        <el-table
            :data="tableData"
            style="width: 100%"
            border
            v-loading="loading"
            height="calc(100% - 110px)"
            @row-click="handleSelectionChange"
        >
            <el-table-column label="选择" width="50" align="center">
                <template slot-scope="scope">
                    <el-radio class="radio" v-model="radio" :label="scope.$index">&nbsp;</el-radio>
                </template>
            </el-table-column>
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
            <el-table-column :resizable="false" prop="id" label="ID"></el-table-column>
            <el-table-column prop="key" label="流程标识" :resizable="false"></el-table-column>
            <el-table-column prop="name" label="流程名称" :resizable="false"></el-table-column>
            <el-table-column prop="version" label="版本" :resizable="false"></el-table-column>
            <el-table-column prop="deploymentId" label="部署ID" :resizable="false"></el-table-column>
            
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

        <div class="dialog-footer">
            <el-button
                type="text"
                @click="resetForm('ruleForm')"
            >取 消</el-button>
            <el-button
                type="primary"
                @click="submitForm('ruleForm')"
            >确 定</el-button>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        processFormId: ''
    },
    data() {
        return {
            // 编辑表单的id
            bindData: {
                id: '',
                processDefinitionKey: ''
            },
            radio: '',
            tableData: [],
            loading: true,
            // 数据总数
            total: 0,
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 流程名称
                processName: '',
                type: 2
            },
        };
    },
    mounted() {
        this.bindData.id = this.processFormId;
        this.getModalList();
    },
    methods: {
        async getModalList() {
            const res = await this.$axios.post('bpm/modeler/list', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
            }
            this.loading = false;
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
        handleSelectionChange(val) {
            this.bindData.processDefinitionKey = val.key;
        },
        /**
         * @Author: flynn.yang
         * @description: 提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        submitForm(formName) {
            if (this.bindData.processDefinitionKey=='') {
                this.$message.error('未选择流程');
            } else {
                this.$axios.post('form/toBindProcessDefinition', this.bindData).then(res => {
                    if (res.data.code == 0) {
                        this.$message.success(res.data.data);
                        this.$emit('dialogProcess', false);
                    } else {
                        this.$message.error('绑定失败');
                    }
                    console.log(res);
                });
            }
        },

        resetForm(formName) {
            this.formId = '';
            this.$emit('dialogProcess', false);
        }
    }
};
</script>

<style lang="less" scoped>
.dialog-footer {
    text-align: right;
}
/deep/.el-input-number .el-input__inner {
    text-align: left;
}
</style>
