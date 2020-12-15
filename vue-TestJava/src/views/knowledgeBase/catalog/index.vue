<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-19 09:55:22
 * @Description: 知识库目录
 * @Version: 1.0
 -->

<template>
  <div class="box">
    <el-row :gutter="20" class="box">
      <!-- 树结构 -->
      <el-col :span="4" class="box">
        <el-input placeholder="请输入关键字进行过滤" v-model="filterText" clearable style="margin-bottom:20px" id="kb-catalog-tree"></el-input>
       <el-scrollbar  style="height:calc(100% - 100px);">
            <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
       </el-scrollbar>
      </el-col>
      <!-- 表格区域 -->
      <el-col :span="20" class="box">
        <el-row :gutter="10">
          <el-col :span="4">
            <el-input v-model="searchInput" placeholder="请输入目录名称" clearable @keyup.enter.native="enterSearch" id="kb-catalog-name"></el-input>
          </el-col>
          <el-col :span="20">
            <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="kb-catalog-search">搜索</el-button>
            <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="kb-catalog-reset">重置</el-button>
            <el-button type="primary" title="新增目录" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo)" v-has="'kb-catalog-add'" id="kb-catalog-add">新增</el-button>
          </el-col>
        </el-row>
        <el-row class="box">
          <el-col class="box">
            <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
              <el-table-column label="序号" align="center" type="index" width="50">
                <template slot-scope="scope">
                  {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
                </template>
              </el-table-column>
              <el-table-column :resizable="false" prop="kbDirName" label="目录名称"></el-table-column>
              <el-table-column :resizable="false" prop="kbDirCode" label="目录编码"></el-table-column>
              <el-table-column :resizable="false" label="操作" width="180px">
                <template slot-scope="scope">
                  <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row)" v-has="'kb-catalog-edit'" id="kb-catalog-edit">编辑</el-button>
                  <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'kb-catalog-del'" id="kb-catalog-del">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 分页区域 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <!-- 新增字典的子组件 -->
    <Add ref="add" @getTableData="getTableData" @getTreeData="getTreeData"></Add>
    <!-- 编辑字典的子组件 -->
    <Edit ref="edit" @getTableData="getTableData" @getTreeData="getTreeData"></Edit>
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
            // 树结构绑定的数据
            treeData: [],
            // 树结构配置项
            defaultProps: {
                children: 'children',
                label: 'kbDirName'
            },
            // 过滤树结构的关键字
            filterText: '',
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
            // 父节点编码
            KbDirGbcode: '',
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            loading: false
        };
    },
    created() {
        this.getTreeData();
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
            const res = await this.$axios.post('kb/dir/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                this.KbDirGbcode = res.data.data[0].kbDirCode;
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
                KbDirGbcode: this.KbDirGbcode,
                kbDirName: this.searchInput
            };
            const res = await this.$axios.post('kb/dir/list', info);

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
            this.searchInput = '';
            this.filterText = '';
            this.pageNum = 1;
            this.pageSize = 10;
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
            return data.kbDirName.indexOf(value) !== -1;
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮删除表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToDel(id) {
            this.$confirm('确定删除该目录吗?此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('kb/dir/del', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                        this.getTreeData();
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
            const res = await this.$axios.post('sys/dictInfo/setInUse', info);

            if (res.data.code == 0) {
                if (this.parentId == 0) {
                    this.getTreeData();
                }
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
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
         * @description: 点击树结构
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        handleToTreeClick(data) {
            // if (data.children.length == 0) {
            //     return false;
            // } else {
            this.KbDirGbcode = data.kbDirCode;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.getTableData();
        },
        // 点击搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTableData();
            // this.getTreeData();
        },
        //input回车搜索
        enterSearch() {
            this.handleToSearch();
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
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>