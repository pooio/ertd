<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 14:11:06
 * @LastEditTime: 2020-07-13 11:47:58
 -->
<template>
    <!-- 新增表格数据的dialog-->
    <div>
        <el-form :model="addForm" ref="ruleForm" :rules="rules" label-width="80px">
            <el-form-item label="模型标识" prop="key">
                <el-input
                    v-model.trim="addForm.key"
                    placeholder="请输入模型标识"
                    maxlength="26"
                    show-word-limit
                    id="process-modal-import-key"
                ></el-input>
            </el-form-item>
            <el-form-item label="模型名称" prop="name">
                <el-input
                    v-model.trim="addForm.name"
                    placeholder="请输入模型名称"
                    maxlength="16"
                    show-word-limit
                    id="process-modal-import-name"
                ></el-input>
            </el-form-item>
            <el-form-item label="模型描述" prop="description">
                <el-input
                    type="textarea"
                    v-model.trim="addForm.description"
                    placeholder="请输入模型描述"
                    maxlength="225"
                    show-word-limit
                    id="process-modal-import-description"
                ></el-input>
            </el-form-item>
            <el-form-item label="上传文件" prop="description">
                <el-upload
                    class="upload-demo"
                    :limit="1"
                    :file-list="fileList"
                    :on-change="handleChange"
                    :on-remove="handleToRemove"
                    :http-request="handleHttpRequest"
                    accept=".json"
                >
                    <el-button size="small" type="primary">选择文件</el-button>
                    <div slot="tip" class="el-upload__tip">只能上传json文件，且不超过1M</div>
                </el-upload>
            </el-form-item>
            <div class="dialog-footer">
                <el-button type="text" @click="resetForm('ruleForm')" id="process-modal-import-cancel">取 消</el-button>
                <el-button type="primary" @click="submitForm('ruleForm')" id="process-modal-import-save">确 定</el-button>
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
                description: [
                    {
                        required: false,
                        message: '请输入请输入模型描述',
                        trigger: 'blur'
                    }
                ]
            },

            // 父节点名称
            parentName: '',
            fileList: []
        };
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description: 文件上传
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleChange(file, fileList) {
            console.log(file);
            console.log(fileList);
            this.fileList = fileList;
        },
        handleToRemove(file, fileList) {
            if (fileList.length == 0) {
                this.form.fileId = '';
            }
        },
        async handleHttpRequest(file) {},

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
                    let formData = new FormData();
                    formData.append('file', this.fileList[0].raw);
                    formData.append('modeKey', this.addForm.key);
                    formData.append('modeName', this.addForm.name);
                    formData.append('modeDescription', this.addForm.description);
                    let config = {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    };
                    this.$axios.post('bpm/modeler/importFile', formData).then(res => {
                        if (res.data.code == 0) {
                            this.$message({
                                type: 'success',
                                message: '保存成功'
                            });
                            this.$emit('dialogImport', false);
                        } else {
                            this.$message({
                                type: 'error',
                                message: res.data.data
                            });
                        }
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
            this.$emit('dialogImport', false);
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
