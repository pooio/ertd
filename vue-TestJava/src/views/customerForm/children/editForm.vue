<!--
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 15:25:05
 * @LastEditTime: 2020-06-28 14:56:06
 -->

<template>
    <!-- 新增表格数据的dialog -->
    <div>
        <el-row :gutter="10">
            <el-col :span="16">
                <el-steps :active="active" simple>
                    <el-step title="编辑表单" icon="el-icon-edit"></el-step>
                    <el-step title="设计表单" icon="el-icon-menu"></el-step>
                    <el-step title="绑定流程" icon="el-icon-rank"></el-step>
                </el-steps>
            </el-col>
            <el-col :span="8" style="text-align: right;line-height: 46px;">
                <el-button type="primary" @click="$router.push({path:'form-list'})" icon="el-icon-back" id="form-edit-back">返回</el-button>
                <el-button type="primary" @click="submitForm('ruleForm',1)" id="form-edit-next">下一步</el-button>
            </el-col>
        </el-row>

        <div class="line"></div>
        <el-form :model="editForm" ref="ruleForm" :rules="rules" label-width="120px" style="width:600px; padding-top:20px;">
            <el-form-item label="表单名称" prop="businessName">
                <el-input v-model.trim="editForm.businessName" placeholder="请输入表单名称" maxlength="16" show-word-limit id="form-edit-businessName"></el-input>
            </el-form-item>
            <el-form-item label="按钮图标" prop="icon">
                <el-col :span="21">
                    <el-input v-model="editForm.icon" placeholder="点击选择图标" readonly id="form-edit-icon" style="width: 100%;">
                        <el-button slot="append" icon="el-icon-thumb" @click="openIconsDialog('icon')" id="form-edit-icon-btn">
                            选择
                        </el-button>
                    </el-input>
                </el-col>
                <el-col :span="1" :offset="1">
                    <el-color-picker v-model="editForm.color"></el-color-picker>
                </el-col>
            </el-form-item>
            <el-form-item label="预览图标">
                <div class="preview-icon" :style="'background-color:'+editForm.color">
                    <i :class="editForm.icon"></i>
                </div>
            </el-form-item>
            <el-form-item label="描述" prop="description">
                <el-input type="textarea" v-model.trim="editForm.description" placeholder="请输入描述" maxlength="225" show-word-limit id="form-edit-description"></el-input>
            </el-form-item>
            <el-form-item label="主业务">
                <el-select v-model="editForm.parentId" placeholder="请选择主业务" style="width:100%" clearable>
                    <el-option v-for="item in selectData" :key="item.id" :label="item.businessName" :value="item.id">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="上传模板">
                <el-upload class="upload-demo" :action="uploadUrl" :headers="headers" :data="uploadData" v-model.trim="editForm.wordFileId" :on-exceed="handleExceed" :on-success="handleSuccess" :on-remove="handleRemove" :before-remove="beforeRemove" accept=".docx,.doc" :limit="1" :file-list="fileList">
                    <el-button size="small" type="primary">点击上传</el-button>
                    <div slot="tip" class="el-upload__tip">只能上传doc/docx文件，且不超过2M</div>
                </el-upload>
            </el-form-item>
        </el-form>
        <iconFont-dialog :visible.sync="iconsVisible" :current="currentIconModel" @select="selected" />
    </div>
</template>

<script>
// import IconSelect from '@/components/common/IconSelect';
import IconFontDialog from './IconFontDialog';
import constants from '../../../constants/index';
export default {
    components: {
        // IconSelect,
        IconFontDialog
    },
    data() {
        var checkFormName = (rule, value, callback) => {
            let data = {};
            data.formName = this.editForm.businessName;
            data.id = this.editForm.id;
            this.$axios.post('form/checkFormName', data).then((res) => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.editForm.businessName = '';
                } else {
                    callback();
                }
            });
        };
        return {
            active: 1,
            uploadUrl: constants.baseURL + 'upload/fileupload',
            headers: {
                Authorization: sessionStorage.getItem('token'),
                USERID: sessionStorage.getItem('userId'),
                sessionId: sessionStorage.getItem('sessionId')
            },
            uploadData: {
                type: 1
            },
            fileList: [],
            iconsVisible: false,
            currentIconModel: 'icon',
            // 编辑表单的信息
            editForm: {
                id: '',
                businessName: '',
                icon: '',
                color: '#408eff',
                description: '',
                wordFileId: '',
                parentId: ''
            },
            // 验证规则
            rules: {
                businessName: [
                    { required: true, message: '请输入表单名称', trigger: 'blur' },
                    { validator: checkFormName, message: '表单名称重复', trigger: 'blur' }
                ],
                icon: [{ required: true, message: '请选择图标', trigger: 'blur' }],
                description: [{ required: false, message: '请输入备注', trigger: 'blur' }]
            },
            selectData: []
        };
    },
    created() {
        this.getSelectData();
        if (this.$route.query.data != null && this.$route.query.data != '') {
            var editData = JSON.parse(this.$route.query.data);
            this.editForm.id = editData.id;
            this.editForm.businessName = editData.businessName;
            this.editForm.icon = editData.icon;
            this.editForm.description = editData.description;
            this.editForm.wordFileId = editData.wordFileId;
            this.editForm.parentId = editData.parentId;
            if (editData.wordFileId != null && editData.wordFileId != '') {
                this.fileList.push({ name: editData.fileName });
            }
        }
    },
    mounted() {},
    methods: {
        // 获取选择器数据
        async getSelectData() {
            const res = await this.$axios.post('formData/getIsPublishCustomList', {});
            if (res.data.code == 0) {
                this.selectData = res.data.data;
            }
        },
        /**
         * @Author: flynn.yang
         * @description: 提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        submitForm(formName, index) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.$axios.post('form/updateBussiness', this.editForm).then((res) => {
                        if (res.data.code == 0) {
                            this.$message.success('保存成功');
                            if (index == 0) {
                                this.$router.push({ path: 'form-list' });
                            } else {
                                this.$router.push({ path: 'form-customer', query: { data: this.editForm.id } });
                            }
                        } else {
                            this.$message.error(res.data.data);
                        }
                    });
                } else {
                    return false;
                }
            });
        },
        // 选中图标
        selected(name) {
            this.editForm.icon = name;
        },
        openIconsDialog(model) {
            this.iconsVisible = true;
        },
        handleSuccess(res) {
            if (res.code == 0) {
                console.log(this.fileList);
                this.editForm.wordFileId = res.data.id;
                this.$message.success('上传成功');
            } else {
                this.fileList = [];
                this.editForm.wordFileId = '';
                this.$message.error(res.data);
            }
        },
        handleExceed() {
            this.$message.warning(`当前限制选择 1 个文件`);
        },
        beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${file.name}？`);
        },
        handleRemove() {
            this.editForm.wordFileId = '';
        }
    }
};
</script>

<style lang="less" scoped>
/deep/.el-input-number .el-input__inner {
    text-align: left;
}
.el-textarea /deep/ .el-textarea__inner {
    padding-right: 60px !important;
}
.el-textarea /deep/ .el-input__count {
    right: 18px;
}
.line {
    margin: 18px 0;
    border-top: 1px solid #ccc;
}
.preview-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    text-align: center;
    line-height: 32px;
    color: #fff;
}
</style>
