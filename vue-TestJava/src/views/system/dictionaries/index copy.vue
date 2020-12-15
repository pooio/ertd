<template>
    <div class="box dic">
        <el-row :gutter="10">
            <el-col :span="10" class="h-99">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span>字典列表</span>
                    </div>
                    <el-collapse-transition>
                        <div class="search" v-show="isLeftSearch">
                            <el-row :gutter="10">
                                <el-col :span="6">
                                    <el-input v-model="searchInfo1.dicName" placeholder="请输入字典名称"></el-input>
                                </el-col>
                                <el-col :span="6">
                                    <el-input v-model="searchInfo1.dicCode" placeholder="请输入字典编码"></el-input>
                                </el-col>
                                <el-col :span="10">
                                    <el-button type="primary" icon="el-icon-search">搜索</el-button>
                                    <el-button type="primary" plain icon="el-icon-refresh">重置</el-button>
                                </el-col>
                            </el-row>
                        </div>
                    </el-collapse-transition>
                    <div class="tool-bar">
                        <el-button type="primary" icon="el-icon-plus">新增</el-button>
                        <div class="hidden">
                            <el-tooltip class="item" effect="dark" content="刷新" placement="top">
                                <el-button icon="el-icon-refresh" circle></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" content="搜索" placement="top">
                                <el-button icon="el-icon-search" circle @click="isLeftSearch = !isLeftSearch"></el-button>
                            </el-tooltip>
                        </div>
                    </div>
                    <el-table :data="tableData1" border style="width: 100%" height="calc(100% - 200px)">
                        <el-table-column header-align="center" align="center" type="index" label="序号"></el-table-column>
                        <el-table-column prop="dicName" label="字典名称" show-overflow-tooltip></el-table-column>
                        <el-table-column prop="dicCode" label="字典编码" show-overflow-tooltip></el-table-column>
                        <el-table-column header-align="center" align="center" prop="date" label="状态" width="80">
                            <template slot-scope="scope">
                                <el-switch v-model="scope.row.value" active-color="#13ce66" inactive-color="#ff4949" active-value="1" inactive-value="0">
                                </el-switch>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="120">
                            <template slot-scope="scope">
                                <el-button type="text" icon="el-icon-edit">编辑</el-button>
                                <el-button type="text" icon="el-icon-delete">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo1.pageNum" :page-size="searchInfo1.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total1">
                    </el-pagination>
                </el-card>
            </el-col>
            <el-col :span="14" class="h-99">
                <el-card class="box-card">
                    <el-collapse-transition>
                        <div class="search" v-show="isRightSearch">
                            <el-row :gutter="10">
                                <el-col :span="6">
                                    <el-input v-model="input1" placeholder="请输入字典名称"></el-input>
                                </el-col>
                                <el-col :span="6">
                                    <el-input v-model="input1" placeholder="请输入字典编码"></el-input>
                                </el-col>
                                <el-col :span="10">
                                    <el-button type="primary" icon="el-icon-search">搜索</el-button>
                                    <el-button type="primary" plain icon="el-icon-refresh">重置</el-button>
                                </el-col>
                            </el-row>
                        </div>
                    </el-collapse-transition>
                    <div class="tool-bar">
                        <el-button type="primary" icon="el-icon-plus">新增</el-button>
                        <div class="hidden">
                            <el-tooltip class="item" effect="dark" content="刷新" placement="top">
                                <el-button icon="el-icon-refresh" circle></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" content="搜索" placement="top">
                                <el-button icon="el-icon-search" circle @click="isRightSearch = !isRightSearch"></el-button>
                            </el-tooltip>
                        </div>
                    </div>
                    <el-table :data="tableData2" border style="width: 100%" height="calc(100% - 200px)">
                        <el-table-column header-align="center" align="center" type="index" label="序号"></el-table-column>
                        <el-table-column prop="name" label="字典名称"></el-table-column>
                        <el-table-column prop="date" label="字典编码"></el-table-column>
                        <el-table-column header-align="center" align="center" prop="date" label="状态" width="80">
                            <template slot-scope="scope">
                                <el-switch v-model="scope.row.value" active-color="#13ce66" inactive-color="#ff4949" active-value="1" inactive-value="0">
                                </el-switch>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="120">
                            <template slot-scope="scope">
                                <el-button type="text" icon="el-icon-edit">编辑</el-button>
                                <el-button type="text" icon="el-icon-delete">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
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
            input1: '',
            searchInfo1: {
                dicName: '',
                dicCode: '',
                pageNum: 1,
                pageSize: 10
            },
            total1: 0,
            tableData1: [],
            loading: true,
            isLeftSearch: true,
            isRightSearch: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-dictionaries-state');
        this.getTreeData1();
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取树结构数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTreeData1() {
            const res = await this.$axios.post('sys/dictInfo/treeData');
            if (res.data.code == 0) {
                this.tableData1 = res.data.data[0].children.slice(0, 10);
                this.total1 = res.data.data[0].children.length;
                // this.treeData = res.data.data;
                // this.parentId = res.data.data[0].id;
                // this.getTableData();
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
                this.tableData2 = res.data.data;
                this.total2 = res.data.total;
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
.dic {
    .el-row {
        height: 100%;
    }
    .el-card {
        height: 100%;
        /deep/.el-card__body {
            height: 100%;
            // .table {
            //     height: calc(~'100% - 180px');
            // }
        }
        /deep/.el-card__header {
            padding: 6px 18px !important;
        }
    }
    .h-99 {
        height: 99%;
    }
}
.el-col {
    margin-bottom: 10px;
    // height: 100%;
}
.tool-bar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
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
