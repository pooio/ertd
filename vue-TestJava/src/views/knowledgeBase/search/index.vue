<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-19 09:55:22
 * @Description: 知识库内容搜索
 * @Version: 1.0
 -->

<template>
    <div class="box">
        <el-row :gutter="20" class="box">
            <!-- 树结构 -->
            <el-col :span="4" class="box">
                <el-input placeholder="请输入关键字进行过滤" v-model="filterText" clearable style="margin-bottom:20px" id="kb-search-tree"></el-input>
                <el-scrollbar style="height:calc(100% - 100px);">
                    <el-tree class="filter-tree" :data="treeData" :props="defaultProps" default-expand-all :filter-node-method="filterNode" ref="tree" @node-click="handleToTreeClick" :expand-on-click-node="false" v-loading="loading"></el-tree>
                </el-scrollbar>
            </el-col>
            <!-- 表格区域 -->
            <el-col :span="20" style="height:100%">
                <el-row :gutter="10">
                    <el-col :span="8">
                        <el-input placeholder="请输入内容" v-model="searchInput" clearable class="input-with-select" @keyup.enter.native="enterSearch" id="kb-search-searchInput">
                            <el-select v-model="searchType" slot="prepend" placeholder="请选择">
                                <el-option label="标题" value="1"></el-option>
                                <el-option label="关键字" value="2"></el-option>
                                <el-option label="全文检索" value="3"></el-option>
                            </el-select>
                        </el-input>
                    </el-col>
                    <el-col :span="16">
                        <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="kb-search-search">搜索</el-button>
                        <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="kb-search-reset">重置</el-button>
                    </el-col>
                </el-row>
                <el-row style="height:calc(100% - 52px)">
                    <el-col style="height:100%;overflow-y:auto">
                        <!-- 列表内容 -->
                        <div class="list-data" v-loading="loading">
                            <div class="list" v-for="item in tableData" :key="item.kbPk">
                                <div class="top">
                                    <div class="title" v-text="item.kbName" @click="handleToSend(item)" id="kb-search-kbNameTitle"></div>
                                    <!-- <div class="date" v-text="item.createTime"></div> -->
                                </div>
                                <!-- <div class="bottom" v-html="item.kbContent"></div> -->
                                <div class="center">
                                    <span>分类: <span v-text="item.kbTypeName"></span></span>
                                    <span>作者: <span v-text="item.createName"></span></span>
                                    <span>创建时间: <span v-text="item.createTime"></span></span>
                                </div>
                            </div>
                            <!-- 分页区域 -->
                            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
                        </div>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
        <!-- 查看内容 -->
        <detailsDialog ref="detailsDialog" :content="content"></detailsDialog>
    </div>
</template>

<script>
import detailsDialog from '../../../components/common/detailsDialog';
export default {
    components: {
        detailsDialog
    },
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
            // 当前选中目录的id
            id: '',
            // 搜索类型
            searchType: '1',
            // 点击当前项的内容
            content: '',
            loading: true
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
                type: this.searchType,
                kbDirPk: this.id,
                word: this.searchInput
            };
            const res = await this.$axios.post('kb/content/doQuery', info);

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
            this.searchType = '1';
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
            // if (data.children.length == 0) {
            //     return false;
            // } else {
            this.KbDirGbcode = data.kbDirCode;
            this.id = data.id;
            // 用来传递给子组件
            this.addInfo = data;
            // }
            this.getTableData();
        },
        // 点击搜索
        handleToSearch() {
            this.pageNum = 1;
            // this.getTreeData()
            this.getTableData();
        },
        // 点击查看内容
        handleToSend(item) {
            this.content = item;
            this.$refs.detailsDialog.openDialog();
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

.el-select /deep/.el-input {
    width: 100px;
}

.input-with-select /deep/ .el-input-group__prepend {
    background-color: #fff;
}

.list-data {
    padding: 0px 20px 5px;
    box-sizing: border-box;
    border: 1px solid rgba(229, 229, 229, 1);
}

.list {
    /* padding: 10px; */
    border-bottom: 2px solid rgba(229, 229, 229, 1);

    .top {
        display: flex;
        justify-content: space-between;
        line-height: 30px;
        margin-top: 16px;

        .title {
            font-size: 20px;
            font-weight: 700;
            cursor: pointer;
        }

        .title:hover {
            color: #2f8dfb;
        }

        .date {
            font-size: 14px;
        }
    }

    .center {
        display: flex;
        /* justify-content: space-between; */
        justify-content: flex-start;
        font-size: 14px;
        line-height: 24px;
        color: #aaa;

        span {
            margin-right: 20px;
        }
    }

    .bottom {
        font-size: 16px;
        width: 100%;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        /deep/ p {
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
        }
    }
}

.biaoti {
    text-align: center;
    font-weight: 700;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 18px;
}

.info {
    display: flex;
    justify-content: space-between;
    padding: 0 40px;
    margin-bottom: 20px;
    color: #999;
}
/deep/.el-scrollbar__view {
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree {
    display: inline-block;
}
</style>