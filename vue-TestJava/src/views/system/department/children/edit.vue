<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改组织机构的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改组织机构" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="500" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="名称" prop="orgName">
        <el-input v-model.trim="editForm.orgName" placeholder="请输入机构名称" maxlength="6" show-word-limit id="system-department-edit-orgName"></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model.trim="editForm.code" placeholder="请输入机构编码" maxlength="16" show-word-limit id="system-department-edit-code"></el-input>
      </el-form-item>
      <el-form-item label="顺序" prop="sort">
        <el-input-number v-model.number="editForm.sort" :min="0" controls-position="right" style="width: 100%;" id="system-department-edit-sort"></el-input-number>
        <!-- <el-input v-model.number="editForm.sortNo" type="number" :min="0" :max="999"></el-input> -->
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model.trim="editForm.remark" placeholder="请输入备注" type="textarea" rows="5" maxlength="100" show-word-limit resize="none" id="system-department-edit-remark"></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="system-department-edit-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToUpdata" id="system-department-edit-save">确 定</el-button>
      </div>
    </el-form>
    <!-- <span slot="footer" class="dialog-footer">
            <el-button type="text" @click="handleToClose">取 消</el-button>
            <el-button type="primary" @click="handleToUpdata">确 定</el-button>
        </span>-->
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkCode = (rule, value, callback) => {
            let data = {};
            data.code = this.editForm.code;
            data.parentId = this.editForm.parentId;
            data.id = this.editForm.id;
            this.$axios.post('sys/org/checkOrgCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.code = '';
                } else {
                    callback();
                }
            });
        };
        var checkName = (rule, value, callback) => {
            let data = {};
            data.orgName = this.editForm.orgName;
            data.parentId = this.editForm.parentId;
            data.id = this.editForm.id;
            this.$axios.post('sys/org/checkOrgName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.orgName = '';
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
                code: '',
                // 字典描述
                remark: '',
                // 字典名称
                orgName: '',
                // 父节点ID
                parentId: '',
                // 顺序
                sort: '',
                // id
                id: '',
                inUse: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                orgName: [
                    { required: true, message: '请输入机构名称', trigger: 'blur' },
                    { validator: checkName, trigger: 'blur' }
                ],
                code: [
                    { required: true, message: '请输入机构编码', trigger: 'blur' },
                    { validator: checkCode, trigger: 'blur' }
                ],
                sort: [{ required: true, message: '请输入顺序', trigger: 'blur' }]
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
            this.editForm.code = row.code;
            this.editForm.orgName = row.orgName;
            this.editForm.parentId = row.parentId;
            this.editForm.sort = row.sort;
            this.editForm.id = row.id;
            this.editForm.inUse = row.inUse;
            this.editForm.remark = row.remark;
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
                    const res = await this.$axios.post('sys/org/update', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        // if (this.editForm.parentId == 1) {
                        //     this.$emit('getTreeData');
                        // }
                        this.$emit('getTreeData');
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
