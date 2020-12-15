<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:04:34
 * @LastEditors: flynn.yang
 * @LastEditTime: 2020-06-28 11:45:12
 * @Description: 菜单管理
 * @Version: 1.0
 -->

<template>
  <div class="box">
    <!-- 搜索新增区域 -->
    <el-row>
      <!-- <el-col :span="4"> -->
      <!-- <el-input v-model="searchInput" placeholder="请输入菜单名称" clearable></el-input> -->
      <!-- </el-col> -->
      <el-col :span="24">
        <!-- <el-button type="success" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch">搜索</el-button>
                <el-button type="primary" plain  title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset">重置</el-button>-->
        <el-button id="system-menu-add" type="primary" icon="el-icon-plus" style="margin-left:20px" title="新增菜单" @click="$refs.add.openDialog(menuData2)" v-has="'system-menu-add'">新增</el-button>
      </el-col>
    </el-row>
    <!-- 表格区域 -->
    <el-table :data="menuData.children" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" row-key="id" style="width: 100%" border height="calc(100% - 60px)" v-loading="loading">
      <el-table-column :resizable="false" type="index" label="序号" align="center"></el-table-column>
      <!-- <el-table-column :resizable="false" prop="name" label="菜单名称"></el-table-column> -->
      <el-table-column :resizable="false" label="菜单名称">
        <template slot-scope="scope">
          <svg-icon style="width: 16px; height: 16px;" :iconClass="scope.row.iconClass == null ? '' : scope.row.iconClass" />
          {{scope.row.name}}
        </template>
      </el-table-column>
      <el-table-column :resizable="false" prop="url" label="路由地址">
        <template slot-scope="scope">
          <span v-if="scope.row.type == '0'">{{scope.row.url}}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column :resizable="false" label="按钮标识">
        <template slot-scope="scope">{{scope.row.type == '1' ? scope.row.btnType : '-'}}</template>
      </el-table-column>
      <el-table-column :resizable="false" prop="sort" label="排序">
        <template slot-scope="scope">
          <span v-if="scope.row.type == '0'">{{scope.row.sort}}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column :resizable="false" label="类型">
        <!-- <template slot-scope="scope">{{ scope.row.type == 0 ? '菜单' : '按钮' }}</template> -->
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type == 0">菜单</el-tag>
          <el-tag v-else type="success">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="120" label="是否显示">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.isShow==0?'隐藏':'显示'" placement="right">
            <el-switch id="system-menu-isShow" v-if="scope.row.type == '0'" v-model="scope.row.isShow" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" :disabled="switchIsDisabled"></el-switch>
            <span v-else>-</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column width="220" label="操作">
        <template slot-scope="scope">
          <el-button id="system-menu-edit" size="mini" type="primary" title="编辑数据" icon="el-icon-edit" @click="$refs.edit.openDialog(menuData, scope.row)" v-if="scope.row.type == '0'" v-has="'system-menu-edit'">编辑</el-button>
          <el-button id="system-menu-del" size="mini" type="danger" title="删除数据" icon="el-icon-delete" @click="handleToDel(scope.row.id)" v-has="'system-menu-del'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增菜单的子组件 -->
    <Add ref="add" @getTreeData="getTreeData"></Add>
    <!-- 新增菜单的子组件 -->
    <Edit ref="edit" @getTreeData="getTreeData"></Edit>
  </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
export default {
    components: {
        Add,
        Edit
    },
    data() {
        return {
            // 搜索条件
            searchInput: '',
            // 菜单数据
            menuData: [],
            // 不包含按钮的菜单数据
            menuData2: [],
            loading: true,
            //switch开关权限
            switchIsDisabled: true
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-menu-state');
        this.getTreeData();
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取菜单信息
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTreeData() {
            let info = {
                roleId: 1,
                type: 0
            };
            const res = await this.$axios.post('/sys/menu/treeData', info);
            if (res.data.code == 0) {
                this.menuData = res.data.data;
            }
            let info2 = {
                roleId: 1,
                type: 3
            };
            const res2 = await this.$axios.post('/sys/menu/treeData', info2);
            if (res2.data.code == 0) {
                this.menuData2 = res2.data.data;
            }
            this.loading = false;
        },
        // 点击按钮删除
        handleToDel(id) {
            this.$confirm('确定删除吗?如果存在下级节点则一并删除，此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/menu/delete', {
                        id
                    });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
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
         * @description: 菜单是否显示
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async handleToChange(row) {
            let info = {
                id: row.id,
                isShow: row.isShow == 1 ? 1 : 0
            };
            const res = await this.$axios.post('sys/menu/setIsShow', info);

            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
                row.inUse = row.isShow == 1 ? 0 : 1
            }
        },
        // 点击按钮搜索
        async handleToSearch() {
            let info = {
                roleId: 1,
                type: 0,
                name: this.searchInput
            };
            const res = await this.$axios.post('sys/menu/treeData', info);
            if (res.data.code == 0) {
                this.menuData = res.data.data;
            }
        },
        // 点击按钮重置
        handleToReset() {
            this.searchInput = '';
            this.getTreeData();
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
.box {
    overflow-y: hidden;
}
.el-table {
    margin-top: 20px;
    overflow-y: auto;
}
</style>