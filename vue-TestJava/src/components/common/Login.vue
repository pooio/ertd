<template>
    <div class="login">
        <div class="login-wrap" v-if="show">
            <img class="logo" :src="logoIMG" alt="" v-if="logoIMG">
            <div class="title">{{pageTitle}}</div>
            <div class="ms-login">
                <div class="ms-title">用户登录</div>
                <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content" size="medium">
                    <el-form-item prop="username">
                        <el-input v-model="param.username" placeholder="请输入用户名" prefix-icon="el-icon-user">
                            <!-- <el-button slot="prepend" icon="el-icon-user"></el-button> -->
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input :type="pwdType" v-model="param.password" placeholder="请输入密码" prefix-icon="el-icon-lock" @keyup.enter.native="submitForm()">
                            <!-- <el-button slot="prepend" icon="el-icon-lock"></el-button> -->
                            <i slot="suffix" class="el-input__icon el-icon-view" :class="{ pwd: isPwd }" @click="handleToPwd"></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-col :span="19">
                            <el-checkbox v-model="checked" style="color:#D3D3D3">记住密码</el-checkbox>
                        </el-col>
                        <el-col :span="5"><span @click="forgotPwdBtn()" class="handleToLogin forgotPwd" style="color:#D3D3D3">忘记密码</span></el-col>
                    </el-form-item>
                    <div class="login-btn">
                        <el-button size="medium" type="primary" @click="submitForm()">登录</el-button>
                    </div>
                </el-form>
            </div>
            <div class="copyright" v-text="copyRight"></div>
        </div>
        <div class="browser" v-else>
            <div class="top">
                <!-- <img src="../../assets/img/shan.png" alt=""> -->
            </div>
            <div class="center">
                <p>暂不支持IE浏览器</p>
                <p>为确保您正常使用、建议安装以下浏览器进行使用</p>
            </div>
            <div class="bottom">
                <div class="item" @click="handleToDownload('chrome')">
                    <img src="../../assets/img/chrome.png" title="点击跳转到下载地址">
                    <h2>Chrome浏览器</h2>
                </div>
                <div class="item" @click="handleToDownload('edge')">
                    <img src="../../assets/img/edge.png" title="点击跳转到下载地址">
                    <h2>Edge浏览器</h2>
                </div>
                <div class="item" @click="handleToDownload('firefox')">
                    <img src="../../assets/img/firefox.png" title="点击跳转到下载地址">
                    <h2>Firefox浏览器</h2>
                </div>
                <!-- <div class="item" @click="handleToDownload('safari')">
                    <img src="../../assets/img/safari.png" title="点击跳转到下载地址">
                    <h2>Safari浏览器</h2>
                </div> -->
            </div>
        </div>
    </div>
</template>

<script>
import qs from 'qs';
import { log } from 'util';
import { initDynamicRoutes } from '@/router';
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
            // logo
            logoIMG: '',
            pwdType: 'password',
            isPwd: false,
            checked: false, //记住密码功能
            show: false
        };
    },
    created() {
        this.getSysConfig();
        this.checkBrowser();
    },
    mounted() {
        this.getCookie();
    },
    methods: {
        // 检测浏览器
        checkBrowser() {
            var getExplorer = (function () {
                var explorer = window.navigator.userAgent,
                    compare = function (s) {
                        return explorer.indexOf(s) >= 0;
                    },
                    ie11 = (function () {
                        return 'ActiveXObject' in window;
                    })();
                if (compare('MSIE') || ie11) {
                    return 'ie';
                } else if (compare('Firefox') && !ie11) {
                    return 'Firefox';
                } else if (compare('Chrome') && !ie11) {
                    return 'Chrome';
                } else if (compare('Opera') && !ie11) {
                    return 'Opera';
                } else if (compare('Safari') && !ie11) {
                    return 'Safari';
                }
            })();
            if (getExplorer == 'ie') {
            } else {
                this.show = true;
            }
        },
        handleToDownload(val) {
            switch (val) {
                case 'chrome':
                    window.open('https://www.google.cn/chrome/', '_blank');
                    break;
                case 'edge':
                    window.open('https://www.microsoft.com/zh-cn/edge', '_blank');
                    break;
                case 'firefox':
                    window.open('https://www.firefox.com.cn/', '_blank');
                    break;
                case 'safari':
                    window.open('https://www.apple.com.cn/safari/', '_blank');
                    break;
            }
        },
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
                                // 把token存到vuex中
                                // this.$store.dispatch('setToken', res.data.data.authCode)
                                // this.$notify({
                                //     title: '成功',
                                //     message: '登录成功',
                                //     type: 'success'
                                // });
                                // sessionStorage.setItem('sessionId', res.data.data.sessionId);
                                sessionStorage.setItem('token', res.data.data.authCode);
                                sessionStorage.setItem('userName', res.data.data.userInfo.name);
                                sessionStorage.setItem('userId', res.data.data.userInfo.id);
                                if (sessionStorage.getItem('Theme') == 'null') {
                                    sessionStorage.setItem('Theme', 'default');
                                } else {
                                    sessionStorage.getItem('Theme');
                                }
                                // this.$axios.post('sys/menu/test').then((res) => {
                                //     this.$store.commit('setRightList', res.data.data);
                                //     initDynamicRoutes();
                                // });
                                this.$axios
                                    .post('sys/menu/treeData', {
                                        roleId: 1,
                                        type: 1
                                    })
                                    .then((res) => {
                                        this.$store.commit('setRightList', res.data.data);
                                        initDynamicRoutes();
                                    });
                                this.$message.success('登录成功');
                                console.log('登陆成功');
                                this.$router.push('/home');
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
                    } else if (item.configType == 8) {
                        sessionStorage.setItem('logoIMG', item.configContent);
                    }
                });
                document.title = sessionStorage.getItem('documentTitle');
                this.copyRight = sessionStorage.getItem('copyRight');
                this.pageTitle = sessionStorage.getItem('pageTitle');
                this.logoIMG = sessionStorage.getItem('logoIMG');
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

<style lang="less" scoped>
@import '../../assets/css/style.css';
.browser {
    width: 100%;
    height: 100%;
    background-color: #ffffff;
    .top {
        margin: 0 300px;
        height: 50%;
        background: url('../../assets/img/mountains.png') center no-repeat;
        background-size: 40%;
    }
    .center {
        margin: 0 300px;
        padding-bottom: 100px;
        text-align: center;
        border-bottom: 1px solid #ccc;
        color: #333333;
        font-size: 40px;
        font-weight: 700;
    }
    .bottom {
        margin: 50px 300px 0;
        display: flex;
        justify-content: space-around;
        .item {
            width: 25%;
            text-align: center;
            color: #333333;
            cursor: pointer;
            img {
                width: 30%;
            }
            h2 {
                margin-top: 20px;
            }
        }
    }
}
.login {
    box-sizing: border-box;
    height: 100%;
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
.login-wrap .logo {
    position: absolute;
    top: 5%;
    left: 6%;
    height: 60px;
    background-color: #000;
}
.login-wrap .title {
    position: absolute;
    top: 46.3%;
    left: 20.15%;
    /* font-size: 6vh; */
    font-size: 3vw;
    color: #fff;
    font-weight: 400;
    line-height: 4vh;
}
.ms-title {
    width: 100%;
    line-height: 72px;
    text-align: center;
    font-size: 20px;
    color: #fff;
    letter-spacing: 2px;
    /* border-bottom: 1px solid #ddd; */
}
.ms-login {
    position: absolute;
    right: 13%;
    top: 50%;
    transform: translate(0%, -50%);
    width: 472px;
    height: 383px;
    border-radius: 5px;
    background: url('../../assets/img/login_denglu.png') center no-repeat;
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px 30px 37px;
}
.ms-content /deep/ .el-input__prefix {
    color: #1885f2;
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
    bottom: 15px;
    width: 100%;
    text-align: center;
    color: #c2c2c2;
}
.pwd {
    color: #409eff;
}
.forgotPwd:hover {
    color: #409eff;
}
</style>