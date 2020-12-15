<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改字典的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改字典" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="名称" prop="dicName">
        <el-input v-model.trim="editForm.dicName" maxlength="16" placeholder="请输入字典名称" show-word-limit id="system-dictionaries-edit-dicName"></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="dicCode">
        <el-input v-model.trim="editForm.dicCode" maxlength="16" placeholder="请输入字典编码" show-word-limit id="system-dictionaries-edit-dicCode"></el-input>
      </el-form-item>
      <el-form-item label="顺序" prop="sortNo">
        <el-input-number v-model.number="editForm.sortNo" :min="0" controls-position="right" style="width: 100%;" id="system-dictionaries-edit-sortNo"></el-input-number>
        <!-- <el-input v-model.number="editForm.sortNo" type="number" :min="0" :max="999"></el-input> -->
      </el-form-item>
      <el-form-item label="备注" prop="dicDesc">
        <el-input v-model.trim="editForm.dicDesc" placeholder="请输入备注" type="textarea" maxlength="100" rows="5" show-word-limit resize="none" id="system-dictionaries-edit-dicDesc"></el-input>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button type="text" @click="handleToClose" id="system-dictionaries-edit-cancel">取 消</el-button>
      <el-button type="primary" @click="handleToUpdata" id="system-dictionaries-edit-save">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.dicName = this.editForm.dicName;
            data.parentId = this.editForm.parentId;
            data.id = this.editForm.id;
            this.$axios.post('sys/dictInfo/checkDicinfoName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.dicName = '';
                } else {
                    callback();
                }
            });
        };
        var checkDicinfoCode = (rule, value, callback) => {
            let data = {};
            data.dicCode = this.editForm.dicCode;
            data.parentId = this.editForm.parentId;
            data.id = this.editForm.id;
            this.$axios.post('sys/dictInfo/checkDicinfoCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.dicCode = '';
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
                dicCode: '',
                // 字典描述
                dicDesc: '',
                // 字典名称
                dicName: '',
                // 父节点ID
                parentId: '',
                // 顺序
                sortNo: '',
                // id
                id: '',
                inUse: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                dicName: [
                    { required: true, message: '请输入字典名称', trigger: 'blur' },
                    { validator: checkDicinfoName, trigger: 'blur' }
                ],
                dicCode: [
                    { required: true, message: '请输入字典编码', trigger: 'blur' },
                    { validator: checkDicinfoCode, trigger: 'blur' }
                ],
                dicDesc: [{ required: false, message: '请输入备注', trigger: 'blur' }],
                sortNo: [{ required: true, message: '请输入顺序', trigger: 'blur' }]
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
            this.editForm.dicCode = row.dicCode;
            this.editForm.dicDesc = row.dicDesc;
            this.editForm.dicName = row.dicName;
            this.editForm.parentId = row.parentId;
            this.editForm.sortNo = row.sortNo;
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
                    const res = await this.$axios.post('sys/dictInfo/updata', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
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
