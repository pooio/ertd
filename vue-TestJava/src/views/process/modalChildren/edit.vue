<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 15:25:05
 * @LastEditTime: 2020-06-28 14:56:06
 -->

<template>
    <!-- 新增表格数据的dialog -->
    <div>
        <el-form :model="addForm" ref="ruleForm" :rules="rules" label-width="80px">
            <el-form-item label="模型标识" prop="key">
                <el-input
                    v-model="addForm.key"
                    placeholder="请输入模型名称"
                    maxlength="26"
                    show-word-limit
                ></el-input>
            </el-form-item>
            <el-form-item label="模型名称" prop="name">
                <el-input
                    v-model="addForm.name"
                    placeholder="请输入模型名称"
                    maxlength="16"
                    show-word-limit
                ></el-input>
            </el-form-item>
            <el-form-item label="模型描述" prop="description">
                <el-input
                    type="textarea"
                    v-model="addForm.description"
                    placeholder="请输入模型描述"
                    maxlength="225"
                    show-word-limit
                ></el-input>
            </el-form-item>

            <div class="dialog-footer">
                <el-button type="text" @click="resetForm('ruleForm')">取 消</el-button>
                <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
            </div>
        </el-form>
    </div>
</template>

<script>
export default {
    props: {
        editDataProp: {
            type: Object
        }
    },
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.modeKey = this.addForm.key;
            data.id = this.addForm.id;
            this.$axios.post('bpm/modeler/checkModelName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.key = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 新增表单数据
            addForm: {
                id: '',
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
    mounted() {
        this.addForm.id = this.editDataProp.id;
        this.addForm.key = this.editDataProp.key;
        this.addForm.description = JSON.parse(this.editDataProp.metaInfo).description;

        this.addForm.name = this.editDataProp.name;
        this.addForm.description = JSON.parse(this.editDataProp.metaInfo).description;
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
                        if (res.data.code == 0) {
                            // this.$message.success('');
                            this.$confirm('新增成功！, 是否跳转流程编辑页面?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            })
                                .then(() => {
                                    window.location.href = this.BASE_URL.LIU_CHENG + '/web/bpm/modelerIndex.html?modelId=' + res.data.data;
                                    this.$emit('dialogEdit', false);
                                })
                                .catch(() => {
                                    this.$emit('dialogEdit', false);
                                });
                        } else {
                            this.$message.success(res.data);
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
            this.$emit('dialogEdit', false);
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
