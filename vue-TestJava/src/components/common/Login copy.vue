<template>
    <div class="box">
        <div class="login-wrap">
            <div class="ms-login">
                <div class="ms-title" v-text="pageTitle"></div>
                <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
                    <el-form-item prop="username">
                        <el-input v-model="param.username" placeholder="请输入用户名">
                            <el-button slot="prepend" icon="el-icon-user"></el-button>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input :type="pwdType" v-model="param.password" placeholder="请输入密码" @keyup.enter.native="submitForm()">
                            <el-button slot="prepend" icon="el-icon-lock"></el-button>
                            <i slot="suffix" class="el-input__icon el-icon-view" :class="{ pwd: isPwd }" @click="handleToPwd"></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-col :span="19">
                            <el-checkbox v-model="checked">记住密码</el-checkbox>
                        </el-col>
                        <el-col :span="5"><span @click="forgotPwdBtn()" class="handleToLogin forgotPwd">忘记密码</span></el-col>
                    </el-form-item>
                    <div class="login-btn">
                        <el-button type="primary" @click="submitForm()">登录</el-button>
                    </div>
                </el-form>
            </div>
            <div class="copyright" v-text="copyRight"></div>
        </div>
    </div>
</template>

<script>
import qs from 'qs';
import { log } from 'util';
export default {
    data: function () {
        return {
            param: {
                username: '',
                password: ''
            },
            rules: {
                username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
                password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
            },
            // 版权信息
            copyRight: '',
            // 登录标题
            pageTitle: '',
            pwdType: 'password',
            isPwd: false,
            checked: false //记住密码功能
        };
    },
    created() {
        this.getSysConfig();
    },
    mounted() {
        this.getCookie();
    },
    methods: {
        submitForm() {
            if (this.checked == true) {
                this.setCookie(this.param.username, this.param.password, 7);
            } else {
                this.clearCookie();
            }
            this.$refs.login.validate((valid) => {
                if (valid) {
                    // this.$router.push('/');
                    this.$axios
                        .post('login', {
                            username: this.param.username,
                            password: this.param.password
                        })
                        .then((res) => {
                            // console.log(res);
                            if (res.data.code == 0) {
                                // this.$notify({
                                //     title: '成功',
                                //     message: '登录成功',
                                //     type: 'success'
                                // });
                                this.$message.success('登录成功');
                                sessionStorage.setItem('sessionId', res.data.data.sessionId);
                                // sessionStorage.setItem('headUrl', res.data.data.userInfo.headUrl);
                                sessionStorage.setItem('token', res.data.data.authCode);
                                sessionStorage.setItem('userName', res.data.data.userInfo.name);
                                sessionStorage.setItem('userId', res.data.data.userInfo.id);
                                if (sessionStorage.getItem('Theme') == 'null') {
                                    sessionStorage.setItem('Theme', 'default');
                                } else {
                                    sessionStorage.getItem('Theme');
                                }
                                // sessionStorage.setItem('Theme',(sessionStorage.getItem('Theme') == 'null' ? 'default': sessionStorage.getItem('Theme')));
                                this.$router.push('/');
                            } else {
                                this.$message.error(`${res.data.data}`);
                            }
                        });
                } else {
                    this.$message.error('请输入账号和密码');
                    return false;
                }
            });
        },
        // 设置cookie
        setCookie(c_name, c_pwd, exdays) {
            var exdate = new Date();
            exdate.setTime(exdate.getTime() + 24 * 60 * 60 * 1000 * exdays); //保存的天数
            // 字符串拼接cookie
            window.document.cookie = 'userName' + '=' + c_name + ';path=/;expires=' + exdate.toGMTString();
            window.document.cookie = 'userPwd' + '=' + c_pwd + ';path=/;expires=' + exdate.toGMTString();
        },
        // 读取cookie
        getCookie() {
            if (document.cookie.length > 0) {
                var arr = document.cookie.split('; ');
                for (var i = 0; i < arr.length; i++) {
                    var arr2 = arr[i].split('=');
                    // 判断查找相对应的值
                    if (arr2[0] == 'userName') {
                        this.param.username = arr2[1];
                    } else if (arr2[0] == 'userPwd') {
                        this.param.password = arr2[1];
                        if (this.param.password) {
                            this.checked = true;
                        }
                    }
                }
            }
        },
        // 清除cookie
        clearCookie() {
            this.setCookie(this.param.username, '', 1);
        },
        async getSysConfig() {
            const res = await this.$axios.post('sys/sysConfig/allList');
            if (res.data.code == 0) {
                res.data.data.forEach((item) => {
                    if (item.configType == 1) {
                        sessionStorage.setItem('documentTitle', item.configContent);
                    } else if (item.configType == 2) {
                        sessionStorage.setItem('pageTitle', item.configContent);
                    } else if (item.configType == 3) {
                        sessionStorage.setItem('copyRight', item.configContent);
                    }
                });
                document.title = sessionStorage.getItem('documentTitle');
                this.copyRight = sessionStorage.getItem('copyRight');
                this.pageTitle = sessionStorage.getItem('pageTitle');
            }
        },
        handleToPwd() {
            this.isPwd = !this.isPwd;
            if (this.isPwd) {
                this.pwdType = 'text';
            } else {
                this.pwdType = 'password';
            }
        },
        // 忘记密码按钮
        forgotPwdBtn() {
            this.$router.push('/ForgotPassword');
        }
    }
};
</script>

<style scoped>
@import '../../assets/css/style.css';
.box {
    background: url('../../assets/img/login_bgb.png') bottom right no-repeat;
}
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background: url('../../assets/img/login_bg.png') center no-repeat;
    /* background-image: url(https://api.isoyu.com/bing_images.php); */
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    border-bottom: 1px solid #ddd;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: #fff;
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
.copyright {
    position: absolute;
    bottom: 0;
    width: 100%;
    text-align: center;
}
.pwd {
    color: #409eff;
}
.forgotPwd:hover {
    color: #409eff;
}
</style>