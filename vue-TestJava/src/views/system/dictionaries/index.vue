<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-19 09:55:22
 * @Description: 字典管理
 * @Version: 1.0
 -->

<template>
    <div class="box">
        <el-row :gutter="20" class="box">
            <!-- 树结构 -->
            <el-col :span="4" class="box">
                <el-input placeholder="请输入关键字进行过滤" v-model="filterText" clearable style="margin-bottom:20px" id="system-dictionaries-filterText"></el-input>
                <el-scrollbar style="height:calc(100% - 100px);">
                    <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading">
                        <span class="spanEllipsis" slot-scope="{node}">
                            <span :title="node.label">{{node.label}}</span>
                        </span>
                    </el-tree>
                </el-scrollbar>
            </el-col>
            <!-- 表格区域 -->
            <el-col :span="20" class="box">
                <el-row :gutter="10">
                    <el-col :span="4">
                        <el-input v-model="searchInput" placeholder="请输入字典名称或字典编码" clearable  @keyup.enter.native="enterSearch" id="system-dictionaries-searchInput"></el-input>
                    </el-col>
                    <el-col :span="20">
                        <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="system-dictionaries-search">搜索</el-button>
                        <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="system-dictionaries-reset">重置</el-button>
                        <el-button type="primary" title="为已选中的字典类型新增字典数据" icon="el-icon-plus" style="margin-left:20px" @click="$refs.add.openDialog(addInfo)" v-has="'system-dictionaries-add'" id="system-dictionaries-add">新增</el-button>
                    </el-col>
                </el-row>
                <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
                    <el-table-column label="序号" align="center" type="index" width="50">
                        <template slot-scope="scope">
                            {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
                        </template>
                    </el-table-column>
                    <el-table-column :resizable="false" prop="dicName" label="字典名称"></el-table-column>
                    <el-table-column :resizable="false" prop="dicCode" label="字典编码"></el-table-column>
                    <el-table-column :resizable="false" prop="sortNo" label="排序"></el-table-column>
                    <el-table-column :resizable="false" prop="dicDesc" label="备注" show-overflow-tooltip></el-table-column>
                    <el-table-column :resizable="false" label="状态" width="120">
                        <template slot-scope="scope">
                            <el-tooltip :content="scope.row.inUse==0?'禁用':'启用'" placement="right">
                                <el-switch v-model="scope.row.inUse" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" id="system-dictionaries-state" :disabled="switchIsDisabled"></el-switch>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column :resizable="false" label="操作" width="220px">
                        <template slot-scope="scope">
                            <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(scope.row)" v-has="'system-dictionaries-edit'" id="system-dictionaries-edit">编辑</el-button>
                            <el-button size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-dictionaries-del'" id="system-dictionaries-del">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页区域 -->
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
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
                label: 'dicName'
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
            // 新增数据的参数 主要用来传递给子组件
            addInfo: {},
            loading: true,
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-dictionaries-state');
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
            const res = await this.$axios.post('sys/dictInfo/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                this.parentId = res.data.data[0].id;
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
                parentId: this.parentId,
                dicName: this.searchInput
            };
            const res = await this.$axios.post('sys/dictInfo/list', info);

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
            return data.dicName.indexOf(value) !== -1;
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
                    const res = await this.$axios.post('sys/dictInfo/del', { id });
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
                row.inUse = row.inUse == 0 ? 1 : 0;
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
            this.parentId = data.id;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.getTableData();
        },
        // 点击搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTreeData();
        }, //input回车搜索
        enterSearch() {
            this.handleToSearch();
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
.spanEllipsis {
    width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-size: 14px;
}
/deep/.el-scrollbar__view {
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree {
    display: inline-block;
}
</style>
