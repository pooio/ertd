<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改区域的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改区域" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="名称" prop="areaName">
        <el-input v-model.trim="editForm.areaName" id="system-area-edit-areaName" placeholder="请输入区域名称" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="areaCode">
        <el-input v-model.trim="editForm.areaCode" id="system-area-edit-areaCode" placeholder="请输入区域编码" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button id="system-area-edit-cancel" type="text" @click="handleToClose">取 消</el-button>
        <el-button id="system-area-edit-save" type="primary" @click="handleToUpdata">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkCode = (rule, value, callback) => {
            let data = {};
            data.areaCode = this.editForm.areaCode;
            data.id = this.editForm.id;
            this.$axios.post('sys/area/checkAreaCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.areaCode = '';
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
                areaName: '',
                areaCode: '',
                parentCode: '',
                id: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                areaName: [
                    { required: true, message: '请输入区域名称', trigger: 'blur' }
                    // { validator: checkName, trigger: 'blur' }
                ],
                areaCode: [
                    { required: true, message: '请输入区域编码', trigger: 'blur' },
                    { validator: checkCode, trigger: 'blur' }
                ]
                // sort: [{ required: true, message: '请输入顺序', trigger: 'blur' }]
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
            this.editForm.areaName = row.areaName;
            this.editForm.areaCode = row.areaCode;
            this.editForm.parentCode = row.parentCode;
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
                    const res = await this.$axios.post('sys/area/updata', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        this.$emit('getTreeData');
                        // if (this.editForm.parentId == 1) {
                        //     this.$emit('getTreeData');
                        // }
                        // this.$emit('getTreeData');
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
