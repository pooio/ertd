<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 新增目录的表单
 * @Version: 1.0
 -->
<template>
  <!-- 新增表格数据的dialog -->
  <el-dialog title="新增目录" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item label="名称" prop="KbDirName">
        <el-input v-model.trim="addForm.KbDirName" maxlength="10" show-word-limit id="kb-catalog-add-KbDirName" placeholder="请输入名称"></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="kb-catalog-add-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToSave" id="kb-catalog-add-save">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.kbDirGbcode = this.addForm.KbDirGbcode;
            data.kbDirName = this.addForm.KbDirName;
            data.id = '';
            this.$axios.post('kb/dir/checkDirName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.dicName = '';
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
                // 目录名称
                KbDirName: '',
                // 父节点编码
                KbDirGbcode: '000000000000'
            },
            // 验证规则
            rules: {
                KbDirName: [
                    { required: true, message: '请输入名称', trigger: 'blur' },
                    { validator: checkDicinfoName, trigger: 'blur' }
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
            // 判断有没有父节点 如果没有 默认父节点为根节点
            JSON.stringify(data) == '{}' ? (this.addForm.KbDirGbcode = '000000000000') : (this.addForm.KbDirGbcode = data.kbDirCode);
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
                    const res = await this.$axios.post('kb/dir/save', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        this.$emit('getTreeData');
                        // if (this.addForm.KbDirGbcode == '000000000000') {
                        //     this.$emit('getTreeData');
                        // }
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