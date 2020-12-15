<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:55:09
 * @Description: 区域管理
 * @Version: 1.0
 -->
<template>
  <div class="box">
    <el-row :gutter="20" class="box">
      <!-- 树结构 -->
      <el-col :span="4" class="box">
        <el-input placeholder="请输入关键字进行过滤" id="system-area-tree" v-model="filterText" clearable style="margin-bottom:20px"></el-input>
        <el-scrollbar style="height:calc(100% - 100px);">
          <el-tree class="filter-tree" :data="treeData" :props="defaultProps" node-key="id" :default-expanded-keys="expandedKeys" :filter-node-method="filterNode" ref="tree" accordion @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
        </el-scrollbar>
      </el-col>
      <!-- 表格区域 -->
      <el-col :span="20" class="box">
        <el-row :gutter="10">
          <el-col :span="4">
            <el-input v-model="searchInput" id="system-area-name" placeholder="请输入区域名称" clearable  @keyup.enter.native="enterSearch"></el-input>
          </el-col>
          <el-col :span="20">
            <el-button type="primary" id="system-area-search" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch">搜索</el-button>
            <el-button type="primary" id="system-area-reset" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset">重置</el-button>
            <el-button type="primary" id="system-area-add" title="为已选中的组织机构添加区域" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo)" v-has="'system-area-add'">新增</el-button>
          </el-col>
        </el-row>

        <!-- <el-row>
          <el-col> -->
            <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
              <el-table-column label="序号" align="center" type="index" width="50">
                <template slot-scope="scope">
                  {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
                </template>
              </el-table-column>
              <el-table-column :resizable="false" prop="areaName" label="区域名称"></el-table-column>
              <el-table-column :resizable="false" prop="areaCode" label="区域编码"></el-table-column>
              <el-table-column :resizable="false" label="操作" width="180px">
                <template slot-scope="scope">
                  <el-button id="system-area-edit" size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row)" v-has="'system-area-edit'">编辑</el-button>
                  <el-button id="system-area-del" size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-area-del'">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <!-- 分页区域 -->
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
          <!-- </el-col>
        </el-row> -->
      </el-col>
    </el-row>
    <!-- 新增区域的子组件 -->
    <Add ref="add" @getTableData="getTableData" @getTreeData="getTreeData"></Add>
    <!-- 编辑区域的子组件 -->
    <Edit ref="edit" @getTableData="getTableData" @getTreeData="getTreeData"></Edit>
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
                label: 'areaName'
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
            // 父节点id
            parentId: '',
            // 父节点编码
            areaCode: 0,
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            // 默认展开的数组
            expandedKeys: [],
            // loading状态
            loading: true
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
            const res = await this.$axios.post('sys/area/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                this.parentId = res.data.data[0].id;
                this.expandedKeys.push(res.data.data[0].id);
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
                // parentId: this.parentId,
                areaName: this.searchInput,
                areaCode: this.areaCode
            };
            const res = await this.$axios.post('sys/area/list', info);

            if (res.data.code == 0) {
                this.loading = false;
                this.tableData = res.data.data;
                this.total = res.data.total;
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
            this.loading = true;
            this.searchInput = '';
            this.filterText = '';
            this.areaCode = 0;
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
            return data.areaName.indexOf(value) !== -1;
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮删除表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToDel(id) {
            this.$confirm('确定删除吗?如果存在下级节点则一并删除，此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/area/del', {
                        id
                    });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                        this.getTreeData();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    // this.$message.info('已取消删除');
                });
        },
        /**
         * @Author: Misaka.chen
         * @description: 组织机构是否禁用
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async handleToChange(row) {
            let info = {
                id: row.id,
                inUse: row.inUse == 1 ? 1 : 0
            };
            const res = await this.$axios.post('sys/org/setInUse', info);

            if (res.data.code == 0) {
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
            this.loading = true;
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
            this.loading = true;
            this.pageNum = newNum;
            this.getTableData();
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
            // this.parentId = data.id;
            this.areaCode = data.areaCode;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.pageNum = 1;
            this.getTableData();
        },
        // 点击按钮搜索
        handleToSearch() {
            this.loading = true;
            this.pageNum = 1;
            this.areaCode = '';
            // this.getTreeData();
            this.getTableData();
        },
         //input回车搜索
        enterSearch(){
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