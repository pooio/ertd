<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改目录的表单
 * @Version: 1.0
 -->
<template>
    <el-dialog
        title="修改目录"
        :visible.sync="editDialogVisible"
        :close-on-click-modal="false"
        width="600"
        :before-close="handleToClose"
    >
        <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
            <el-form-item label="名称" prop="kbDirName">
                <el-input
                    v-model.trim="editForm.kbDirName"
                    maxlength="10"
                    show-word-limit
                    id="kb-catalog-edit-kbDirName"
                    placeholder="请输入名称"
                ></el-input>
            </el-form-item>
        </el-form>
        <div class="dialog-footer">
            <el-button type="text" @click="handleToClose" id="kb-catalog-edit-cancel">取 消</el-button>
            <el-button type="primary" @click="handleToUpdata" id="kb-catalog-edit-save">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
export default {
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            var data = {};
            data.kbDirGbcode = this.editForm.kbDirGbcode;
            data.kbDirName = this.editForm.kbDirName;
            data.id = this.editForm.id;
            this.$axios.post('kb/dir/checkDirName', data).then((res) => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.dicName = '';
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
                // 目录名称
                kbDirName: '',
                // 父节点目录编码
                kbDirGbcode: '',
                // 目录编码
                kbDirCode: '',
                // id
                id: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                kbDirName: [
                    { required: true, message: '请输入名称', trigger: 'blur' },
                    { validator: checkDicinfoName, trigger: 'blur' }
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
            console.log(row);
            this.editForm.kbDirName = row.kbDirName;

            this.editForm.kbDirCode = row.kbDirCode;
            this.editForm.kbDirGbcode = row.kbDirGbcode;
            this.editForm.id = row.id;
            this.editDialogVisible = true;
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
            this.$refs.editForm.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('kb/dir/update', this.editForm);
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