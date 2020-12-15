<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 新增字典的表单
 * @Version: 1.0
 -->
<template>
  <!-- 新增表格数据的dialog -->
  <el-dialog title="新增职务" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="500" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item label="名称" prop="post">
        <el-input v-model.trim="addForm.post" placeholder="请输入职务名称" maxlength="16" show-word-limit id="system-post-add-post"></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="postCode">
        <el-input v-model.trim="addForm.postCode" placeholder="请输入职务编码" maxlength="16" show-word-limit id="system-post-add-postCode"></el-input>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model.trim="addForm.remark" placeholder="请输入备注" type="textarea" rows="5" maxlength="100" show-word-limit resize="none" id="system-post-add-remark"></el-input>
      </el-form-item>

      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="system-post-add-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToSave" id="system-post-add-save">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkPostName = (rule, value, callback) => {
            let data = {};
            data.post = this.addForm.post;
            this.$axios.post('sys/post/checkPostName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.post = '';
                } else {
                    callback();
                }
            });
        };
        var checkPostCode = (rule, value, callback) => {
            let data = {};
            data.postCode = this.addForm.postCode;
            this.$axios.post('sys/post/checkPostCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.postCode = '';
                } else {
                    callback();
                }
            });
        };

        return {
            // 控制新增dialog的显示与隐藏
            addDialogVisible: false,
            // 新增表单数据
            addForm: {
                // 字典编码
                post: '',
                // 字典描述
                postCode: '',
                // 字典名称
                remark: ''
            },
            // 验证规则
            rules: {
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
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 打开dialog的方法
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        openDialog(data) {
            this.addDialogVisible = true;
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToSave() {
            this.$refs.addForm.validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('sys/post/save', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
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
        },
        /**
         * @Author: Misaka.chen
         * @description: dialog关闭的回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToClose() {
            this.addDialogVisible = false;
            // 重置表单
            this.$refs.addForm.resetFields();
        }
    }
};
</script>

<style lang="less" scoped>
.dialog-footer {
    text-align: right;
}
/deep/.el-input-number .el-input__inner {
    text-align: left;
}
</style>
