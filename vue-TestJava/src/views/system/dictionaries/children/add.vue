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
  <el-dialog title="新增字典" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item label="类型">
        <el-input v-model.number="parentName" readonly id="system-dictionaries-parentName"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="dicName">
        <el-input v-model.trim="addForm.dicName" placeholder="请输入字典名称" maxlength="16" show-word-limit id="system-dictionaries-add-dicName"></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="dicCode">
        <el-input v-model.trim="addForm.dicCode" placeholder="请输入字典编码" maxlength="16" show-word-limit id="system-dictionaries-add-dicCode"></el-input>
      </el-form-item>
      <el-form-item label="顺序" prop="sortNo">
        <el-input-number v-model.number="addForm.sortNo" :min="0" controls-position="right" style="width: 100%;" id="system-dictionaries-add-sortNo"></el-input-number>
        <!-- <el-input v-model.number="addForm.sortNo" type="number" :min="0" :max="999"></el-input> -->
      </el-form-item>
      <el-form-item label="备注" prop="dicDesc">
        <el-input v-model.trim="addForm.dicDesc" placeholder="请输入备注" type="textarea" maxlength="100" rows="5" show-word-limit resize="none" id="system-dictionaries-add-dicDesc"></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="system-dictionaries-add-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToSave" id="system-dictionaries-add-save">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.dicName = this.addForm.dicName;
            data.parentId = this.addForm.parentId;
            data.id = 0;
            this.$axios.post('sys/dictInfo/checkDicinfoName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.dicName = '';
                } else {
                    callback();
                }
            });
        };
        var checkDicinfoCode = (rule, value, callback) => {
            let data = {};
            data.dicCode = this.addForm.dicCode;
            data.parentId = this.addForm.parentId;
            data.id = 0;
            this.$axios.post('sys/dictInfo/checkDicinfoCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.dicCode = '';
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
                dicCode: '',
                // 字典描述
                dicDesc: '',
                // 字典名称
                dicName: '',
                // 父节点ID
                parentId: 0,
                // 顺序
                sortNo: ''
            },
            // 验证规则
            rules: {
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
            },

            // 父节点名称
            parentName: '字典类型'
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
            JSON.stringify(data) == '{}' ? (this.addForm.parentId = 0) : (this.addForm.parentId = data.id);
            JSON.stringify(data) == '{}' ? (this.parentName = '字典类型') : (this.parentName = data.dicName);
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
                    const res = await this.$axios.post('sys/dictInfo/save', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        // this.$emit('getTreeData');
                        if (this.addForm.parentId == 0) {
                            this.$emit('getTreeData');
                        }
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
