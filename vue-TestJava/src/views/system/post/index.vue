<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:55:01
 * @Description: 岗位管理
 * @Version: 1.0
 -->
<template>
  <div class="box">
    <!-- 表格区域 -->
    <el-row :gutter="10">
      <el-col :span="4">
        <el-input v-model="searchInput" placeholder="请输入职务名称或编码" clearable @keyup.enter.native="enterSearch" id="system-post-searchInput"></el-input>
      </el-col>
      <el-col :span="20">
        <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="system-post-search">搜索</el-button>
        <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="system-post-reset">重置</el-button>
        <el-button type="primary" title="新增岗位" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo)" v-has="'system-post-add'" id="system-post-add">新增</el-button>
      </el-col>
    </el-row>

    <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
      <el-table-column label="序号" align="center" type="index" width="50">
        <template slot-scope="scope">
          {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
        </template>
      </el-table-column>
      <el-table-column :resizable="false" prop="post" label="职务名称"></el-table-column>
      <el-table-column :resizable="false" prop="postCode" label="职务编码"></el-table-column>
      <el-table-column :resizable="false" prop="remark" label="备注" show-overflow-tooltip></el-table-column>
      <el-table-column :resizable="false" label="操作" width="220px">
        <template slot-scope="scope">
          <el-button size="mini" title="编辑数据" type="primary" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row)" v-has="'system-post-edit'" id="system-post-edit">编辑</el-button>
          <el-button size="mini" title="删除数据" type="danger" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-post-del'" id="system-post-del">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>

    <!-- 新增职务的子组件 -->
    <Add ref="add" @getTableData="getTableData"></Add>
    <!-- 编辑职务的子组件 -->
    <Edit ref="edit" @getTableData="getTableData"></Edit>
  </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
export default {
    // 注册子组件
    components: { Add, Edit },
    data() {
        return {
            // 搜索条件
            searchInput: '',
            // 表格绑定的数据
            tableData: [],
            // 当前页
            pageNum: 1,
            // 每页显示的数量
            pageSize: 10,
            // 总条数
            total: 0,
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            loading: true
        };
    },
    created() {
        this.getTableData();
    },

    methods: {
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
                postName: this.searchInput
            };
            const res = await this.$axios.post('sys/post/list', info);
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
         * @description: 点击切换每页显示的数量
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        handleSizeChange(newSize) {
            this.pageSize = newSize;
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
            this.pageNum = newNum;
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮重置input
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToReset() {
            this.searchInput = '';
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
            this.$confirm('确定删除吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/post/del', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.pageNum = 1;
                        this.pageSize = 10;
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    this.$message.info('已取消删除');
                });
        },
        // 点击按钮搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTableData();
        }, //input回车搜索
        enterSearch() {
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
    text-align: center;
    margin-top: 20px;
}
</style>
