<template>
  <div class="box">
    <!-- <el-page-header @back="goBack" content="分配用户">
    </el-page-header> -->
    <el-row :gutter="20" class="box">
      <el-col :span="6" class="box">
        <el-input id="system-allocation-add-tree" placeholder="请输入关键字进行过滤" v-model="filterText" style="margin-bottom:20px" clearable></el-input>
        <el-scrollbar style="height:calc(100% - 110px);">
          <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false"></el-tree>
        </el-scrollbar>
      </el-col>
      <el-col :span="18" class="box">
          <el-row :gutter="16" style="margin-bottom:20px;">
            <el-col :span="6">
              <el-input id="system-allocation-add-name" v-model="searchInfo.name" placeholder="请输入姓名" clearable></el-input>
            </el-col>
            <el-col :span="18">
              <el-button id="system-allocation-add-search" title="根据输入条件搜索" icon="el-icon-search" type="primary" @click="handleToSearch">搜索</el-button>
              <el-button id="system-allocation-add-reset" title="清空搜索条件" icon="el-icon-refresh" type="primary" plain @click="handleToReset">重置</el-button>
              <el-button id="system-allocation-add-save" type="primary" icon="el-icon-check" v-if="multipleSelection.length !== 0" @click="handleToSave">确定</el-button>
            </el-col>
          </el-row>
          <el-table ref="multipleTable" :data="tableData" border tooltip-effect="dark" style="width: 100%" height="calc(100% - 110px)" row-key="id" @selection-change="handleSelectionChange">
            <el-table-column type="selection" reserve-selection width="55">
            </el-table-column>
            <el-table-column :resizable="false" label="序号" align="center" type="index" width="50">
              <template slot-scope="scope">
                {{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名">
            </el-table-column>
            <el-table-column prop="orgName" label="部门">
            </el-table-column>
          </el-table>
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
        <!-- <el-col :span="6">
          <el-tag v-for="(tag,index) in multipleSelection" :key="index" closable @close="toggleSelection(tag)">
            {{tag.name}}
          </el-tag>
          <el-button id="system-allocation-add-save" type="primary" v-if="multipleSelection.length !== 0" @click="handleToSave">确认</el-button>
        </el-col> -->
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
    props: ['treeData'],
    data() {
        return {
            // 树结构绑定的数据
            // treeData: [],
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
            multipleSelection: []
        };
    },
    created() {
        this.searchInfo.roleId = this.$route.query.roleId;
        this.getTableData();
    },
    methods: {
        // 点击返回触发父组件的方法
        goBack() {
            this.$emit('handleChangeIsAdd');
        },
        // 获取表格数据
        async getTableData() {
            const res = await this.$axios.post('sys/user/getNoUserListByRoleOnOrg', this.searchInfo);
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
            // if (val.length !== 0) {
            this.multipleSelection = val;
            // }

            // this.multipleSelection = this._.cloneDeep(val);
            // console.log(this.multipleSelection === val);
        },
        // 点击搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getTableData();
        },
        // 点击重置
        handleToReset() {
            this.filterText = '';
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
        // 点击确认 分配角色
        async handleToSave() {
            let userIds = [];
            this.multipleSelection.forEach(item => {
                userIds.push(item.id);
            });
            const res = await this.$axios.post('sys/role/saveUsersForRole', {
                roleId: this.searchInfo.roleId,
                userIds: userIds.join(',')
            });
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
                this.$emit('getTableData');
                this.$emit('handleChangeIsAdd');
                this.$refs.multipleTable.clearSelection();
            } else {
                this.$message.error(`${res.data.data}`);
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
.box {
    .el-page-header {
        margin-bottom: 20px;
    }
    .el-pagination {
        text-align: center;
        margin-top: 20px;
    }
}
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>
