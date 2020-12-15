<template>
    <div class="box">
        <div class="box" v-if="isShow">
            <div style="">{{errorMsg}}</div>
        </div>
        <div class="box" v-else>
            <el-row :gutter="10">
                <el-col :span="3">
                    <el-select v-model="searchInfo.fieldColumn" placeholder="请选择搜索类型" clearable>
                        <el-option v-for="(item,index) in selectData" :key="index" :label="item.label" :value="item.value">
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
                    <el-button type="primary" title="新增内容" icon="el-icon-plus" @click="toFormFill()" v-has="'form-manage-add'" id="form-manage-add">新增</el-button>
                    <el-button type="success" title="导出数据" icon="el-icon-download" @click="toDownloadAll()" v-has="'form-manage-download'" id="form-manage-download">导出</el-button>
                    <el-button type="primary" title="高级搜索" icon="el-icon-s-operation" @click="drawer = true">高级搜索</el-button>
                </el-col>
            </el-row>
            <el-table :data="tableData" style="width: 100%;margin-top:20px;" height="calc(100% - 110px)" border v-loading="loading">
                <el-table-column label="序号" align="center" type="index" width="50">
                    <template slot-scope="scope">{{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}</template>
                </el-table-column>
                <el-table-column :resizable="false" v-for="(item, index) in tableTitle" :key="index" :prop="item.fieldName" :label="item.label">
                    <template slot-scope="scope">
                        <div v-if="item.fieldType=='el-upload'">
                            <div class="bulletinTitle cursorPoint" v-for="file in getFillList(scope.row[item.fieldName])" :key="file.id" @click="fileDownload(file)" title="点击下载">{{ file.name }}</div>
                        </div>
                        <div v-else>
                            {{ scope.row[item.fieldName] }}
                        </div>
                        <!-- <div class="bulletinTitle cursorPoint" v-for="file in eval('('+scope.row[item.fieldName]+')')" :key="file.id" @click="fileDownload(file)" title="点击下载">{{ file.name }}</div> -->
                    </template>
                </el-table-column>
                <el-table-column :resizable="false" prop="user_name" label="创建人" width="100"></el-table-column>
                <el-table-column :resizable="false" prop="create_time" label="创建时间" width="150"></el-table-column>
                <el-table-column :resizable="false" label="操作" width="400px">
                    <template slot-scope="scope">
                        <el-button size="mini" type="success" title="查看详情" icon="el-icon-view" @click="toFormFill(scope.row, 'view')" v-has="'form-manage-edit'" id="form-manage-edit">详情</el-button>
                        <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="toFormFill(scope.row, 'edit')" v-has="'form-manage-edit'" id="form-manage-edit">编辑</el-button>
                        <el-button size="mini" type="warning" title="下载数据" icon="el-icon-download" v-if="wordFileId!=null&&wordFileId!=''" @click="toDownload(scope.row)" v-has="'form-manage-edit'" id="form-manage-edit">下载</el-button>
                        <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'form-manage-del'" id="form-manage-del">删除</el-button>
                        <el-button size="mini" type="warning" title="子业务" icon="el-icon-connection" v-if="show" @click="handleToConnection(scope.row.id)">子业务</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页区域 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        </div>
        <!-- 填写 -->
        <el-dialog title="填写" :visible.sync="fillVisible" width="900px" :close-on-click-modal="false">
            <formFill @dialogFill="dialogFormFill" @tableRefrash="tableRefrash" v-bind:data="formDataFill" v-bind:formId="formId" v-bind:fillId="fillId" v-if="fillVisible"></formFill>
        </el-dialog>
        <!-- 查看子表 -->
        <!-- <sonDialog ref="sonDialog" :son-info="searchInfo"></sonDialog> -->
        <!-- 高级搜索 -->
        <el-drawer title="高级搜索" :before-close="handleClose" :visible.sync="drawer" direction="rtl" ref="drawer">
            <el-scrollbar>
                <div class="demo-drawer__content">
                    <el-row :gutter="10" v-for="(item,index) in AdvancedSearch" :key="index" style="margin-bottom:10px;">
                        <el-col :span="12">
                            <el-select v-model="item.type" placeholder="请选择搜索类型" clearable style="width:100%" @change="disabledItem">
                                <!-- <el-option v-for="(item,index) in selectData" :key="index" :label="item.label" :value="item.value" :disabled="item.disabled"> -->
                                <el-option v-for="(item,index) in selectData" :key="index" :label="item.label" :value="item.value">
                                </el-option>
                            </el-select>
                        </el-col>
                        <el-col :span="11">
                            <el-input v-model="item.value" placeholder="请输入内容" clearable></el-input>
                        </el-col>
                        <el-col :span="1" style="line-height:32px;" class="cursorPoint">
                            <i class="el-icon-circle-plus-outline" @click="addCondition" v-if="index == 0"></i>
                            <i class="el-icon-remove-outline" @click="subtractCondition(index)" v-else></i>
                        </el-col>
                    </el-row>
                    <!-- 搜索or重置 -->
                    <div class="demo-drawer__footer">
                        <el-button type="primary" plain @click="resetAdvancedSearch">重 置</el-button>
                        <el-button type="primary" @click="search">搜 索</el-button>
                    </div>
                </div>
            </el-scrollbar>
        </el-drawer>
    </div>
</template>

<script>
import formFill from '../customerForm/children/formFill';
// import sonDialog from './manageSon';
export default {
    // 注册子组件
    components: { formFill },
    data() {
        return {
            // 搜索条件
            searchInfo: {
                // 当前页
                pageNum: 1,
                // 每页显示的数量
                pageSize: 10,
                id: '',
                date: '',
                fieldValue: '',
                fieldColumn: ''
            },
            // 表格绑定的数据
            tableTitle: [],
            // 表格绑定的数据
            tableData: [],
            // 选择器数据
            selectData: [],
            // 总条数
            total: 0,
            loading: true,
            fillVisible: false,
            formData: {},
            formDataFill: {},
            formId: '',
            fillId: '',
            isShow: false,
            errorMsg: '',
            wordFileId: '',
            fileType: '',
            // 控制是否显示查看子表
            show: false,
            // 当前表id
            id: '',
            // 高级搜索
            drawer: false,
            AdvancedSearch: [
                {
                    type: '',
                    value: ''
                }
            ]
        };
    },
    created() {
        this.getformData(this.$route.params.id);
    },
    beforeRouteEnter(to, from, next) {
        var formManageName = sessionStorage.getItem('formManageName');
        if (formManageName != null) {
            JSON.parse(formManageName).forEach((item) => {
                if (item.urlParam == to.params.id) {
                    to.meta.title = item.name;
                }
            });
        }
        next(); //继续
    },
    beforeRouteUpdate(to, from, next) {
        var formManageName = sessionStorage.getItem('formManageName');
        if (formManageName != null) {
            JSON.parse(formManageName).forEach((item) => {
                if (item.urlParam == to.params.id) {
                    to.meta.title = item.name;
                }
            });
        }
        this.tableTitle = [];
        this.getformData(to.params.id);
        next(); //继续
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取树结构数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getformData(id) {
            const res = await this.$axios.post('form/getFormByIdentification', { id });
            if (res.data.code == 0) {
                this.isShow = false;
                this.formData = res.data.data;
                this.searchInfo.id = res.data.data.id;
                this.wordFileId = res.data.data.wordFileId;
                this.fileType = res.data.data.fileType;
                this.id = res.data.data.id;
                res.data.data.isOwnChildren == '0' ? (this.show = false) : (this.show = true);
                this.getTableData();
            } else {
                this.isShow = true;
                this.errorMsg = res.data.data;
                this.$message.error(res.data.data);
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTableData() {
            const res = await this.$axios.post('formData/getDataList', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                let arr = [];
                let arr2 = [];
                JSON.parse(this.formData.formJson).fields.forEach((item) => {
                    if (item.__config__.showStatistic == null || item.__config__.showStatistic == true) {
                        arr.push({
                            fieldName: item.__vModel__,
                            label: item.__config__.label,
                            fieldType: item.__config__.tag
                        });
                        arr2.push({
                            value: item.__vModel__,
                            label: item.__config__.label,
                            disabled: false
                        });
                    }
                });
                this.tableTitle = arr;
                this.selectData = arr2;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮删除表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
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
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换每页显示的数量
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(newSize) {
            this.searchInfo.pageSize = newSize;
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换当前页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(newNum) {
            this.searchInfo.pageNum = newNum;
            this.getTableData();
        },
        dialogFormFill() {
            this.fillVisible = false;
        },
        // 发起跳转自定义表单填写页面
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
        // 发起跳转自定义表单填写页面
        toDownloadAll() {
            this.$confirm('确定导出全部数据！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    var time = this.$moment(new Date()).format('YYYYMMDDHHmmss');
                    this.$download.downloadFile(
                        'formData/exportCustomFormData',
                        { id: this.searchInfo.id },
                        this.formData.businessName + '.' + time + '.xls'
                    );
                })
                .catch(() => {
                    this.$message.info('已取消导出');
                });
        },
        // 发起跳转自定义表单填写页面
        toDownload(val) {
            this.$confirm('确定导出本条数据！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    var time = this.$moment(new Date()).format('YYYYMMDDHHmmss');
                    this.$download.downloadFile(
                        'formData/exportCustomFormDataToWord',
                        { formId: this.searchInfo.id, dataId: val.id },
                        this.formData.businessName + '.' + time + '.' + this.fileType
                    );
                })
                .catch(() => {
                    this.$message.info('已取消导出');
                });
        },
        // 发起跳转自定义表单填写页面
        fileDownload(val) {
            this.$confirm('确定下载该文件！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    this.$download.downloadFile('download/downloadFile', { fileId: val.id }, val.name);
                })
                .catch(() => {
                    this.$message.info('已取消下载');
                });
        },
        tableRefrash() {
            this.getTableData();
        },
        getFillList(val) {
            if (val == null || val == '') {
                return val;
            } else {
                return eval('(' + val + ')');
            }
        },
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
        // 点击查看子表
        // async handleToConnection(rowId) {
        //     const res = await this.$axios.post('form/getChildrenCustomTable', {
        //         formId: this.id
        //     });
        //     let sonId = res.data.data[0].id;
        //     this.$refs.sonDialog.openDialog(rowId, sonId);
        // }
        handleToConnection(rowId) {
            // this.$refs.sonDialog.openDialog(rowId,this.id);
            this.$router.push({
                name: 'sublist',
                params: {
                    rowId: rowId,
                    id: this.id
                }
            });
        },
        handleClose(done) {
            this.$confirm('确认关闭？')
                .then((_) => {
                    done();
                })
                .catch((_) => {});
        },
        // 点击+添加搜索条件
        addCondition() {
            this.AdvancedSearch.push({ type: '', value: '' });
        },
        // 点击-删除搜索条件
        subtractCondition(i) {
            // this.AdvancedSearch[i].disabled = false;
            this.AdvancedSearch.splice(i, 1);
        },
        // 点击重置高级搜索
        resetAdvancedSearch() {
            this.AdvancedSearch.forEach((item) => {
                item.value = '';
            });
        },
        // 选择器禁用已选项
        disabledItem(val) {
            this.selectData.forEach((item) => {
                // if (item.value == val) {
                //     item.disabled = true;
                // }
            });
        },
        async search() {
            this.loading = true
            // console.log(this.AdvancedSearch);
            let fieldColumns = [];
            let fieldValues = [];
            this.AdvancedSearch.forEach((item) => {
                item.type && fieldColumns.push(item.type);
                item.type && fieldValues.push(item.value);
            });
            let obj = {};
            obj.id = this.searchInfo.id;
            obj.fieldColumn = fieldColumns.join(',');
            obj.fieldValue = fieldValues.join(',');
            obj.startTime = this.searchInfo.date[0];
            obj.endTime = this.searchInfo.date[1];
            obj.pageNum = this.searchInfo.pageNum;
            obj.pageSize = this.searchInfo.pageSize;
            const res = await this.$axios.post('formData/getDataList',obj);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.loading = false;
            }else {
                this.loading = false;
            }
        }
    },
    watch: {
        customName(val) {
            this.getTreeData(val);
        }
    }
};
</script>

<style lang="less" scoped>
.el-pagination {
    margin: 10px;
    text-align: center;
}
/* 详情btn*/
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
.cursorPoint {
    cursor: pointer;
}
/deep/.el-drawer__body {
    height: calc(~'100% - 75px') !important;
}
.el-scrollbar {
    height: 100% !important;
}
.demo-drawer__content {
    height: 100%;
    padding: 20px;
}
.demo-drawer__footer {
    margin-top: 40px;
    text-align: center;
}
// 去除Drawer Title的黑色边框
/deep/ :focus {
    outline: 0;
}
</style>