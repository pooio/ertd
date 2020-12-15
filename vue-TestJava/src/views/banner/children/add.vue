<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: banner轮播图
 * @Version: 1.0
-->
<template>
    <!-- 新增表格数据的dialog -->
    <el-dialog title="新增轮播图" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="35%" :before-close="handleToClose">
        <el-form :model="addForm" ref="addForm" :rules="rules" label-width="100px">
            <el-form-item label="标题" prop="title">
                <el-input v-model.trim="addForm.title" id="system-banner-add-title" placeholder="请输入标题" maxlength="16" show-word-limit></el-input>
            </el-form-item>
            
            <el-form-item label="轮播图" prop="img">
                <el-upload class="avatar-uploader" action="" :show-file-list="false" accept=".jpg, .jpge, .png" :on-change="uploadHandleChange" :before-upload="beforeAvatarUpload" :http-request="handleHttpRequest">
                    <img v-if="imageUrl" :src="imageUrl" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>
            <div class="dialog-footer">
                <el-button type="text" @click="handleToClose" id="system-config-add-cancel">取 消</el-button>
                <el-button type="primary" @click="handleToSave" id="system-config-add-save">确 定</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>
<script>
export default {
    data() {
        return {
            // 控制新增dialog的显示与隐藏
            addDialogVisible: false,

            // 新增表单数据
            addForm: {
                // 配置内容
                title: '',
                // 配置类型
                img: ''
            },
            // 验证规则
            rules: {
                title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
                img: [{ required: true, message: '请选择图片', trigger: 'blur' }]
            },
            // 控制图片上传的显示
            isShow: true,
            imageUrl: ''
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
        openDialog(addinfo) {
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
            this.$refs.addForm.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('sys/banner/save', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
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
            this.isShow = true;
            this.addForm.img="";
            this.imageUrl="";
            // 重置表单
            this.$refs.addForm.resetFields();
        },
        // 选择器事件
        handleChange(val) {
            console.log(val);
            if (val == '8') {
                this.isShow = false;
            } else {
                this.isShow = true;
            }
        },
        // 图片上传change事件
        uploadHandleChange(file, fileList) {},
        // 限制文件上传大小和类型
        beforeAvatarUpload(file) {
            var testmsg = file.name.substring(file.name.lastIndexOf('.') + 1);
            const jpg = testmsg == 'jpg' || testmsg == 'JPG';
            const jpeg = testmsg == 'jpeg' || testmsg == 'JPEG';
            const png = testmsg == 'png' || testmsg == 'PNG';
            const isLt2M = file.size / 1024 / 1024 < 1;
            if (!jpg && !jpeg && !png) {
                this.$message({
                    message: '暂不支持该文件类型',
                    type: 'error'
                });
            }
            if (!isLt2M) {
                this.$message({
                    message: '上传文件大小不能超过 1MB!',
                    type: 'error'
                });
            }
            return (jpg || jpeg || png) && isLt2M;
        },
        // 自定义上传事件
        async handleHttpRequest(file) {
            let formData = new FormData();
            formData.append('file', file.file);
            formData.append('type', 0);
            let config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
            const res = await this.$axios.post('upload/fileupload', formData, config);
            if (res.data.code == 0) {
                this.imageUrl = 'data:image/png;base64,' + res.data.data;
                this.addForm.img = 'data:image/png;base64,' + res.data.data;
            }
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
.avatar-uploader /deep/ .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}
.avatar-uploader .el-upload:hover {
    border-color: #409eff;
}
.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
}
.avatar {
    width: 100px;
    height: 100px;
    display: block;
}
</style>
