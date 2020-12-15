<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 14:11:06
 * @LastEditTime: 2020-07-07 17:18:56
 -->
<template>
    <!-- 新增表格数据的dialog -->
    <div>
        <el-form :model="addForm" ref="ruleForm" :rules="rules" label-width="80px">
            <el-form-item label="模型标识" prop="key">
                <el-input
                    v-model.trim="addForm.key"
                    placeholder="请输入模型标识"
                    maxlength="26"
                    show-word-limit
                    id="process-modal-add-key"
                ></el-input>
            </el-form-item>
            <el-form-item label="模型名称" prop="name">
                <el-input
                    v-model.trim="addForm.name"
                    placeholder="请输入模型名称"
                    maxlength="16"
                    show-word-limit
                    id="process-modal-add-name"
                ></el-input>
                 
            </el-form-item>
            <el-form-item label="模型描述" prop="description">
                <el-input
                    type="textarea"
                    v-model.trim="addForm.description"
                    placeholder="请输入模型描述"
                    maxlength="225"
                    show-word-limit
                    id="process-modal-add-description"
                ></el-input>
            </el-form-item>

            <div class="dialog-footer">
                <el-button
                    type="text"
                    @click="resetForm('ruleForm')"
                    id="process-modal-add-cancel"
                >取 消</el-button>
                <el-button
                    type="primary"
                    @click="submitForm('ruleForm')"
                    id="process-modal-add-save"
                >确 定</el-button>
            </div>
        </el-form>
    </div>
</template>

<script>
export default {
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.modeKey = this.addForm.key;
            this.$axios.post('bpm/modeler/checkModelName', data).then(res => {
                console.log(res.data.data.data);

                if (!res.data.data.data) {
                    callback(new Error('模型标识重复，请重新输入'));
                    this.addForm.key = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 新增表单数据
            addForm: {
                key: '',
                name: '',
                description: ''
            },
            // 验证规则
            rules: {
                key: [
                    { required: true, message: '请输入模型标识', trigger: 'blur' },
                    { validator: checkDicinfoName, trigger: 'blur' }
                ],
                name: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
                description: [{ required: false, message: '请输入备注', trigger: 'blur' }]
            },

            // 父节点名称
            parentName: ''
        };
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description: 提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        submitForm(formName) {
            this.$refs[formName].validate(valid => {
                if (valid) {
                    this.$axios.post('bpm/modeler/open', this.addForm).then(res => {
                        console.log(res);

                        if (res.data.code == 0) {
                            // this.$message.success('');
                            this.$confirm('新增成功！, 是否跳转流程编辑页面?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            })
                                .then(() => {
                                    this.$emit('dialog', false);
                                    window.open(`${this.BASE_URL.LIU_CHENG}/bpm/modelerIndex.html?modelId=${res.data.data}`, '_blank');
                                })
                                .catch(() => {
                                    this.$emit('dialog', false);
                                });
                        } else {
                            this.$message.error('保存失败');
                        }
                        console.log(res);
                    });
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        /**
         * @Author: flynn.yang
         * @description: 重置表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        resetForm(formName) {
            this.$refs[formName].resetFields();
            this.$emit('dialog', false);
        },
        resetFormData(formName) {
            this.$refs[formName].resetFields();
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
