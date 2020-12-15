<template>
    <div class="box">
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane v-for="item in tabData" :label="item.businessName" :key="item.id" :name="item.id">
                <!-- 搜索、新增、 -->
                <div class="search">
                    <el-row :gutter="10">
                        <el-col :span="3">
                            <el-select v-model="searchInfo.fieldColumn" placeholder="请选择搜索类型" clearable>
                                <el-option v-for="(item,index) in tableTitle" :key="index" :label="item.label" :value="item.fieldName">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="3">
                            <el-input v-model="searchInfo.fieldValue" placeholder="请输入内容" clearable></el-input>
                        </el-col>
                        <el-col :span="17">
                            <el-date-picker style="margin-right:10px;" v-model="searchInfo.date" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd">
                            </el-date-picker>
                            <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" id="form-manage-search" @click="handleToSearch">搜索</el-button>
                            <el-button type="primary" title="新增内容" icon="el-icon-plus" @click="toFormFill">新增</el-button>
                            <el-button type="success" title="导出数据" icon="el-icon-download">导出</el-button>
                        </el-col>
                    </el-row>
                </div>
                <!-- 表格 -->
                <el-table :data="tableData" style="width: 100%" :loading="loading" border>
                    <el-table-column v-for="(item,index) in tableTitle" :key="index" :prop="item.fieldName" :label="item.label"></el-table-column>
                    <el-table-column :resizable="false" prop="user_name" label="创建人" width="100"></el-table-column>
                    <el-table-column :resizable="false" prop="create_time" label="创建时间" width="150"></el-table-column>
                    <el-table-column :resizable="false" label="操作" width="400px">
                        <template slot-scope="scope">
                            <el-button size="mini" type="success" title="查看详情" icon="el-icon-view" @click="toFormFill(scope.row, 'view')">详情</el-button>
                            <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="toFormFill(scope.row, 'edit')">编辑</el-button>
                            <el-button size="mini" type="warning" title="下载数据" icon="el-icon-download" v-if="wordFileId!=null&&wordFileId!=''" @click="toDownload(scope.row)">下载</el-button>
                            <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'form-manage-del'" id="form-manage-del">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-tab-pane>
        </el-tabs>
        <!-- 填写 -->
        <el-dialog title="填写" :visible.sync="fillVisible" width="900px" :close-on-click-modal="false">
            <formFill @dialogFill="dialogFormFill" @tableRefrash="tableRefrash" v-bind:data="formDataFill" :parentDataId="parentDataId" v-bind:formId="formId" v-bind:fillId="fillId" v-if="fillVisible"></formFill>
        </el-dialog>
    </div>
</template>

<script>
import formFill from '../customerForm/children/formFill';
export default {
    components: { formFill },
    data() {
        return {
            activeName: '',
            loading: true,
            tabData: [],
            // 搜索条件
            searchInfo: {
                // 当前页
                pageNum: 1,
                // 每页显示的数量
                pageSize: 10,
                id: '',
                // 当前行id
                dataId: '',
                date: '',
                fieldValue: '',
                fieldColumn: ''
            },
            // 表格数据
            tableData: [],
            // 表头数据
            tableTitle: [],
            // ------------
            fillVisible: false,
            formData: {},
            formDataFill: {},
            formId: '',
            fillId: '',
            isShow: false,
            errorMsg: '',
            wordFileId: '',
            fileType: '',
            parentDataId: ''
        };
    },
    created() {
        this.searchInfo.dataId = this.$route.params.rowId;
        this.parentDataId = this.$route.params.rowId;
        this.searchInfo.id = this.$route.params.id;
        this.getSonForm();
    },
    methods: {
        // 获取子表
        async getSonForm(rowId) {
            const res = await this.$axios.post('form/getChildrenCustomTable', {
                formId: this.searchInfo.id
            });
            if (res.data.code == 0) {
                this.tabData = res.data.data;
                this.activeName = res.data.data[0].id;
                this.searchInfo.id = res.data.data[0].id;
                let title = this.tableTitleFilter(res.data.data[0].formJson);
                this.tableTitle = title;
                this.getTableData();
                this.getForm();
            }
        },
        // 获取表头
        async getTableData() {
            const res = await this.$axios.post('formData/getDataList', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        // tab点击事件
        handleClick(tab) {
            this.tabData.forEach(async (item) => {
                if (item.id == tab.name) {
                    const res = await this.$axios.post('form/getFormByIdentification', {
                        id: item.formIdentification
                    });
                    this.searchInfo.id = res.data.data.id;
                    this.formData = res.data.data;
                    let title = this.tableTitleFilter(res.data.data.formJson);
                    this.tableTitle = title;
                    this.getTableData();
                }
            });
        },
        // 获取表头方法
        tableTitleFilter(val) {
            let arr = [];
            JSON.parse(val).fields.forEach((item) => {
                if (item.__config__.showStatistic == null || item.__config__.showStatistic == true) {
                    arr.push({
                        fieldName: item.__vModel__,
                        label: item.__config__.label,
                        fieldType: item.__config__.tag
                    });
                }
            });
            return arr;
        },
        // 搜索
        async handleToSearch() {
            let obj = {};
            obj.id = this.searchInfo.id;
            obj.fieldColumn = this.searchInfo.fieldColumn;
            obj.fieldValue = this.searchInfo.fieldValue;
            obj.startTime = this.searchInfo.date[0];
            obj.endTime = this.searchInfo.date[1];
            obj.pageNum = this.searchInfo.pageNum;
            obj.pageSize = this.searchInfo.pageSize;
            const res = await this.$axios.post('formData/getDataList', obj);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                let arr = [];
                JSON.parse(this.formData.formJson).fields.forEach((item) => {
                    if (item.__config__.showStatistic == null || item.__config__.showStatistic == true) {
                        arr.push({
                            fieldName: item.__vModel__,
                            label: item.__config__.label,
                            fieldType: item.__config__.tag
                        });
                    }
                });
                this.tableTitle = arr;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        async getForm() {
            const res = await this.$axios.post('form/getFormByIdentification', {
                id: this.tabData[0].formIdentification
            });
            this.formData = res.data.data;
        },
        // 填写表单
        toFormFill(val, type) {
            var _formId = '';
            var data = {};

            if (type === 'view') {
                _formId = null;
            } else {
                _formId = this.searchInfo.id;
            }
            for (let key in this.formData) {
                data[key] = this.formData[key];
            }
            if (val != null) {
                var fillData = JSON.parse(data.formJson);
                fillData.fields.forEach((item) => {
                    console.log(val[item.__vModel__]);
                    if (val[item.__vModel__] != null && val[item.__vModel__].indexOf('[') > -1 && item.__config__.tag != 'el-upload') {
                        item.__config__.defaultValue = eval('(' + val[item.__vModel__] + ')');
                    } else {
                        item.__config__.defaultValue = val[item.__vModel__];
                    }
                });
                data.formJson = JSON.stringify(fillData);
                this.fillId = val.id;
            } else {
                this.fillId = '';
            }
            this.formId = _formId;
            this.formDataFill = data;
            this.fillVisible = true;
        },
        // dialog关闭
        dialogFormFill() {
            this.fillVisible = false;
        },
        // 刷新表格
        tableRefrash() {
            this.getTableData();
        },
        // 删除
        handleToDel(id) {
            this.$confirm('确定删除该内容吗?此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('formData/deleteData', { formId: this.searchInfo.id, dataId: id });
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
        }
    }
};
</script>

<style lang="less" scoped>
.search {
    margin-bottom: 20px;
}
</style>
