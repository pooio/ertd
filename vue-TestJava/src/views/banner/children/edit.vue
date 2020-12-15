<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改系統配置的表单
 * @Version: 1.0
-->
<template>
    <el-dialog title="修改轮播图" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="35%" :before-close="handleToClose">
        <el-form ref="editForm" :model="editForm" :rules="rules" label-width="100px">
            <el-form-item label="标题" prop="title">
                <el-input v-model.trim="editForm.title" id="system-banner-edit-title" placeholder="请输入标题" maxlength="16" show-word-limit></el-input>
            </el-form-item>
           
            <el-form-item label="轮播图" prop="img">
                <el-upload class="avatar-uploader" action="" :show-file-list="false" accept=".jpg, .jpge, .png" :on-change="uploadHandleChange" :before-upload="beforeAvatarUpload" :http-request="handleHttpRequest">
                    <img v-if="imageUrl" :src="imageUrl" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </el-form-item>
            <div class="dialog-footer">
                <el-button type="text" @click="handleToClose" id="system-config-edit-cancel">取 消</el-button>
                <el-button type="primary" @click="handleToUpdata" id="system-config-edit-save">确 定</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
export default {
    data() {
        return {
            // 控制编辑表单对话框的显示隐藏
            editDialogVisible: false,
            // 编辑表单的信息
            editForm: {
                // 配置内容
                title: '',
                // 配置类型
                img: '',
                // id
                id: ''
            },
            // 验证规则
            rules: {
                title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
                imageUrl: [{ required: true, message: '请选择图片', trigger: 'blur' }]
            },
            // 控制图片上传的显示
            isShow: true,
            imageUrl: ''
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
            this.imageUrl= row.img;
             this.editForm.img=row.img;
            this.editForm.title=row.title;
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
            this.$refs.editForm.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('sys/banner/updata', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
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
        uploadHandleChange() {},
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
                this.editForm.img = 'data:image/png;base64,' + res.data.data;
            }
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
