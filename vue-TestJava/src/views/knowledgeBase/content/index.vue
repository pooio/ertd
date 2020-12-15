<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors: Please set LastEditors
 * @LastEditTime: 2020-03-02 15:24:49
 * @Description: 知识库内容
 * @Version: 1.0
 -->

<template>
  <div class="box">
    <el-row :gutter="20" class="box">
      <!-- 树结构 -->
      <el-col :span="4" class="box">
        <el-input placeholder="请输入关键字进行过滤" v-model="filterText" clearable style="margin-bottom:20px" id="kb-content-tree"></el-input>
       <el-scrollbar style="height:calc(100% - 100px);">
            <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
       </el-scrollbar>
      </el-col>
      <!-- 表格区域 -->
      <el-col :span="20" class="box">
        <el-row :gutter="10">
          <el-col :span="4">
            <el-input v-model="searchInput" placeholder="请输入标题" clearable @keyup.enter.native="enterSearch" id="kb-content-name"></el-input>
          </el-col>
          <el-col :span="20">
            <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="kb-content-search">搜索</el-button>
            <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="kb-content-reset">重置</el-button>
            <el-button type="primary" title="新增内容" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo,selectData)" v-has="'kb-content-add'" id="kb-content-add">新增</el-button>
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
              <el-table-column :resizable="false" prop="kbName" label="标题">
                <template slot-scope="scope">
                  <div class="bulletinTitle el-tag__close" style="cursor: pointer;" @click="handleToSend(scope.row)" id="kb-content-kbNamePage">{{scope.row.kbName}}</div>
                </template>
              </el-table-column>
              <el-table-column :resizable="false" prop="kbTypeName" label="类型" width="100"></el-table-column>
              <el-table-column :resizable="false" prop="createName" label="创建人" width="100"></el-table-column>
              <el-table-column :resizable="false" prop="createTime" label="创建时间" width="150"></el-table-column>
              <el-table-column :resizable="false" label="操作" width="260px">
                <template slot-scope="scope" width="180">
                  <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row,selectData)" v-has="'kb-content-edit'" id="kb-content-edit">编辑</el-button>
                  <el-button size="mini" type="success" title="审批内容" icon="el-icon-success" @click="handleToApproval(scope.row)" id="kb-content-approval">审批</el-button>
                  <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.kbPk)" v-has="'kb-content-del'" id="kb-content-del">删除</el-button>
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
     <!-- 查看内容 -->
     <detailsDialog ref="detailsContent" :content="contentData"></detailsDialog>
  </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
import  detailsDialog  from '../../../components/common/detailsDialog';
export default {
    // 注册子组件
    components: { Add, Edit ,detailsDialog},
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
            // 当前选中目录的id
            id: '',
            // 选择器数据
            selectData: [],
            loading: true,
            contentData: '' //详情数据
        };
    },
    created() {
        this.getTreeData();
        this.getSelectData();
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
                this.id = res.data.data[0].id;
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
                kbDirPk: this.id,
                kbName: this.searchInput
            };
            const res = await this.$axios.post('kb/content/getList', info);

            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        async getSelectData() {
            const res = await this.$axios.post('sys/dictInfo/getDictList', { dicCode: 'KB_TYPE' });
            if (res.data.code == 0) {
                this.selectData = res.data.data;
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
            this.$confirm('确定删除该内容吗?此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('kb/content/doDel', { dirPk: id });
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
            console.log(data);
            // if (data.children.length == 0) {
            //     return false;
            // } else {
            this.KbDirGbcode = data.kbDirCode;
            this.id = data.id;
            // this.searchInput = data.kbDirName;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.getTableData();
        },
        // 点击搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTableData();
        },
        // 点击启动流程
        async handleToApproval(row) {
            const res = await this.$axios.post('bpm/start', {
                businessKey: row.kbPk,
                businessName: row.kbName,
                processDefinitionKey: 'kb',
                variables: ''
            });
            if (res.data.code == 0) {
            } else {
                this.$message.error(`${res.data.data}`);
            }
        }, //input回车搜索
        enterSearch() {
            this.handleToSearch();
        },
        // 点击查看目录详情
        async handleToSend(data) {
            this.contentData = data;
            this.$refs.detailsContent.openDialog();
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
// @import '@assets/css/style.css';
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

.biaoti {
    text-align: center;
    font-weight: 700;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>