<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改字典的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改职务" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="500" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="名称" prop="post">
        <el-input v-model.trim="editForm.post" placeholder="请输入职务名称" maxlength="16" show-word-limit id="system-post-edit-post"></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="postCode">
        <el-input v-model.trim="editForm.postCode" placeholder="请输入职务编码" maxlength="16" show-word-limit id="system-post-edit-postCode"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model.trim="editForm.remark" placeholder="请输入备注" type="textarea" rows="5" maxlength="100" show-word-limit resize="none" id="system-post-edit-remark"></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="system-post-edit-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToUpdata" id="system-post-edit-save">确 定</el-button>
      </div>
    </el-form>
    <!-- <span slot="footer" class="dialog-footer">
            <el-button type="text" @click="handleToClose">取 消</el-button>
            <el-button type="primary" @click="handleToUpdata">确 定</el-button>
        </span> -->
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkPostCode = (rule, value, callback) => {
            let data = {};
            data.postCode = this.editForm.postCode;
            data.id = this.editForm.id;
            this.$axios.post('sys/post/checkPostCode', data).then(res => {
                // console.log("返回结果" + JSON.stringify(res));
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.postCode = '';
                } else {
                    callback();
                }
            });
        };
        var checkPostName = (rule, value, callback) => {
            let data = {};
            data.post = this.editForm.post;
            data.id = this.editForm.id;
            this.$axios.post('sys/post/checkPostName', data).then(res => {
                // console.log("返回结果" + JSON.stringify(res));
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.post = '';
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
                post: '',
                // 字典描述
                postCode: '',
                // 字典名称
                remark: '',
                // id
                id: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                post: [
                    { required: true, message: '请输入职务名称', trigger: 'blur' },
                    { validator: checkPostName, trigger: 'blur' }
                ],
                postCode: [
                    { required: true, message: '请输入职务编码', trigger: 'blur' },
                    { validator: checkPostCode, trigger: 'blur' }
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
            this.editForm.post = row.post;
            this.editForm.postCode = row.postCode;
            this.editForm.remark = row.remark;
            this.editForm.id = row.id;
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
                    const res = await this.$axios.post('sys/post/updata', this.editForm);
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
