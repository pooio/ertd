<template>
    <div>
        <div :class="{
                'collapse-btn': collapse,
                'collapse-btn-small': !collapse,
                collapseZhong: ThemeData == 'zhongqiu',
                collapseGuo: ThemeData == 'national',
                collapse: ThemeData == 'default',
                collapseQing: ThemeData == 'qingming',
                collapseChun: ThemeData == 'spring-festival'
            }" @click="collapseChage">
            <i v-if="!collapse" class="el-icon-caret-left"></i>
            <i v-else class="el-icon-caret-right"></i>
        </div>

        <div class="header" :class="{
                headerZhong: ThemeData == 'zhongqiu',
                headerGuo: ThemeData == 'national',
                header1: ThemeData == 'default',
                headerQing: ThemeData == 'qingming',
                headerChun: ThemeData == 'spring-festival'
            }">
            <div class="logoDataImg logoZhong" v-text="title" v-if="ThemeData == 'zhongqiu'"></div>
            <div class="logoDataImg logoGuo" v-text="title" v-else-if="ThemeData == 'national'"></div>
            <div class="logoDataImg logoChun" v-text="title" v-else-if="ThemeData == 'spring-festival'"></div>
            <div class="logoDataImg logoQing" v-text="title" v-else-if="ThemeData == 'qingming'"></div>
            <div class="logoDataImg" v-text="title" v-else-if="ThemeData == 'default'"></div>
            <!-- 折叠按钮 -->

            <!-- <div class="logo" v-text="title"></div> -->
            <div class="header-right">
                <div class="header-user-con">
                    <!-- 换肤 -->
                    <div class="theme">
                        <!-- <el-tooltip effect="dark" content="更换主题" placement="bottom">
                        <router-link to="/theme">
                            <i class="iconfont el-icon-s-open"></i>
                        </router-link>
                        </el-tooltip>-->
                        <!-- 颜色选择器 -->
                        <!-- <el-tooltip effect="dark" content="更换主题" placement="bottom">
                        <el-color-picker
                            @change="colorChage"
                            @active-change="colorActive"
                            v-show="showColorPicker"
                            class="color-picker"
                            popper-class="theme-picker-dropdown"
                            v-model="color"
                            :predefine="predefineColors"
                        ></el-color-picker>
                        </el-tooltip>-->
                        <el-tooltip effect="dark" content="切换主题" placement="bottom">
                            <el-dropdown class="user-name" trigger="click" @command="handleTheme">
                                <span class="el-dropdown-link">
                                    <svg-icon :icon-class="ThemeData" style="height: 32px;width:  32px;" />
                                    <!-- <i class="el-icon-caret-bottom"></i> -->
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <!-- <el-dropdown-item command="personalCenter">个人中心</el-dropdown-item> -->
                                    <el-tooltip effect="dark" content="默认" placement="right">
                                        <el-dropdown-item command="default">
                                            <svg-icon icon-class="default" style="height: 32px;width:  32px;" />
                                        </el-dropdown-item>
                                    </el-tooltip>
                                    <el-tooltip effect="dark" content="中秋" placement="right">
                                        <el-dropdown-item divided command="zhongqiu">
                                            <svg-icon icon-class="zhongqiu" style="height: 32px;width:  32px;" />
                                        </el-dropdown-item>
                                    </el-tooltip>
                                    <el-tooltip effect="dark" content="国庆" placement="right">
                                        <el-dropdown-item divided command="national">
                                            <svg-icon icon-class="national" style="height: 32px;width:  32px;" />
                                        </el-dropdown-item>
                                    </el-tooltip>
                                    <el-tooltip effect="dark" content="春节" placement="right">
                                        <el-dropdown-item divided command="spring-festival">
                                            <svg-icon icon-class="spring-festival" style="height: 32px;width:  32px;" />
                                        </el-dropdown-item>
                                    </el-tooltip>
                                    <el-tooltip effect="dark" content="清明" placement="right">
                                        <el-dropdown-item divided command="qingming">
                                            <svg-icon icon-class="qingming" style="height: 32px;width:  32px;" />
                                        </el-dropdown-item>
                                    </el-tooltip>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </el-tooltip>
                    </div>
                    <!-- 全屏显示 -->
                    <div class="btn-fullscreen" @click="handleFullScreen" style="line-height: 28px;">
                        <el-tooltip effect="dark" :content="fullscreen ? `取消全屏` : `全屏`" placement="bottom">
                            <i class="iconfont iconquanping1" v-if="!fullscreen"></i>
                            <i class="iconfont iconquxiaoquanping" v-else></i>
                        </el-tooltip>
                    </div>
                    <!-- 国际化 -->
                    <div class="en-ch">
                        <el-tooltip effect="dark" content="国际化" placement="bottom">
                            <!-- <router-link to="/i18n">
                            <i class="iconfont el-icon-s-promotion"></i>
                            </router-link>-->
                            <el-dropdown trigger="click" @command="handleToLanguage">
                                <span class="el-dropdown-link">
                                    <!-- <i class="iconfont el-icon-s-promotion"></i> -->
                                    <svg-icon iconClass="translate" style="height: 22px;width: 22px; margin-right: 5px;" />
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item command="zh">中文</el-dropdown-item>
                                    <el-dropdown-item command="en">英文</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </el-tooltip>
                    </div>
                    <!-- 消息中心 -->
                    <div class="btn-bell">
                        <el-tooltip effect="dark" :content="message ? `有${message}条未读消息` : `消息中心`" placement="bottom">
                            <router-link to="/message">
                                <i class="iconfont iconicon-test2"></i>
                            </router-link>
                        </el-tooltip>
                        <span class="btn-bell-badge" v-if="message">{{ message > '99' ? '99+' : message }}</span>
                    </div>
                    <!-- 用户头像 -->
                    <div class="user-avator">
                        <img v-if="headImg" :src="headImg" />
                        <img v-else src="../../assets/img/headImg.png" alt="">
                    </div>
                    <!-- 用户名下拉菜单 -->
                    <el-dropdown class="user-name" @command="handleCommand">
                        <span class="el-dropdown-link" id="home-username">
                            {{ username }}
                            <i class="el-icon-caret-bottom"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="personalCenter" id="home-personalCenter">个人中心</el-dropdown-item>
                            <el-dropdown-item command="setPassword" id="home-setPassword">修改密码</el-dropdown-item>
                            <el-dropdown-item divided command="loginout" id="home-loginout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                    <!-- <el-dropdown class="user-dropdown" @visible-change="addCla">
                        <span class="el-dropdown-link user-avator" id="home-username">
                            <img v-if="headImg" :src="headImg" />
                            <img v-else src="../../assets/img/headImg.png" alt="">
                            <span style="float:right;line-height: 40px;margin-left: 8px;">{{username}}</span>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                            <ul class="dropdown-box">
                                <li @click="personal" id="home-personalCenter">
                                    <i class="el-icon-user"></i>
                                    <span>个人中心</span>
                                </li>
                                <li @click="dialogVisible = true" id="home-setPassword">
                                    <i class="iconfont icon31"></i>
                                    <span>修改密码</span>
                                </li>
                                <li @click="logOut" id="home-loginout">
                                    <i class="el-icon-switch-button"></i>
                                    <span>退出登录</span>
                                </li>
                            </ul>
                        </el-dropdown-menu>
                    </el-dropdown> -->
                </div>
            </div>
            <!-- 修改密码的dialog -->
            <el-dialog title="修改密码" :visible.sync="dialogVisible" width="30%" :before-close="handleClose" :close-on-click-modal="false">
                <el-form :model="pwdForm" :rules="rules" ref="pwdForm" label-width="100px" class="demo-ruleForm">
                    <el-form-item label="旧密码" prop="oldPwd">
                        <el-input :type="pwdType1" v-model="pwdForm.oldPwd" placeholder="请输入旧密码" id="home-oldPwd">
                            <i slot="suffix" class="el-input__icon el-icon-view" :class="{ pwd: isPwd1 }" @click="handleToPwd1"></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPwd">
                        <el-input :type="pwdType2" v-model="pwdForm.newPwd" placeholder="请输入新密码" id="home-newPwd">
                            <i slot="suffix" class="el-input__icon el-icon-view" :class="{ pwd: isPwd2 }" @click="handleToPwd2"></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item label="确认新密码" prop="confirmNewPwd">
                        <el-input :type="pwdType3" v-model="pwdForm.confirmNewPwd" placeholder="请再次输入新密码" id="home-confirmNewPwd">
                            <i slot="suffix" class="el-input__icon el-icon-view" :class="{ pwd: isPwd3 }" @click="handleToPwd3"></i>
                        </el-input>
                    </el-form-item>
                </el-form>
                <div class="dialog-footer">
                    <el-button type="text" @click="handleClose" id="home-cancel">取 消</el-button>
                    <el-button type="primary" @click="changePwd" id="home-save">确 定</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>
<script>
import bus from './bus';
import { toggleClass } from '@/utils';
const version = require('element-ui/package.json').version; // element-ui version from node_modules
const ORIGINAL_THEME = '#409EFF'; // default color (blue)
export default {
    data() {
        var checkPwd = (rule, value, callback) => {
            let data = {};
            data.oldPassword = this.pwdForm.oldPwd;
            this.$axios.post('sys/user/oldPasswordIsRight', data).then((res) => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.pwdForm.oldPwd = '';
                } else {
                    callback();
                }
            });
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
            ThemeData: 'default',
            collapse: false,
            fullscreen: false,
            name: '🍦',
            // 头像
            headImg: '',
            message: 0,
            // 头部标题
            title: sessionStorage.getItem('pageTitle'),
            // 当前登录用户id
            userId: sessionStorage.getItem('userId'),
            // 控制修改密码对话框的显示与隐藏
            dialogVisible: false,
            // 修改密码的表单
            pwdForm: {
                // 旧密码
                oldPwd: '',
                // 新密码
                newPwd: '',
                // 确认新密码
                confirmNewPwd: ''
            },
            // 修改密码的验证规则
            rules: {
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
            // 控制颜色选择器的显示与隐藏
            chalk: '', // 当前是否已经获取过css样式文件内容，如果获取过，这里会有值，避免多次获取
            showColorPicker: true,
            color: '#409EFF',
            predefineColors: ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'],
            // 修改密码文本框的type
            pwdType1: 'password',
            isPwd1: false,
            pwdType2: 'password',
            isPwd2: false,
            pwdType3: 'password',
            isPwd3: false,
            timer: null
        };
    },
    computed: {
        username() {
            let username = sessionStorage.getItem('userName');
            return username ? username : this.name;
        }
    },
    created() {
        bus.$on('getTarget', (target) => {
            this.getUserInfo();
        });
        bus.$on('getMessageNumber', (target) => {
            this.getMessageNumber();
        });
        this.getUserInfo();
        this.ThemeData =
            sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null'
                ? 'default'
                : sessionStorage.getItem('Theme');
        // 定时刷新消息接口 1次/分
        this.timer = setInterval(() => {
            this.getMessageNumber();
        }, 60000);

        if (this.ThemeData == 'zhongqiu') {
            this.color = '#FFA342';
            this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
        } else if (this.ThemeData == 'national') {
            this.color = '#f5222d';
            this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
        } else if (this.ThemeData == 'spring-festival') {
            this.color = '#DC0A00';
            this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
        } else if (this.ThemeData == 'qingming') {
            this.color = '#005A56';
            this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
        } else if (this.ThemeData == 'default') {
            this.color = '#409EFF';
            this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
        }
    },
    watch: {
        themeModel(val) {
            toggleClass(document.body, 'theme-summer');
            if (val === 1) {
                this.showColorPicker = true;
            } else {
                this.showColorPicker = false;
            }
        },
        async color(val) {
            // 如果存在chalk，oldVal就是当前颜色(颜色选择器中选取的就是新颜色)
            // 否则，oldVal就是element-uidefault的蓝色
            const oldVal = this.chalk ? this.color : ORIGINAL_THEME;
            if (typeof val !== 'string') return;

            const themeCluster = this.getThemeCluster(val.replace('#', ''));
            const originalCluster = this.getThemeCluster(oldVal.replace('#', ''));

            const getHandler = (variable, id) => {
                return () => {
                    const originalCluster = this.getThemeCluster(ORIGINAL_THEME.replace('#', ''));
                    const newStyle = this.updateStyle(this[variable], originalCluster, themeCluster);

                    let styleTag = document.getElementById(id);
                    if (!styleTag) {
                        styleTag = document.createElement('style');
                        styleTag.setAttribute('id', id);
                        document.head.appendChild(styleTag);
                    }
                    styleTag.innerText = newStyle;
                };
            };

            // 如果没有chalk就是第一次换颜色，需要远程获取css文件
            // 后面的换色，就不用再次远程获取了
            if (!this.chalk) {
                const url = `https://unpkg.com/element-ui@${version}/lib/theme-chalk/index.css`;
                await this.getCSSString(url, 'chalk');
            }

            const chalkHandler = getHandler('chalk', 'chalk-style');
            chalkHandler();

            // 过滤当前整个页面的样式文件，找到含有oldVal颜色的样式文件
            const styles = [].slice.call(document.querySelectorAll('style')).filter((style) => {
                const text = style.innerText;
                return new RegExp(oldVal, 'i').test(text) && !/Chalk Variables/.test(text);
            });
            // 然后，将其中oldVal的颜色，全部换成我们颜色选择器中选择的新的颜色
            styles.forEach((style) => {
                const { innerText } = style;
                if (typeof innerText !== 'string') return;
                style.innerText = this.updateStyle(innerText, originalCluster, themeCluster);
            });
        }
    },
    methods: {
        // 当dropdown显示是给body添加类名 防止样式全局污染
        addCla(bl) {
            var timer = null;
            if (bl) {
                document.getElementsByTagName('body')[0].className = 'current-body'; //弹出添加class
            } else {
                clearTimeout(timer);
                timer = setTimeout(() => {
                    document.body.removeAttribute('class', 'current-body'); //隐藏移除class
                }, 100);
            }
        },
        colorChage(e) {
            // console.log(e);
        },
        colorActive(e) {
            // console.log(e);
        },
        handleTheme(e) {
            this.ThemeData = e;
            bus.$emit('Theme', e);
            sessionStorage.setItem('Theme', e);
            this.ThemeData =
                sessionStorage.getItem('Theme') == null || sessionStorage.getItem('Theme') == 'null'
                    ? 'default'
                    : sessionStorage.getItem('Theme');
            // sessionStorage.setItem('Theme');
            if (this.ThemeData == 'zhongqiu') {
                this.color = '#FFA342';

                this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#FFA342'];
            } else if (this.ThemeData == 'national') {
                this.color = '#f5222d';
                this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#f5222d'];
            } else if (this.ThemeData == 'spring-festival') {
                this.color = '#DC0A00';
                this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#DC0A00'];
            } else if (this.ThemeData == 'qingming') {
                this.color = '#005A56';
                this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#005A56'];
            } else if (this.ThemeData == 'default') {
                this.color = '#409EFF';
                this.predefineColors = ['#409EFF', '#1890ff', '#304156', '#212121', '#11a983', '#13c2c2', '#6959CD', '#409EFF'];
            }
        },
        // 用户名下拉菜单选择事件
        handleCommand(command) {
            if (command == 'loginout') {
                sessionStorage.clear();
                sessionStorage.setItem('Theme', this.ThemeData);
                this.$router.push('/login');
                setTimeout(() => {
                    window.location.reload();
                }, 100);
                // this.$store.commit('setRightList', []);
            } else if (command == 'personalCenter') {
                this.$router.push('/personalInfo');
            } else if (command == 'setPassword') {
                this.dialogVisible = true;
            }
        },
        // 个人中心
        personal() {
            this.$router.push('/personalInfo');
        },
        // 登出
        logOut() {
            sessionStorage.clear();
            sessionStorage.setItem('Theme', this.ThemeData);
            this.$store.commit('removeData', null);
            // this.$router.push('/login');
            document.body.removeAttribute('class', 'current-body'); //隐藏移除class
        },
        // 侧边栏折叠
        collapseChage() {
            this.collapse = !this.collapse;
            bus.$emit('collapse', this.collapse);
        },
        // 全屏事件
        handleFullScreen() {
            let element = document.documentElement;
            if (this.fullscreen) {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitCancelFullScreen) {
                    document.webkitCancelFullScreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            } else {
                if (element.requestFullscreen) {
                    element.requestFullscreen();
                } else if (element.webkitRequestFullScreen) {
                    element.webkitRequestFullScreen();
                } else if (element.mozRequestFullScreen) {
                    element.mozRequestFullScreen();
                } else if (element.msRequestFullscreen) {
                    // IE11
                    element.msRequestFullscreen();
                }
            }
            this.fullscreen = !this.fullscreen;
        },

        // 修改密码
        changePwd() {
            this.$refs.pwdForm.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('sys/user/changePwd', {
                        password: this.pwdForm.newPwd
                    });
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.isPwd1 = false;
                        this.pwdType1 = 'password';
                        this.isPwd2 = false;
                        this.pwdType2 = 'password';
                        this.isPwd3 = false;
                        this.pwdType3 = 'password';
                        this.handleClose();
                        sessionStorage.clear();
                        this.$router.push('/login');
                    } else {
                        this.$message.error(`${res.data.data}`);
                        this.handleClose();
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        // dialog关闭事件
        handleClose() {
            this.dialogVisible = false;
            this.isPwd1 = false;
            this.pwdType1 = 'password';
            this.isPwd2 = false;
            this.pwdType2 = 'password';
            this.isPwd3 = false;
            this.pwdType3 = 'password';
            this.$refs.pwdForm.resetFields();
        },
        // 获取消息数量
        async getMessageNumber() {
            const res = await this.$axios.post('sys/message/getMessageNumber');
            if (res.data.code == 0) {
                this.message = res.data.data.unreadedNum;
            }
        },
        /* 更新样式 - 使用新的颜色变量替换之前的 */
        updateStyle(style, oldCluster, newCluster) {
            let newStyle = style;
            oldCluster.forEach((color, index) => {
                newStyle = newStyle.replace(new RegExp(color, 'ig'), newCluster[index]);
            });
            return newStyle;
        },
        // 创建xhr，远程获取css文件，并给chalk赋值
        getCSSString(url, variable) {
            return new Promise((resolve) => {
                const xhr = new XMLHttpRequest();
                xhr.onreadystatechange = () => {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        this[variable] = xhr.responseText.replace(/@font-face{[^}]+}/, '');
                        resolve();
                    }
                };
                xhr.open('GET', url);
                xhr.send();
            });
        },
        /**
         * 传入一个颜色的HEX，得到这个颜色的深浅颜色数组
         * 我们知道，我们default的主色调蓝色，在实际使用中，还需要对应的浅蓝和深蓝
         * @param  {[string]]} theme [color]
         * @return {[array]}       [对应的深浅颜色数组]
         */
        getThemeCluster(theme) {
            const tintColor = (color, tint) => {
                let red = parseInt(color.slice(0, 2), 16);
                let green = parseInt(color.slice(2, 4), 16);
                let blue = parseInt(color.slice(4, 6), 16);

                if (tint === 0) {
                    // when primary color is in its rgb space
                    return [red, green, blue].join(',');
                } else {
                    red += Math.round(tint * (255 - red));
                    green += Math.round(tint * (255 - green));
                    blue += Math.round(tint * (255 - blue));

                    red = red.toString(16);
                    green = green.toString(16);
                    blue = blue.toString(16);

                    return `#${red}${green}${blue}`;
                }
            };

            const shadeColor = (color, shade) => {
                let red = parseInt(color.slice(0, 2), 16);
                let green = parseInt(color.slice(2, 4), 16);
                let blue = parseInt(color.slice(4, 6), 16);

                red = Math.round((1 - shade) * red);
                green = Math.round((1 - shade) * green);
                blue = Math.round((1 - shade) * blue);

                red = red.toString(16);
                green = green.toString(16);
                blue = blue.toString(16);

                return `#${red}${green}${blue}`;
            };

            const clusters = [theme];
            for (let i = 0; i <= 9; i++) {
                clusters.push(tintColor(theme, Number((i / 10).toFixed(2))));
            }
            clusters.push(shadeColor(theme, 0.1));
            return clusters;
        },
        // 切换语言
        handleToLanguage(data) {
            this.$i18n.locale = data;
        },
        // 显示隐藏密码
        handleToPwd1() {
            this.isPwd1 = !this.isPwd1;
            if (this.isPwd1) {
                this.pwdType1 = 'text';
            } else {
                this.pwdType1 = 'password';
            }
        },
        handleToPwd2() {
            this.isPwd2 = !this.isPwd2;
            if (this.isPwd2) {
                this.pwdType2 = 'text';
            } else {
                this.pwdType2 = 'password';
            }
        },
        handleToPwd3() {
            this.isPwd3 = !this.isPwd3;
            if (this.isPwd3) {
                this.pwdType3 = 'text';
            } else {
                this.pwdType3 = 'password';
            }
        },
        async getUserInfo() {
            const res = await this.$axios.post('/sys/user/getUserInfoSelf', {});
            if (res.data.code == 0) {
                this.headImg = res.data.data.headImg;
            }
        }
    },
    mounted() {
        if (document.body.clientWidth < 1500) {
            this.collapseChage();
        }
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer); // 在Vue实例销毁前清除定时器
        }
    }
};
</script>
<style lang="less" scoped>
.header {
    display: flex;
    justify-content: space-between;
    padding-right: 40px;
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 70px;
    font-size: 22px;
    color: #fff;
    background-size: cover;
}

.collapseZhong {
    color: rgb(153, 153, 153) !important;
    background: rgb(11, 35, 56) !important;
}

.collapseGuo {
    color: rgb(102, 102, 102) !important;
    background: rgb(255, 234, 234) !important;
}

.collapse {
    background: rgb(25, 32, 45) !important;
    color: #fff !important;
}

.collapseQing {
    color: #fff !important;
    background: #7cbbbd !important;
}

.collapseChun {
    color: rgb(255, 255, 255) !important;
    background: rgb(110, 0, 10) !important;
}

.headerGuo {
    background: url('../../assets/img/dingbu@2x.png') no-repeat 195px center;
    background-size: cover;
}

.headerZhong {
    background: url('../../assets/img/ding@2x.png') no-repeat 195px center;
    background-size: cover;
}

.headerQing {
    background: url('../../assets/img/dingbuQing@2x.png') no-repeat 195px center;
    background-size: cover;
}

.headerChun {
    background: url('../../assets/img/dingbuChun@2x.png') no-repeat 195px center;
    background-size: cover;
}

.collapse-btn {
    position: absolute;
    height: 50px;
    line-height: 50px;
    background: #fff;
    width: 18px;
    top: 120px;
    left: 64px;
    cursor: pointer;
    z-index: 2000;
    -webkit-appearance: none;
    background-color: #fff;
    background-image: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    border: 1px solid #dcdfe6;
    border-left: 0;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    color: #606266;
    display: inline-block;
    font-size: inherit;
    transition: 0.3s left ease-in-out;
}

.collapse-btn-small {
    position: absolute;
    height: 50px;
    line-height: 50px;
    background: #fff;
    width: 18px;
    text-align: center;
    top: 120px;

    left: 200px;
    cursor: pointer;
    z-index: 2000;
    -webkit-appearance: none;
    background-color: #fff;
    background-image: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    border: 1px solid #dcdfe6;
    border-left: 0;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    color: #606266;
    display: inline-block;
    font-size: inherit;
    transition: 0.3s left ease-in-out;
}

.logoDataImg {
    float: left;
    /* width: 250px; */
    // width: 200px;
    line-height: 70px;
    /* text-indent: 2.5em; */
    /* background: url('../../assets/logo.png') no-repeat left; */
    background-size: 40px 40px;
    text-align: center;
    padding: 0 40px;
    font-size: 18px;
    height: 70px;
}

.logoGuo {
    background: url('../../assets/img/logo@2x.png') no-repeat;
}

.logoZhong {
    background: url('../../assets/img/logoz@2x.png') no-repeat;
}

.logoChun {
    background: url('../../assets/img/logoChun@2x.png') no-repeat;
}

.logoQing {
    background: url('../../assets/img/logoQing@2x.png') no-repeat;
}

.header-right {
    float: right;
    // padding-right: 50px;
}

.header-user-con {
    display: flex;
    height: 70px;
    align-items: center;
}

.btn-fullscreen {
    /* transform: rotate(45deg); */
    margin-right: 5px;
    font-size: 24px;
}

.btn-bell,
.btn-fullscreen {
    position: relative;
    width: 30px;
    height: 30px;
    text-align: center;
    border-radius: 15px;
    cursor: pointer;
}

.btn-bell-badge {
    position: absolute;
    right: 0;
    top: -2px;
    width: 14px;
    height: 14px;
    border-radius: 7px;
    background: #f56c6c;
    font-size: 12px;
    color: #fff;
}

.btn-bell .el-icon-bell {
    color: #fff;
}

.user-name {
    margin-left: 10px;
}

.user-avator {
    display: block;
    // margin-left: 20px;
}

.user-avator img {
    display: inline-block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.el-dropdown-link {
    color: #fff;
    cursor: pointer;
}

.el-dropdown-menu__item {
    text-align: center;
}

.iconquanping1,
.iconquxiaoquanping {
    font-size: 24px;
}

.iconicon-test2 {
    font-size: 24px;
    color: #fff;
}

.theme {
    margin-right: 2px;
}

.el-icon-s-open {
    font-size: 24px;
    color: #fff;
}

.el-icon-s-promotion {
    font-size: 24px;
    color: #fff;
    margin-right: 8px;
}

.dialog-footer {
    text-align: right;
}

.el-icon-view {
    cursor: pointer;
}

.el-icon-view {
    cursor: pointer;
}

.pwd {
    color: #409eff;
}
.user-dropdown {
    margin-left: 10px;
}

.current-body {
    // 个人信息样式
    .el-dropdown-menu {
        // margin: 0 !important;
        padding: 0 !important;
        width: 126px;
        right: 0;
        border-color: transparent;
        border: none;
        background-color: transparent;
        .user-info {
            box-sizing: border-box;
            display: flex;
            align-items: center;
            padding: 35px 20px 25px 30px;
            background: url('../../assets/img/login_bg.png') center no-repeat;
            background-size: 100% 100%;
            border-top-left-radius: 5px;
            border-top-right-radius: 5px;
            // transform: translateY(-6px);
            z-index: 100;
            position: relative;
            .head-img {
                width: 80px;
                height: 80px;
                border-radius: 50%;
                border: 2px solid #fff;
                overflow: hidden;
                img {
                    display: block;
                    width: 100%;
                    height: 100%;
                }
            }
            .text {
                margin-left: 20px;
                color: #fff;
            }
            .triangle {
                position: absolute;
                top: -8px;
                right: 10px;
                z-index: 9999;
            }
        }
        .dropdown-box {
            padding-left: 16px;
            padding-top: 10px;
            overflow: hidden;
            background-color: #fff;
            border-bottom-left-radius: 5px;
            border-bottom-right-radius: 5px;
            // transform: translateY(-6px);
            li {
                position: relative;
                box-sizing: border-box;
                padding-bottom: 3px;
                // margin: 10px 0;
                width: 90px;
                margin-bottom: 10px;
                cursor: pointer;
                i {
                    margin-right: 10px;
                }
            }
            li::before {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                height: 2px;
                background-color: #409efe;
                transform-origin: bottom right;
                transform: scaleX(0);
                transition: transform 0.5s ease;
            }

            li:hover::before {
                transform-origin: bottom left;
                transform: scaleX(1);
            }
        }
    }
}
</style>
