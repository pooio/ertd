<template>
    <div class="login-wrap">
        <div class="ms-forgotPwd">
            <el-steps :active="active" align-center>
                <el-step title="填写登录名" description></el-step>
                <el-step title="设置新密码" description></el-step>
                <el-step title="完成" description></el-step>
            </el-steps>
            <!-- 填写账户 -->
            <div v-if="active =='0'">
                <el-form
                    :model="param"
                    :rules="rulesAccount"
                    ref="accountOne"
                    key="form1"
                    label-width="0px"
                    class="ms-content"
                >
                    <el-form-item prop="username" style="width:350px">
                        <el-input v-model="param.username" placeholder="请输入登录名">
                            <el-button slot="prepend" icon="el-icon-user"></el-button>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop>
                        <div class="login-btn">
                            <el-button v-show="isShowTime" type="primary" @click="nextOne()">下一步</el-button>
                            <el-button v-show="!isShowTime" type="info" plain disabled>{{countDownNum}} S</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
            <!-- 设置新密码 -->
            <div v-if="active =='1'">
                <el-form
                    :model="pwdParam"
                    :rules="rulesPwd"
                    ref="pwdTwo"
                    key="form2"
                    label-width="0px"
                    class="ms-content"
                >
                    <el-form-item prop="newPwd" style="width:350px">
                        <el-input
                            :type="pwdTypeOne"
                            v-model="pwdParam.newPwd"
                            placeholder="请输入新密码"
                            id="pwd1"
                        >
                            <el-button slot="prepend" icon="el-icon-lock"></el-button>
                            <i
                                slot="suffix"
                                class="el-input__icon el-icon-view"
                                :class="{ pwd: isPwdOne }"
                                @click="handleNewPwd"
                            ></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop="againPwd" style="width:350px">
                        <el-input
                            :type="pwdTypeTwo"
                            v-model="pwdParam.againPwd"
                            placeholder="请再次输入新密码"
                            id="pwd2"
                        >
                            <el-button slot="prepend" icon="el-icon-lock"></el-button>
                            <i
                                slot="suffix"
                                class="el-input__icon el-icon-view"
                                :class="{ pwd: isPwdTwo }"
                                @click="handleAgainPwd"
                            ></i>
                        </el-input>
                    </el-form-item>
                    <el-form-item prop>
                        <div class="login-btn">
                            <el-button type="primary" @click="nextTwo()">下一步</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
            <!-- 完成 -->
            <div v-if="active==2">
                <div class="ms-content">
                    密码已重置成功！
                    <span @click="nextThree()" class="handleToLogin handleToLoginPage">点击跳转登录页面</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import CONST from '@/constants'
export default {
  data () {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入新密码'));
      } else if (value !== this.pwdParam.newPwd) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      active: 0,
      // 第一步-账户信息参数
      param: {
        username: "",
      },
      // 第二步-设置新密码参数
      pwdParam: {
        newPwd: "",// 新密码
        againPwd: "",//再次重复密码
      },
      // 第一步-校验账户
      rulesAccount: {
        username: [{ required: true, message: '请输入登录名', trigger: 'blur' }],
      },
      // 第二步-校验密码
      rulesPwd: {
        newPwd: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
        ],
        againPwd: [
          { required: true, trigger: 'blur', validator: validatePass },
          { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
        ],
      },
      // 密码类型
      pwdTypeOne: 'password',
      pwdTypeTwo: 'password',
      isPwdOne: false,
      isPwdTwo: false,
      countDownNum:10,//60秒倒计时
      timer:null,
      isShowTime:true,
    }
  },
  mounted(){
      //邮箱地址跳转找回密码的设置新密码位置
      var urlKey  = this.$route.query.key;
      if(urlKey != '' && urlKey != null && urlKey != undefined){
        this.active = 1;
      }
  },
  methods: {
    // 第一步-下一步按钮
    nextOne () { 
      this.$refs.accountOne.validate(valid => {
        if (valid) {
          this.$axios
            .post('sys/user/sendRecoverEmail', {
              loginName: this.param.username,
              ipAddress: CONST.URL +'/#'+ this.$route.path
            })
            .then(res => {
              if (res.data.code == 0) {
                var tips = res.data.data;
                var newTips = tips.split('@');
                var tipHead = newTips[0].slice(0,4);
                this.$message("您应尽快前往"+tipHead+"****@"+newTips[1]+"重设您的密码！");
                this.active = 0;
                this.countDown();
              } else {
                this.$message.error(`${res.data.data}`);
              }
            });
        } else {
          this.$message.error('请核对账户信息');
          return false;
        }
      })

    },
    // 第二步-设置新密码下一步按钮
    nextTwo () {
      this.$refs.pwdTwo.validate(valid => {
        if (valid) {
            this.$axios
            .post('sys/user/recoverPassword', {
                key:this.$route.query.key,
                oldPassword :this.pwdParam.newPwd,
                newPassword  :this.pwdParam.againPwd,
            })
            .then(res => {
              if (res.data.code == 0) {
                this.active = 2;
              } else {
                this.$message.error(`${res.data.data}`);
              }
            });
        } else {
          this.$message.error('请核对密码');
          return false;
        }
      })
    },
    // 新密码-查看
    handleNewPwd () {
      this.isPwdOne = !this.isPwdOne;
      if (this.isPwdOne) {
        this.pwdTypeOne = 'text';
      } else {
        this.pwdTypeOne = 'password';
      }
    },
    // 确认密码-查看
    handleAgainPwd () {
      this.isPwdTwo = !this.isPwdTwo;
      if (this.isPwdTwo) {
        this.pwdTypeTwo = 'text';
      } else {
        this.pwdTypeTwo = 'password';
      }
    },
    // 第三步-确认按钮
    nextThree () {
      this.active = 3;
      this.$router.push('/Login');
    },
    //60秒倒计时
    countDown:function(){
      this.countDownNum = 60;
      this.isShowTime = false;
          this.timer=setInterval(() => {
             this.countDownNum--;
                if(this.countDownNum<=0){
                    clearInterval(this.timer);
                     this.isShowTime = !this.isShowTime;
                }
          },1000);
      }
  }
}
</script>
<style lang="less" scoped>
@import '../../assets/css/style.css';
.ms-content {
    padding: 30px 30px;
    text-align: center;
    margin: 0 190px;
}
.handleToLoginPage{
  color: #409eff;
}
.pwd {
    color: #409eff;
}
</style>