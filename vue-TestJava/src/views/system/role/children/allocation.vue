<template>
  <div class="box">

      <el-row :gutter="20" class="box">
        <el-col :span="6" class="box">
          <el-input id="system-allocation-tree" placeholder="请输入关键字进行过滤" v-model="filterText" style="margin-bottom:20px" clearable></el-input>
          <el-scrollbar style="height:calc(100% - 100px);">
            <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
          </el-scrollbar>
        </el-col>
        <el-col :span="18" class="box">
          <el-row :gutter="20" style="margin-bottom:20px">
            <el-col :span="6">
              <el-input id="system-allocation-name" v-model="searchInfo.name" placeholder="请输入姓名" clearable></el-input>
            </el-col>
            <el-col :span="18">
              <el-button id="system-allocation-search" title="根据输入条件搜索" icon="el-icon-search" type="primary" @click="handleToSearch">搜索</el-button>
              <el-button id="system-allocation-reset" title="清空搜索条件" plain icon="el-icon-refresh" type="primary" @click="handleToReset">重置</el-button>
              <el-button id="system-allocation-add" title="新增用户" icon="el-icon-plus" type="primary" @click="handleChangeIsAdd">新增</el-button>
            </el-col>
          </el-row>
          <el-table ref="multipleTable" :data="tableData" border tooltip-effect="dark" style="height:calc(100% - 100px);" @selection-change="handleSelectionChange" v-loading="loading">
            <el-table-column type="index" label="序号" width="50" align="center">
            </el-table-column>
            <el-table-column prop="name" label="姓名">
            </el-table-column>
            <el-table-column prop="orgName" label="部门">
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button id="system-allocation-del" size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        </el-col>
      </el-row>
    <!-- 新增分配角色的子组件 -->
    <el-dialog title="分配用户" :visible.sync="isAdd" :close-on-click-modal="false" width="900px">
        <addAllocation @handleChangeIsAdd="handleChangeIsAdd" :treeData="treeData" @getTableData="getTableData"></addAllocation>
    </el-dialog>
  </div>
</template>

<script>
import addAllocation from './addAllocation';
export default {
    components: {
        addAllocation
    },
    props: {
        roleId: ''
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
            loading: true,
            tableData: [],
            searchInfo: {
                // 角色Id
                roleId: '',
                // 部门id
                orgId: '',
                // 姓名
                name: '',
                // 当前页
                pageNum: 1,
                // 每页显示的数量
                pageSize: 10
            },
            // 数据总数
            total: 0,
            multipleSelection: [],
            isAdd: false
        };
    },
    created() {
        this.searchInfo.roleId = this.$route.query.roleId;
        this.getTreeData();
    },
    methods: {
        // 获取部门数据
        async getTreeData() {
            const res = await this.$axios.post('sys/org/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                // this.parentId = res.data.data[0].id;
                this.loading = false;
                this.getTableData();
            }
        },
        // 获取表格数据
        async getTableData() {
            const res = await this.$axios.post('sys/user/getUserListByRole', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            }
        },
        handleToTreeClick(data) {
            this.loading = true;
            this.searchInfo.orgId = data.id;
            this.getTableData();

            // console.log(data);
            // if (data.children.length == 0) {
            //     return false;
            // } else {
            // this.parentId = data.id;
            // 用来传递给子组件
            // this.addInfo = data;
            // }
            // this.getTableData();
        },
        filterNode(value, data) {
            if (!value) return true;
            return data.orgName.indexOf(value) !== -1;
        },
        toggleSelection(row) {
            this.$refs.multipleTable.toggleRowSelection(row);
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        // 点击搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getTableData();
        },
        // 点击重置
        handleToReset() {
            this.searchInfo.name = '';
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.getTableData();
        },
        handleSizeChange(newSize) {
            this.searchInfo.pageSize = newSize;
            this.getTableData();
        },
        handleCurrentChange(newNum) {
            this.searchInfo.pageNum = newNum;
            this.getTableData();
        },
        // 点击删除
        handleToDel(id) {
            this.$confirm('此操作将删除该用户, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/role/deleteUsersForRole', {
                        roleId: this.searchInfo.roleId,
                        userId: id
                    });
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
        },
        // 切换新增组件的显示与隐藏
        handleChangeIsAdd() {
            this.isAdd = !this.isAdd;
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
/deep/ .el-dialog .el-dialog__body{
    height: 600px;
}
.el-pagination {
    text-align: center;
    margin-top: 20px;
}
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>
