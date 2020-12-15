<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:04:34
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:05:38
 * @Description: 角色管理
 * @Version: 1.0
 -->
 
<template>
  <div class="box">
    <!-- 表格区域 -->
    <el-row :gutter="10">
      <!-- 下拉框 -->
      <el-col :span="4">
        <el-input id="system-role-orgName" v-model="searchInfo.orgName" placeholder="请输入角色名称" clearable  @keyup.enter.native="enterSearch"></el-input>
      </el-col>
      <el-col :span="4">
        <el-input id="system-role-orgCode" v-model="searchInfo.orgCode" placeholder="请输入角色编码" clearable  @keyup.enter.native="enterSearch"></el-input>
      </el-col>
      <el-col :span="16">
        <el-button id="system-role-search" type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch">搜索</el-button>
        <el-button id="system-role-reset" type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset">重置</el-button>
        <el-button id="system-role-add" type="primary" title="新增角色" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo,moduleList)" v-has="'system-role-add'">新增</el-button>
      </el-col>
    </el-row>
    <!-- <el-row>
      <el-col> -->
        <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
          <el-table-column label="序号" align="center" type="index" width="50">
            <template slot-scope="scope">
              {{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}
            </template>
          </el-table-column>
          <el-table-column prop="roleName" label="角色名称" :resizable="false"></el-table-column>
          <el-table-column prop="roleCode" label="角色编码" :resizable="false"></el-table-column>
          <el-table-column prop="remark" label="备注" :resizable="false" show-overflow-tooltip></el-table-column>
          <el-table-column :resizable="false" label="状态" width="100">
            <template slot-scope="scope">
                <el-tooltip :content="scope.row.inUse==0?'禁用':'启用'" placement="right">
                    <el-switch v-model="scope.row.inUse" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" id="system-role-state" :disabled="switchIsDisabled"></el-switch>
                </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column :resizable="false" label="操作" width="350px">
            <template slot-scope="scope">
              <el-button id="system-role-edit" size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row,moduleList)" v-has="'system-role-edit'">编辑</el-button>
              <el-button id="system-role-permission" type="warning" icon="el-icon-key" title="为角色分配权限" @click="$router.push({path:'system-role-permission',query:{roleId:scope.row.id}})" v-has="'system-role-permission'">权限分配</el-button>
              <!-- <el-button id="system-role-user" type="info" icon="el-icon-user-solid" title="为角色批量分配用户" @click="$router.push({path:'system-role-allocation',query:{roleId:scope.row.id}})" v-has="'system-role-user'">用户</el-button>
              <el-button id="system-role-menu" type="warning" icon="el-icon-menu" title="为角色分配可访问的系统菜单" @click="$refs.menu.openDialog(scope.row,menuData)" v-has="'system-role-menu'">菜单</el-button>
              <el-button id="system-role-btn" type="success" icon="el-icon-set-up" title="为角色分配页面中的操作权限" @click="$refs.btn.openDialog(scope.row,btnData)" v-has="'system-role-btn'">操作</el-button>
              <el-button id="system-role-data" type="primary" icon="el-icon-share" title="为角色分配数据权限" @click="$refs.data.openDialog(scope.row)" v-has="'system-role-data'">数据</el-button> -->
              <el-button id="system-role-del" size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-role-del'">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页区域 -->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
      <!-- </el-col>
    </el-row> -->
    <!-- 新增字典的子组件 -->
    <Add ref="add" @getTableData="getTableData"></Add>
    <!-- 编辑字典的子组件 -->
    <Edit ref="edit" @getTableData="getTableData"></Edit>
    <!-- 菜单分配的子组件 -->
    <!-- <Menu ref="menu"></Menu> -->
    <!-- 数据分配的子组件 -->
    <!-- <Data ref="data"></Data> -->
    <!-- 按钮分配的子组件 -->
    <!-- <Btn ref="btn"></Btn> -->
  </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
export default {
    // 注册子组件
    components: { Add, Edit},
    data() {
        return {
            // 查询参数
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 角色名称
                orgName: '',
                // 角色编码
                orgCode: ''
            },
            //配置数据列表
            tableData: [],
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            // 菜单树列表
            menuData: [],
            // 按钮树列表
            btnData: [],
            loading: true,
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-role-state');
        // 获取角色列表
        this.getTableData();
        // 获取菜单树
        this.getTreeData();
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取角色列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTableData() {
            this.loading = true;
            const res = await this.$axios.post('sys/role/list', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        // 获取菜单树
        async getTreeData() {
            let info = {
                roleId: 1,
                type: 2
            };
            const res = await this.$axios.post('sys/menu/treeData', info);
            if (res.data.code == 0) {
                this.menuData = res.data.data;
            }
            const res2 = await this.$axios.post('sys/menu/btnTreeData', {});
            if (res2.data.code == 0) {
                // this.btnData = res2.data.data;
            }
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
            const res = await this.$axios.post('sys/role/setInUse', info);

            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
                row.inUse = row.isShow == 1 ? 0 : 1
            }
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
            this.searchInfo.userName = '';
            this.searchInfo.module = '';
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
                    const res = await this.$axios.post('sys/role/delete', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.data}`);
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
        // 重置
        handleToReset() {
            this.searchInfo.orgName = '';
            this.searchInfo.orgCode = '';
            this.getTableData();
        },
        // 点击按钮搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getTableData();
        }, //input回车搜索
        enterSearch() {
            this.handleToSearch();
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
    text-align: center;
    margin-top: 20px;
}
</style>

