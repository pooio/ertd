<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:55:09
 * @Description: 用户管理
 * @Version: 1.0
 -->
<template>
    <div class="box">
        <el-row :gutter="20" class="box">
            <!-- 树结构 -->
            <el-col :span="4" class="box-l box">
                <el-input :placeholder="$t('i18n.filter')" v-model="filterText" clearable style="margin-bottom:20px"></el-input>
                <el-scrollbar style="height:calc(100% - 100px);">
                    <el-tree class="filter-tree" id="system-user-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
                </el-scrollbar>
            </el-col>
            <!-- 表格区域 -->
            <el-col :span="20" class="box-r box">
                <el-row :gutter="15" class="box-r-search">
                    <el-col :span="4">
                        <el-input v-model="name" id="system-user-name" :placeholder="$t('i18n.inputName')" clearable @keyup.enter.native="nameSearch"></el-input>
                    </el-col>
                    <el-col :span="4">
                        <el-input v-model="loginName" id="system-user-loginName" :placeholder="$t('i18n.inputLoginName')" clearable @keyup.enter.native="loginSearch"></el-input>
                    </el-col>
                    <el-col :span="10">
                        <el-button type="primary" id="system-user-search" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch">{{ $t('i18n.search') }}</el-button>
                        <el-button type="primary" id="system-user-reset" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset">{{ $t('i18n.reset') }}</el-button>
                        <el-button type="primary" id="system-user-add" title="为已选中的组织机构添加用户" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo, selectData, postData)" v-has="'system-user-add'">{{ $t('i18n.add') }}</el-button>
                    </el-col>
                </el-row>
                <!-- <el-row class="box-r-table box">
                    <el-col :span="24" class="table box"> -->
                <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
                    <el-table-column :resizable="false" :label="$t('i18n.number')" align="center" type="index" width="50">
                        <template slot-scope="scope">{{ scope.$index + 1 + pageSize * (pageNum - 1) }}</template>
                    </el-table-column>
                    <el-table-column :resizable="false" prop="name" :label="$t('i18n.name')">
                        <template slot-scope="scope">
                            <span class="bulletinTitle cursorPoint" @click="handleToSend(scope.row)">{{ scope.row.name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :resizable="false" prop="loginName" :label="$t('i18n.loginName')">
                        <template slot-scope="scope">
                            <span class="bulletinTitle cursorPoint" @click="handleToSend(scope.row)">{{ scope.row.loginName }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :resizable="false" :label="$t('i18n.Gender')">
                        <template slot-scope="scope">{{ scope.row.sex == 0 ? '女' : '男' }}</template>
                    </el-table-column>
                    <el-table-column :resizable="false" :label="$t('i18n.state')" width="120">
                        <template slot-scope="scope">
                            <el-tooltip :content="scope.row.status==0?'禁用':'启用'" placement="right">
                                <el-switch v-if="scope.row.loginName != 'admin'" id="system-user-status" v-model="scope.row.status" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" :disabled="switchIsDisabled"></el-switch>
                                <el-switch v-if="scope.row.loginName == 'admin'" id="system-user-status" v-model="scope.row.status" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" :disabled=true></el-switch>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column :resizable="false" :label="$t('i18n.operation')" width="320px">
                        <template slot-scope="scope">
                            <el-button id="system-user-edit" size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row, selectData, postData, treeData)" v-has="'system-user-edit'">编辑</el-button>
                            <el-button id="system-user-resetPwd" size="mini" type="warning" title="重置用户密码" icon="el-icon-refresh" @click="handleToPwdReset(scope.row.id)" v-has="'system-user-reset'">重置密码</el-button>
                            <el-button v-if="scope.row.loginName != 'admin'" id="system-user-del" size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-user-del'">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页区域 -->
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
                <!-- </el-col>
                </el-row> -->
            </el-col>
        </el-row>
        <!-- 新增用户的子组件 -->
        <Add ref="add" @getTableData="getTableData" @getTreeData="getTreeData"></Add>
        <!-- 编辑用户子组件 -->
        <Edit ref="edit" @getTableData="getTableData" @getTreeData="getTreeData"></Edit>
        <!-- 登录名查看详情 -->
        <el-dialog title="提示" :visible.sync="dialogVisible" width="40%" :before-close="handleClose" class="userConter">
            <span slot="title" class="dialog-footer">
                <div class="gonggao">查看内容</div>
                <div class="biaoti">登录名：{{contentData.loginName}}</div>
            </span>
            <el-row class="info">
                <el-col :span="10" :offset="4">
                    <span>
                        姓名:
                        <span v-text="contentData.name"></span>
                    </span>
                </el-col>
                <el-col :span="10">
                    <span>
                        性别:
                        <span v-text="contentData.sex== 1?'男':'女'"></span>
                    </span>
                </el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="10" :offset="4">
                    <span>
                        手机号:
                        <span v-text="contentData.phoneNumber"></span>
                    </span>
                </el-col>
                <el-col :span="10">
                    <span>
                        工号:
                        <span v-text="contentData.userNo"></span>
                    </span>
                </el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="10" :offset="4">
                    <span>
                        所属单位:
                        <span v-text="contentData.orgName"></span>
                    </span>
                </el-col>
                <el-col :span="10">
                    <span>
                        邮箱:
                        <span v-text="contentData.email"></span>
                    </span>
                </el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="20" :offset="4">
                    <span>
                        职务:
                        <span v-text="contentData.postName"></span>
                    </span>
                </el-col>
            </el-row>
            <el-row class="info">
                <el-col :span="2" :offset="4">
                    角色:
                </el-col>
                <el-col :span="20">
                    <el-tag v-for="tag in roleList" :key="tag.id" type="info" class="tagStyle">{{tag.roleName}}</el-tag>
                </el-col>
            </el-row>
        </el-dialog>
    </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
export default {
    // 注册子组件
    components: {
        Add,
        Edit
    },
    data() {
        return {
            // 树结构绑定的数据
            treeData: [],
            // 树结构配置项
            defaultProps: {
                children: 'children',
                label: 'orgName'
            },
            // 过滤树结构的关键字
            filterText: '',
            // 搜索条件
            name: '',
            // 搜索条件
            loginName: '',
            // 表格绑定的数据
            tableData: [],
            // 当前页
            pageNum: 1,
            // 每页显示的数量
            pageSize: 10,
            // 总条数
            total: 0,
            // 父节点id
            parentId: '',
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            // 角色选择器数据
            selectData: [],
            // 职务选择器数据
            postData: [],
            loading: true,
            dialogVisible: false, //详情
            contentData: [], //详情数据,
            roleList: [], //详情角色数组
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-user-state');
        this.getTreeData();
        // 获取角色列表
        this.getRoleList();
        console.log(sessionStorage.getItem('btnContext').indexOf('system-menu-ad'));
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取树结构数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTreeData() {
            const res = await this.$axios.post('sys/org/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                this.parentId = res.data.data[0].id;
                this.addInfo = res.data.data[0].children[0];
                this.getTableData();
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
            let info = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                orgId: this.parentId,
                loginName: this.loginName,
                name: this.name
            };
            const res = await this.$axios.post('sys/user/list', info);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮重置input
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToReset() {
            this.loginName = '';
            this.name = '';
            this.filterText = '';
            this.pageNum = 1;
            this.pageSize = 10;
            this.getTableData();
            this.getTreeData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 输入关键字过滤
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        filterNode(value, data) {
            if (!value) return true;
            return data.orgName.indexOf(value) !== -1;
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮删除表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToDel(id) {
            this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/user/batch/delete', {
                        id
                    });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                        // this.getTreeData();
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
         * @description: 用户是否禁用
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async handleToChange(row) {
            console.log(row);
            let info = {
                id: row.id,
                status: row.status == 1 ? 1 : 0
            };
            const res = await this.$axios.post('sys/user/setStatus', info);
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
                row.inUse = row.isShow == 1 ? 0 : 1;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换每页显示的数量
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(newSize) {
            this.pageSize = newSize;
            this.getTreeData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换当前页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(newNum) {
            this.pageNum = newNum;
            this.getTreeData();
        },
        /**
         * @Author: Misaka.chen
         * @description:
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToTreeClick(data) {
            // if (data.children.length == 0) {
            //     return false;
            // } else {
            this.parentId = data.id;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取角色列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getRoleList() {
            const res = await this.$axios.post('sys/role/allList');
            if (res.data.code == 0) {
                this.selectData = res.data.data;
                this.getPostList();
            }
            // console.log(res);
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取职务列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getPostList() {
            const res = await this.$axios.post('sys/post/allList');
            if (res.data.code == 0) {
                this.postData = res.data.data;
            }
            // console.log(res);
        },
        // 点击按钮重置密码
        handleToPwdReset(id) {
            this.$confirm('此操作将重置该用户密码, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/user/resetPwd', {
                        id: id,
                        password: ''
                    });
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data},新密码为: 123456`);
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消重置'
                    });
                });
        },
        // 点击按钮搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTableData();
        }, //input姓名回车搜索
        nameSearch() {
            this.handleToSearch();
        }, //input登录名回车搜索
        loginSearch() {
            this.handleToSearch();
        },
        // diaolog关闭回调
        handleClose() {
            this.dialogVisible = false;
        },
        // 点击查看用户信息详情
        async handleToSend(data) {
            this.contentData = data;
            this.dialogVisible = true;
            this.contentData.roleName = ''; //角色字符串
            this.roleList = [];
            for (var i = 0; i < this.selectData.length; i++) {
                for (var j = 0; j < this.contentData.roleCodes.length; j++) {
                    if (this.selectData[i].id == this.contentData.roleCodes[j]) {
                        this.roleList.push(this.selectData[i]);
                    }
                }
            }
        },
        switchDisabled(item) {
            var permissionList = sessionStorage.getItem('btnContext');
            if (permissionList != null) {
                if (permissionList.split(',').indexOf(item) > -1) {
                    this.switchIsDisabled = false;
                }
            }
        }
    },
    watch: {
        filterText(val) {
            this.$refs.tree.filter(val);
        }
    }
};
</script>

<style lang="less" scoped>
@import '../../../assets/css/style.css';
.el-col {
    margin-bottom: 20px;
}

.el-pagination {
    text-align: center;
    margin-top: 20px;
}

/deep/.el-tree-node.is-current > .el-tree-node__content {
    background-color: #2f8dfb !important;
    color: white;
}

.el-checkbox__input.is-checked + .el-checkbox__label {
    color: black;
}

/deep/.el-scrollbar__view {
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree {
    display: inline-block;
}
</style>