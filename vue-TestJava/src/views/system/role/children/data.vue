<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 数据授权的表单
 * @Version: 1.0
 -->
<template>
    <el-row :gutter="10" class="box" style="border:1px solid #ccc;">
        <!-- 下拉框 -->
        <el-col :span="6" style="border-right:1px solid #ccc;" class="box">
            <el-scrollbar style="height:calc(100% - 100px);">
                <el-form ref="form" label-width="100px">
                    <el-form-item label="数据范围">
                        <el-radio-group v-model="roleType" @change="handleToChange">
                            <el-radio :label="0">无数据</el-radio>
                            <el-radio :label="1">自定义数据</el-radio>
                            <el-radio :label="2">全部数据</el-radio>
                            <el-radio :label="3">本部门数据</el-radio>
                            <el-radio :label="4">本部门以及子部门数据</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button type="primary" @click="handleToSave" id="system-role-permission-save">确 定</el-button>
                </div>
            </el-scrollbar>
        </el-col>
        <el-col :span="6" class="box">
            <el-scrollbar style="height:calc(100% - 100px);">
                <el-tree
                    :data="treeData"
                    show-checkbox
                    node-key="id"
                    :default-expand-all="true"
                    :props="defaultProps"
                    ref="datatree"
                    v-if="roleType == 1"
                ></el-tree>
            </el-scrollbar>
        </el-col>
        <el-col :span="12" style="padding:20px;float:right;">
            <el-card class="box-card">
                <div slot="header" class="clearfix">
                    <span>说明：</span>
                </div>
                <div style="line-height:30px;" >
                    <p>数据访问权限用于设置此角色用户在对应功能页面中可查看的数据范围</p>
                    <p>无数据：不允许查看任何数据。</p>
                    <p>自定义数据：自定义可访问的部门数据。</p>
                    <p>全部数据：可查看功能页面的全部业务数据。</p>
                    <p>本部门数据：只可查看此角色用户所在部门的业务数据，子部门和其他部门数据不可查看。</p>
                    <p>本部门及子部门数据：可查看此角色用户所在部门及其子部门的业务数据，其他部门数据不可查看。</p>
                </div>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
export default {
    props: {
        roleId: ''
    },
    data() {
        return {
            // 角色授权类型
            roleType: 0,
            // 组织机构树
            treeData: [],
            // tree树形控件的配置项
            defaultProps: {
                children: 'children',
                label: 'orgName'
            },
            // 当前角色拥有的权限
            checkedKeys: []
        };
    },
    created() {
        this.getTreeData();
    },
    methods: {
        // 获取组织结构数据
        async getTreeData() {
            const res = await this.$axios.post('sys/org/treeData');
            if (res.data.code == 0) {
                this.treeData = res.data.data;
                console.log(res);
            }
        },
        // 获取当前角色拥有的数据权限
        async getRoleOrg() {
            const res = await this.$axios.post('sys/role/getRoleOrg', {
                id: this.roleId
            });
            if (res.data.code == 0) {
                this.checkedKeys = res.data.data.split(',');
                // 通过keys设置复选框的选中状态
                this.$refs.datatree.setCheckedKeys(this.checkedKeys);
            }
        },
        // 当点选按钮发生变化时
        handleToChange(data) {
            if (data == 1) {
                this.getRoleOrg();
            }
        },
        // 点击按钮保存
        async handleToSave() {
            let ids = [];
            if (this.roleType == 1) {
                let arr = this.$refs.datatree.getCheckedNodes();
                arr.forEach((item) => {
                    ids.push(item.id);
                });
                ids = ids.join(',');
            } else {
                ids = [];
            }
            const res = await this.$axios.post('sys/role/saveRoleOrg', {
                id: this.roleId,
                roleType: this.roleType,
                orgIds: ids
            });
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            }
        }
    }
};
</script>

<style lang="less" scoped>
.dialog-footer {
    padding-left: 100px;
}
/deep/.menu-dialog {
    max-height: 75%;
    .el-dialog__body {
        height: 50%;
        .el-tree {
            height: 50%;
            overflow-y: auto;
        }
    }
    .el-form-item:last-child {
        max-height: 411px;
        overflow-y: auto;
    }
}
.el-radio {
    width: 100%;
    line-height: 32px;
    float: left;
}
.box-card {
    border-left: 8px solid red;
    border-radius: 6px;
}
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>
