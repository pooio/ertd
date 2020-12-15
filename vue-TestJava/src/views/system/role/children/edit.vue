<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改角色的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改角色" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="名称" prop="roleName">
        <el-input id="system-role-edit-roleName" v-model.trim="editForm.roleName" placeholder="请输入角色名称" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="roleCode">
        <el-input id="system-role-edit-roleCode" v-model.trim="editForm.roleCode" placeholder="请输入角色编码" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input id="system-role-edit-remark" v-model.trim="editForm.remark" placeholder="请输入备注" type="textarea" rows="5" maxlength="100" show-word-limit resize="none"></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button id="system-role-edit-cancel" type="text" @click="handleToClose">取 消</el-button>
        <el-button id="system-role-edit-save" type="primary" @click="handleToUpdata">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkCode = (rule, value, callback) => {
            let data = {};
            data.roleCode = this.editForm.roleCode;
            data.roleId = this.editForm.id;
            this.$axios.post('sys/role/checkRoleCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.roleCode = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 控制编辑表单对话框的显示隐藏
            editDialogVisible: false,
            // 编辑表单的信息
            editForm: {
                // 字典编码
                roleName: '',
                // 字典描述
                roleCode: '',
                // 角色类型
                typeId: '',
                // 字典名称
                remark: '',
                id: '',
                inUse: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
                roleCode: [
                    { required: true, message: '请输入角色编码', trigger: 'blur' },
                    { validator: checkCode, trigger: 'blur' }
                ]
            }
        };
    },
    created() {},
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 打开dialog的方法
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        openDialog(row) {
            this.editDialogVisible = true;
            this.editForm.roleName = row.roleName;
            this.editForm.roleCode = row.roleCode;
            this.editForm.remark = row.remark;
            this.editForm.id = row.id;
            this.editForm.inUse = row.inUse;
        },
        /**
         * @Author: Misaka.chen
         * @description: dialog关闭的回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToClose() {
            this.editDialogVisible = false;
            this.$refs.editForm.resetFields();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮更新数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        handleToUpdata() {
            this.$refs.editForm.validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('sys/role/update', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
                        this.handleToClose();
                    }
                } else {
                    return false;
                }
            });
        }
    }
};
</script>

<style lang="less" scoped>
/deep/.el-input-number .el-input__inner {
    text-align: left;
}
.dialog-footer {
    text-align: right;
}
</style>
