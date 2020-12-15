<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 15:25:05
 * @LastEditTime: 2020-06-28 14:56:06
 -->

<template>
    <!-- 新增表格数据的dialog -->
    <div class="box">
        <el-row :gutter="10">
            <el-col :span="16">
                <el-steps :active="active" simple>
                    <el-step title="编辑表单" icon="el-icon-edit"></el-step>
                    <el-step title="设计表单" icon="el-icon-menu"></el-step>
                    <el-step title="绑定流程" icon="el-icon-rank"></el-step>
                </el-steps>
            </el-col>
            <el-col :span="8" style="text-align: right;line-height: 46px;">
                <el-button 
                    type="primary" 
                    @click="$router.push({path:'form-customer',query:{data:$route.query.data}})"
                    id="process-modal-back"
                >上一步</el-button>
                <el-button 
                    type="primary" 
                    @click="$router.push({path:'form-list'})"
                    id="process-modal-bind"
                >完成</el-button>
            </el-col>
        </el-row>

        <div class="line"></div>
        <el-row :gutter="10">
            <el-col :span="4">
                <el-input
                    v-model="searchInfo.processName"
                    placeholder="请输入流程名称"
                    clearable
                    id="process-modal-processName"
                    @keyup.enter.native="handleToSearch"
                ></el-input>
            </el-col>
            <el-col :span="20">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                    id="process-modal-search"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="getSysInfoListRe"
                    id="process-modal-reset"
                >重置</el-button>
                
                
            </el-col>
        </el-row>
        <el-table
            :data="tableData"
            style="width: 100%"
            border
            v-loading="loading"
            height="calc(100% - 192px)"
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
            <el-table-column :resizable="false" prop="id" label="ID"></el-table-column>
            <el-table-column prop="key" label="流程标识" :resizable="false"></el-table-column>
            <el-table-column prop="name" label="流程名称" :resizable="false">
                <template slot-scope="scope">
                    <span
                        @click="viewModal(scope.row)"
                        class="bulletinTitle cursorPoint"
                        id="form-bind-name"
                    >{{scope.row.name}}</span>
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
            <el-table-column label="操作" :resizable="false" width="100px">
                <template slot-scope="scope">
                    <el-button
                        size="mini"
                        type="warning"
                        title="已绑定流程"
                        icon="el-icon-finished"
                        id="process-modal-finished"
                        @click="submitForm(scope.row)"
                        v-if="scope.row.key==bindId"
                    >已绑</el-button>
                    <el-button
                        size="mini"
                        type="primary"
                        title="绑定流程"
                        @click="submitForm(scope.row)"
                        icon="el-icon-rank"
                        id="process-modal-rank"
                        v-has="'edit-process-modal'"
                        v-else
                    >绑定</el-button>
                    
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

        <el-dialog
            title="流程示意图"
            width="900"
            class="ln_dialog"
            :visible.sync="dialogTableVisible"
            @close="close"
            :close-on-click-modal="false"
        >
            <div>
                <div class="img">
                    <img
                        id="images"
                        alt
                        style="margin: 0 auto;display: block;margin-bottom: 20px;width:100%"
                    />
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            // 编辑表单的id
            active: 3,
            bindData: {
                id: '',
                processDefinitionKey: ''
            },
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
            dialogTableVisible: false,
            bindId: ''
        };
    },
    mounted() {
        this.bindId = this.$route.query.bindId;
        this.bindData.id = this.$route.query.data;
        this.bindData.processDefinitionKey = this.$route.query.bindId;
        console.log(this.$route.query.bindId);
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
        /**
         * @Author: flynn.yang
         * @description: 提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        submitForm(val) {
            var msg = '';
            if(this.bindData.processDefinitionKey == val.key){
                msg = '解绑';
                this.bindData.processDefinitionKey = null;
            }else{
                msg = '绑定';
                this.bindData.processDefinitionKey = val.key;
            }
            this.$axios.post('form/toBindProcessDefinition', this.bindData).then(res => {
                if (res.data.code == 0) {
                    this.$message.success(msg+'成功');
                    this.bindId = this.bindData.processDefinitionKey;
                    this.getModalList();
                } else {
                    this.$message.error(msg+'失败');
                }
            });
        },
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
        viewModal(val) {
            let that = this;
            let postDataImg = {
                deploymentId: val.deploymentId
            };
            this.$axios
                .post('bpm/viewPic', postDataImg, {
                    responseType: 'arraybuffer'
                })
                .then(res => {
                    return (
                        'data:image/png;base64,' + btoa(new Uint8Array(res.data).reduce((data, key) => data + String.fromCharCode(key), ''))
                    );
                })
                .then(data => {
                    //这一次箭头函数是依赖于第一次.then函数处理的data值
                    this.dialogTableVisible = true;
                    this.$nextTick(() => {
                        var oStu = document.getElementById('images');
                        oStu.setAttribute('src', data);
                    });
                });
        },
        close() {
            this.dialogTableVisible = false;
        }
    }
};
</script>

<style lang="less" scoped>
/deep/.el-input-number .el-input__inner {
    text-align: left;
}
.el-table {
    margin-top: 20px;
}
.el-pagination {
    margin: 10px;
    text-align: center;
}
.line{
    margin: 18px 0;
    border-top: 1px solid #ccc;
}
.bulletinTitle {
    background: transparent !important;
    border: none !important;
    padding: 0 !important;
    transition: none !important;
    line-height: normal !important;
}
.bulletinTitle:hover {
    color: #409efe;
    font-weight: 700;
}
.cursorPoint{
    cursor: pointer;
}
</style>
