<template>
    <div>
        <el-row class="input-style">
            <el-divider content-position="left">个人资料</el-divider>
            <el-form ref="personalInfo" :model="personalInfo" :rules="personalInfoRules" label-width="120px">
                <el-col :span="16" :offset="3" style="margin-bottom:20px;text-align:center;">
                    <el-form-item prop="headImg">
                        <el-upload
                            class="avatar-uploader"
                            :action="uploadUrl"
                            :show-file-list="false"
                            :on-change="uploadHandleChange"
                            :http-request="handleHttpRequest"
                            :auto-upload="false"
                            :disabled="personalInfo.headImg!=null&&personalInfo.headImg!=''"
                        >
                            <img v-if="personalInfo.headImg!=null&&personalInfo.headImg!=''" :src="personalInfo.headImg" class="avatar" id="personalInfo-headImg"/>
                            <img v-else src="../../assets/img/headImg.png" class="avatar" alt="">
                            <i v-if="personalInfo.headImg!=null&&personalInfo.headImg!=''" class="el-icon-close delete_img" @click="deleteImg"></i>
                        </el-upload>
                        <div class="upload_span">(支持jpg、jpeg、gif、png、bmp 格式,大小2MB以内!)</div>
                    </el-form-item>
                </el-col>
                <el-col :span="9" :offset="2">
                    <el-form-item label="登录名" prop="loginName">
                        <el-input v-model.number="personalInfo.loginName" disabled id="personalInfo-loginName"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model.number="personalInfo.name" disabled placeholder="请输入姓名" id="personalInfo-name"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9" :offset="2">
                    <el-form-item label="年龄" prop="age">
                        <el-input v-model.number="personalInfo.age" placeholder="请输入年龄" id="personalInfo-age"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9">
                    <el-form-item label="性别" prop="sex">
                        <el-select v-model="personalInfo.sex" placeholder="请选择性别" id="personalInfo-sex" clearable>
                            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="9" :offset="2">
                    <el-form-item label="公司" prop="company">
                        <el-input v-model.trim="personalInfo.company" placeholder="请输入公司名称" maxlength="30" show-word-limit id="personalInfo-company"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9">
                    <el-form-item label="QQ" prop="qqAcount">
                        <el-input v-model.trim="personalInfo.qqAcount" placeholder="请输入QQ号码" maxlength="10" show-word-limit id="personalInfo-qqAcount"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9" :offset="2">
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model.trim="personalInfo.email" placeholder="请输入邮箱地址" maxlength="30" show-word-limit id="personalInfo-email"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="9">
                    <el-form-item label="手机号" prop="phoneNumber">
                        <el-input v-model.trim="personalInfo.phoneNumber" placeholder="请输入手机号" maxlength="11" show-word-limit id="personalInfo-phoneNumber"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="16" :offset="3" style="text-align:center;">
                    <el-form-item>
                        <el-button type="primary" @click="onSubmit('personalInfo')" id="personalInfo-save">保存</el-button>
                    </el-form-item>
                </el-col>
            </el-form>
        </el-row>
        <!-- 图片裁剪 -->
        <el-dialog title="图片剪裁" :visible.sync="dialogVisible" append-to-body :close-on-click-modal="false">
            <div class="cropper-content">
                <div class="cropper" style="text-align: center;">
                    <vueCropper
                        ref="cropper"
                        :img="option.img"
                        :outputSize="option.size"
                        :outputType="option.outputType"
                        :info="true"
                        :full="option.full"
                        :canMove="option.canMove"
                        :canMoveBox="option.canMoveBox"
                        :original="option.original"
                        :autoCrop="option.autoCrop"
                        :fixed="option.fixed"
                        :fixedNumber="option.fixedNumber"
                        :centerBox="option.centerBox"
                        :infoTrue="option.infoTrue"
                        :fixedBox="option.fixedBox"
                    ></vueCropper>
                </div>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="changeScale(1)">+</el-button>
                <el-button @click="changeScale(-1)">-</el-button>
                <el-button @click="rotateLeft">↺</el-button>
                <el-button @click="rotateRight">↻</el-button>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="finish">上传</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import { VueCropper } from 'vue-cropper';
import Bus from '../../components/common/bus.js';
export default {
    data() {
        var checkAge = (rule, value, callback) => {
            if (!value && value !== 0) {
                return callback(new Error('请输入年龄'));
            }
            setTimeout(() => {
                if (!Number.isInteger(+value)) {
                    callback(new Error('请输入数字'));
                } else if (value > 120 || value < 1) {
                    callback(new Error('年龄有效范围为1-120岁'));
                } else {
                    callback();
                }
            }, 300);
        };
        var checkPhone = (rule, value, callback) => {
            const phoneReg = /^1[3|4|5|6|7|8][0-9]{9}$/;
            if (!value) {
                return callback(new Error('请输入手机号'));
            }
            setTimeout(() => {
                if (!Number.isInteger(+value)) {
                    callback(new Error('请输入正确的手机号'));
                } else {
                    if (phoneReg.test(value)) {
                        callback();
                    } else {
                        callback(new Error('请输入正确的手机号'));
                    }
                }
            }, 100);
        };
        var checkQQ = (rule, value, callback) => {
            const qqReg = /^[1-9][0-9]{4,9}$/gim;
            console.log(value);
            if (value!=null&&value!='') {
                setTimeout(() => {
                    if (!Number.isInteger(+value)) {
                        callback(new Error('请输入正确的QQ'));
                    } else {
                        if (qqReg.test(value)) {
                            callback();
                        } else {
                            callback(new Error('请输入正确的QQ'));
                        }
                    }
                }, 100)
            }else{
                callback();
            };
        };
        var checkPwd = (rule, value, callback) => {
            let data = {};
            data.oldPassword = this.pwdForm.oldPwd;
            this.$axios.post('sys/user/oldPasswordIsRight', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.pwdForm.oldPwd = '';
                } else {
                    callback();
                }
            });
        };
        var checkAge = (rule, value, callback) => {
            if (value!=null&&String(value)!='') {
                setTimeout(() => {
                    if (!Number.isInteger(+value)) {
                        callback(new Error('请输入正确的年龄'));
                    } else {
                        if (value*1>0 && value*1<121) {
                            callback();
                        } else {
                            callback(new Error('年龄应在1到120之间'));
                        }
                    }
                }, 100)
            }else{
                callback();
            };
        };
        const validatePass = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.pwdForm.newPwd) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        };
        return {
            personalInfo: {
                id: window.sessionStorage.getItem('userId'),
                headId: '',
                // headUrl: '',
                loginName: '', //登录名
                name: '', //姓名
                age: '', //年龄
                sex: '', //性别
                company: '', //公司
                qqAcount: '', //qq
                email: '', //邮箱
                phoneNumber: '', //手机
                headImg: '' //base64
                // roleId:"971e8a83e8fa407688bf252ce039a30d"//角色ID
            },
            options: [
                {
                    value: 1,
                    label: '男'
                },
                {
                    value: 0,
                    label: '女'
                }
            ],
            pwdForm: {
                // 旧密码
                oldPwd: '',
                // 新密码
                newPwd: '',
                // 确认新密码
                confirmNewPwd: ''
            },
            option: {
                img: '', // 裁剪图片的地址
                info: false, // 裁剪框的大小信息
                outputSize: 0.8, // 裁剪生成图片的质量
                outputType: 'jpeg', // 裁剪生成图片的格式
                canScale: false, // 图片是否允许滚轮缩放
                autoCrop: true, // 是否默认生成截图框
                autoCropWidth: 160, // 默认生成截图框宽度
                autoCropHeight: 200, // 默认生成截图框高度
                fixedBox: false, // 固定截图框大小 不允许改变
                fixed: true, // 是否开启截图框宽高固定比例
                fixedNumber: [4, 5], // 截图框的宽高比例
                full: true, // 是否输出原图比例的截图
                canMoveBox: true, // 截图框能否拖动
                canMove: false,
                original: false, // 上传图片按照原始比例渲染
                centerBox: true, // 截图框是否被限制在图片里面
                infoTrue: true // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
            },
            dialogVisible: false, //图片裁剪的弹框dialog
            filename: '', //文件名
            uploadUrl: '', //文件上传地址
            passRules: {
                oldPwd: [
                    { required: true, message: '请输入旧密码', trigger: 'blur' },
                    { validator: checkPwd, message: '旧密码错误', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
                ],
                newPwd: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
                ],
                confirmNewPwd: [
                    { required: true, trigger: 'blur', validator: validatePass },
                    { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
                ]
            },
            personalInfoRules: {
                // headImg: [
                //     {
                //         required: false,
                //         message: '请选择个人头像',
                //         trigger: 'change'
                //     }
                // ],
                age: [{ validator: checkAge, trigger: 'blur' }],
                // sex: [{ required: false, message: '请选择性别', trigger: 'change' }],
                phoneNumber: [{ required: true, validator: checkPhone, trigger: 'blur' }],
                qqAcount: [{ validator: checkQQ, trigger: 'blur'}],
                company: [
                    // { required: false, message: '请输入公司名称', trigger: 'blur' },
                    { min: 1, max: 32, message: '长度在 1 到 32个字符', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
                ]
            },
            // 修改密码文本框的type
            pwdType1: 'password',
            isPwd1: false,
            pwdType2: 'password',
            isPwd2: false,
            pwdType3: 'password',
            isPwd3: true,
        };
    },
    components: {
        VueCropper
    },
    mounted() {
        this.getUserInfo();
    },
    methods: {
        //获取个人信息
        getUserInfo() {
            let that = this;
            that.$axios.post('/sys/user/getUserInfoSelf', {}).then(function(res) {
                console.log(res);
                if (res.data.code == 0) {
                    that.personalInfo = res.data.data;
                }
            });
        },
        //图片改变的钩子
        uploadHandleChange(file) {
            this.fileName = file.name;
            const isLt2M = file.size / 1024 / 1024 < 2;
            const isIMG = ['.jpeg', '.jpg', '.png', '.bmp', '.gif', '.JPEG', '.JPG', '.GIF', '.PNG', '.BMP'].some(r =>
                file.name.endsWith(r)
            );
            if (!isIMG) {
                this.$message.error('上传图片只能是 jpg、jpeg、gif、 png、bmp 格式!');
                return false;
            }
            if (!isLt2M) {
                this.$message.error('上传图片大小不能超过 2MB!');
                return false;
            }
            this.$nextTick(() => {
                this.option.img = URL.createObjectURL(file.raw);
                this.dialogVisible = true;
            });
        },
        //保存个人信息
        onSubmit(formName) {
            let that = this;
            console.log('保存');
            console.log(that.personalInfo);
            that.$refs[formName].validate(valid => {
                console.log(valid);
                if (valid) {
                    that.$axios.post('/sys/user/updateUserInfoSelf', that.personalInfo).then(function(res) {
                        console.log(res);
                        if (res.data.code == 0) {
                            that.$message.success('保存成功');
                            //Event Bus
                            Bus.$emit('getTarget', event.target);
                        } else {
                            that.$message.error(`${res.data.data}`);
                        }
                    });
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        changeScale(val) {
            this.$refs.cropper.changeScale(val);
        },
        // 旋转图片
        rotateLeft() {
            this.$refs.cropper.rotateLeft();
        },
        rotateRight() {
            this.$refs.cropper.rotateRight();
        },
        //确定裁剪
        finish() {
            this.$refs.cropper.getCropData(res => {
                let file = this.dataURLtoFile(res, this.fileName);
                this.handleHttpRequest(file);
                this.dialogVisible = false;
            });
        },
        // base64转file
        dataURLtoFile(dataurl, filename) {
            var arr = dataurl.split(','),
                mime = arr[0].match(/:(.*?);/)[1],
                bstr = atob(arr[1]),
                n = bstr.length,
                u8arr = new Uint8Array(n);
            while (n--) {
                u8arr[n] = bstr.charCodeAt(n);
            }
            return new File([u8arr], filename, { type: mime });
        },
        //上传事件
        async handleHttpRequest(file) {
            console.log(file);
            let formData = new FormData();
            formData.append('file', file);
            formData.append('type', 0);
            let config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
            const res = await this.$axios.post('upload/fileupload', formData, config);
            if (res.data.code == 0) {
                this.personalInfo.headImg = 'data:image/png;base64,' + res.data.data;
            }
        },
        //点击关闭按钮时关闭裁剪弹出框
        handleClose() {
            this.dialogVisible = false;
        },
        //点击关闭按钮时关闭裁剪弹出框
        deleteImg() {
            this.personalInfo.headImg = '';
        }
    }
};
</script>
<style lang="less" scoped>
.avatar-uploader /deep/ .el-upload {
    position: relative;
}
.avatar-uploader .delete_img{
    position: absolute;
    top: 10px;
    right: 10px;
    width: 24px;
    height: 24px;
    display: inline-block;
    color: #fff;
    background-color: red;
    font-weight: bold;
    border-radius: 50%;
    line-height: 24px;
}
.avatar {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    display: inline-block;
    border: 1px solid #eeeeee;
}
.upload_span {
    color: #666666;
}
.el-input,
.el-select {
    height: 60px;
    width: 100%;
}
.el-input /deep/.el-input__suffix{
    height:40px!important;
}
.input-style /deep/.el-input--small .el-input__inner {
    height: 40px !important;
    line-height: 40px !important;
}
.el-form-item--small .el-form-item__content,
.el-form-item--small /deep/.el-form-item__label {
    line-height: 40px !important;
    color: #000000;
}
.el-button {
    width: 90px;
    height: 35px;
}
.cropper-content .cropper {
    width: auto;
    height: 300px;
}
.el-icon-view {
    cursor: pointer;
    height: 40px;
}

.pwd {
    color: #409eff;
}
.el-icon-s-open {
    font-size: 24px;
    color: #fff;
}
.input-style .el-input--small .el-input__icon {
    line-height: 40px;
}
.el-divider__text {
    font-size: 16px;
}
</style>
