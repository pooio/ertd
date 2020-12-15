<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 修改用户的表单
 * @Version: 1.0
 -->
<template>
  <!-- 修改用户的dialog -->
  <el-dialog :title="$t('i18n.editUser')" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="500" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item :label="$t('i18n.Company')">
        <!-- <el-input v-model.number="addForm.orgName" readonly></el-input> -->
        <treeselect v-model.trim="addForm.orgId" id="system-user-edit-orgId" :options="treeData.children" :normalizer="normalizer" placeholder="点击选择菜单,默认为一级菜单" />
      </el-form-item>
      <el-form-item :label="$t('i18n.loginName')" prop="loginName">
        <el-input v-model.trim="addForm.loginName" id="system-user-edit-loginName" :placeholder="$t('i18n.inputLoginName')" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item :label="$t('i18n.name')" prop="name">
            <el-input v-model.trim="addForm.name" id="system-user-edit-name" :placeholder="$t('i18n.inputName')" maxlength="16" show-word-limit></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('i18n.Gender')" prop="sex">
            <el-radio v-model="addForm.sex" id="system-user-edit-1" label="1">男</el-radio>
            <el-radio v-model="addForm.sex" id="system-user-edit-0" label="0">女</el-radio>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item :label="$t('i18n.email')" prop="email">
        <el-input v-model.trim="addForm.email" id="system-user-edit-email" :placeholder="$t('i18n.inputEmail')" maxlength="30" show-word-limit></el-input>
      </el-form-item>
      <el-form-item :label="$t('i18n.tel')" prop="phoneNumber">
        <el-input v-model.trim="addForm.phoneNumber" id="system-user-edit-tel" :placeholder="$t('i18n.inputTel')" maxlength="11" show-word-limit></el-input>
      </el-form-item>
      <el-form-item :label="$t('i18n.jobNumber')" prop="userNo">
        <el-input v-model.trim="addForm.userNo" id="system-user-edit-jobNumber" :placeholder="$t('i18n.inputJobNumber')" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item :label="$t('i18n.role')" prop="roleId">
        <el-select v-model="addForm.roleId" id="system-user-edit-role" multiple :placeholder="$t('i18n.inputRole')" style="width:100%">
          <el-option :label="item.roleName" :value="item.id" v-for="item in selectData" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('i18n.post')" prop="postId">
        <el-select v-model="addForm.postId" id="system-user-edit-post" :placeholder="$t('i18n.inputPost')" style="width:100%">
          <el-option :label="item.post" :value="item.id" v-for="item in postData" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <div class="dialog-footer">
        <el-button id="system-user-edit-cancel" type="text" @click="handleToClose">{{$t('i18n.cancel')}}</el-button>
        <el-button id="system-user-edit-save" type="primary" @click="handleToUpdate">{{$t('i18n.Determine')}}</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect';
import '@riophae/vue-treeselect/dist/vue-treeselect.css';
export default {
    components: { Treeselect },
    data() {
        var checkLoginName = (rule, value, callback) => {
            let data = {};
            data.loginName = this.addForm.loginName;
            data.id = this.addForm.id;
            this.$axios.post('sys/user/checkLoginName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.loginName = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 控制新增dialog的显示与隐藏
            addDialogVisible: false,
            // 新增表单数据
            addForm: {
                // 性别
                sex: '1',
                // 登录名
                loginName: '',
                // 姓名
                name: '',
                // 工号
                userNo: '',
                // 邮箱
                email: '',
                // 父节点id
                orgId: null,
                // 父节点名称
                orgName: '组织机构',
                // 手机号
                phoneNumber: '',
                //角色ID
                roleId: '',
                // 职务id
                postId: '',
                // 当前用户id
                id: ''
            },
            // 验证规则
            rules: {
                name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
                loginName: [
                    { required: true, message: '请输入登录名', trigger: 'blur' },
                    { validator: checkLoginName, message: '登录名重复', trigger: 'blur' }
                ],
                phoneNumber: [
                    { required: false, message: '请输入手机号', trigger: 'blur' },
                    // 自定义正则验证
                    { pattern: /^1(3|4|5|7|8)\d{9}$/, message: '手机号格式有误' }
                ],
                email: [
                    { required: false, message: '请输入邮箱', trigger: 'blur' },
                    // 自定义正则验证
                    { pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message: '邮箱格式有误' }
                ],
                roleId: [{ required: true, message: '请选择角色', trigger: 'blur' }],
                postId: [{ required: true, message: '请选择职务', trigger: 'blur' }]
            },

            // 角色选择器数据
            selectData: [],
            // 职务选择器数组
            postData: [],
            // 组织机构数据
            treeData: []
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
        openDialog(data, selectData, postData, treeData) {
            this.treeData = treeData[0];
            // 判断有没有父节点 如果没有 默认父节点为根节点
            JSON.stringify(data) == '{}' ? (this.addForm.orgId = null) : (this.addForm.orgId = data.orgId);
            // JSON.stringify(data) == '{}' ? (this.addForm.orgName = '组织机构') : (this.addForm.orgName = data.orgName);
            // 表单回填
            this.selectData = selectData;
            this.postData = postData;
            this.addForm.id = data.id;
            this.addForm.loginName = data.loginName;
            this.addForm.name = data.name;
            this.addForm.sex = data.sex + '';
            this.addForm.email = data.email;
            this.addForm.phoneNumber = data.phoneNumber;
            this.addForm.userNo = data.userNo;
            this.addForm.roleId = data.roleCodes;
            this.addForm.postId = data.postId;

            this.addDialogVisible = true;
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToUpdate() {
            this.$refs.addForm.validate(async valid => {
                if (valid) {
                    // 角色数组转为,分割的字符串
                    this.addForm.roleId = this.addForm.roleId.join(',');
                    const res = await this.$axios.post('sys/user/update', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        // this.$emit('getTreeData');
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
                        this.handleToClose();
                    }
                    // console.log(this.addForm);
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
            this.addForm.orgId = null;
            // 重置表单
            this.$nextTick(() => {
                this.$refs.addForm.resetFields();
            });
            // this.addForm = {};
        },
        /**
         * @Author: Misaka.chen
         * @description: 后台返回的数据字段不符合VueTreeselect的要求的，需要进行转换
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        normalizer(node) {
            //去掉children=[]的children属性
            // if (node.children && !node.children.length) {
            if (node.children == null) {
                delete node.children;
            }
            return {
                id: node.id,
                label: node.orgName,
                children: node.children
            };
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
