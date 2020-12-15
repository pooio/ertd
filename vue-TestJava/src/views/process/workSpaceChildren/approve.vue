<!--
 * @页面名称：审批处理页面
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-25 14:11:06
 * @LastEditTime: 2020-07-01 14:00:02
 * @Autor:serena.li
 * @Version: 1.0
 * @Desc: 规范修改文件名称，增加表格的loading属性，删除冗余文件
 * @Date: 2020-07-2 10:48:54
 * @LastEditTime: 2020-07-02 16:17:41
 -->
<template>
    <!-- 审批页面-->
    <div>
        <!-- 审批Form表单 -->
        <formFill :data="formData" v-if="flag"></formFill>
        <!-- 审批意见 -->
        <el-form :model="approveForm" ref="ruleForm" :rules="rules" label-width="110px">
            <el-form-item label="审批意见" prop="description">
                <el-input
                    type="textarea"
                    v-model="approveForm.description"
                    placeholder="请输入审批意见"
                    maxlength="225"
                    show-word-limit
                ></el-input>
            </el-form-item>

            <div class="dialog-footer">
                <el-button type="danger" icon="el-icon-error" @click="refuse('ruleForm')">拒绝</el-button>
                <el-button type="success" icon="el-icon-success"  @click="passThrough('ruleForm')">同意</el-button>
            </div>
        </el-form>
    </div>
</template>

<script>
import formFill from '../../customerForm/children/formFill';
export default {
    props: {
        editDataProp: {
            type: Object
        }
    },
    components: {
        formFill
    },
    data() {
        return {
            // 新增表单数据
            approveForm: {
                description: ''
            },
            // 验证规则
            rules: {
                description: [{ required: false, message: '请输入审批意见', trigger: 'blur' }]
            },
            formData: {}, //form表单数据
            flag: false,
        };
    },
    mounted() {
        console.log(this.editDataProp);
        this.getFormJson();
    },
    methods: {
        // 获取表单页面
        getFormJson() {
            this.$axios
                .post('form/getFormData', { businessKey: this.editDataProp.businessKey })
                .then((res) => {
                    console.log(res);
                    if (res != null) {
                        this.formData = res.data.data;
                        this.$axios
                            .post('formData/getDataJson', { businessKey: this.editDataProp.businessKey })
                            .then((val) => {
                                if (val != null) {
                                    var valData = JSON.parse(val.data.data.customData);
                                    if (this.formData.formJson != null && this.formData.formJson != '') {
                                        var newFormData = JSON.parse(this.formData.formJson);
                                        newFormData.fields.forEach((item) => {
                                            if (item) {
                                                item.disabled = true;
                                                item.__config__.defaultValue = valData[item.__vModel__];
                                            }
                                        });
                                        this.formData.formJson = JSON.stringify(newFormData);
                                        this.flag = true;
                                    }
                                }
                            })
                            .catch((data) => {
                                console.info('报错的信息', err);
                            });
                    }
                })
                .catch((err) => {
                    console.info('报错的信息', err);
                });
        },
        /**
         * @Author: flynn.yang
         * @description: 审批通过
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        passThrough(formName) {
            this.$refs[formName].validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('bpm/complete', {
                        taskId: this.editDataProp.id,
                        taskPass: true,
                        comment: this.approveForm.description,
                        variables: ''
                    });
                    if (res.data.code == 0) {
                        this.$emit('dialog', false);
                        this.$message.success('审批成功');
                    } else {
                        this.$message.success('审批失败');
                    }
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
         * @Author: serena.li
         * @description: 拒绝时更新申请单状态
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async refuse(formName) {
            this.$refs[formName].validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('bpm/complete', {
                        taskId: this.editDataProp.id,
                        taskPass: false,
                        comment: this.approveForm.description,
                        variables: ''
                    });
                    if (res.data.code == 0) {
                        this.$emit('dialog', false);
                        this.$message.success('已拒绝');
                    } else {
                        this.$message.success('拒绝失败');
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
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
