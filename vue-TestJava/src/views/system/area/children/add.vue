<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 新增区域的表单
 * @Version: 1.0
 -->
<template>
  <!-- 新增表格数据的dialog -->
  <el-dialog title="新增区域" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="600" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item label="上级">
        <el-input v-model.number="parentOrgName" readonly></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="areaName">
        <el-input v-model.trim="addForm.areaName" id="system-area-add-areaName" placeholder="请输入区域名称" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="编码" prop="areaCode">
        <el-input v-model.trim="addForm.areaCode" id="system-area-add-areaCode" placeholder="请输入区域编码" maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <div class="dialog-footer">
        <el-button id="system-area-add-cancel" type="text" @click="handleToClose">取 消</el-button>
        <el-button id="system-area-add-save" type="primary" @click="handleToSave">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
    data() {
        var checkCode = (rule, value, callback) => {
            let data = {};
            data.areaCode = this.addForm.areaCode;
            data.id = '';
            this.$axios.post('sys/area/checkAreaCode', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.areaCode = '';
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
                // 区域编码
                areaCode: '',
                // 备注
                // remark: '',
                //区域名称
                areaName: '',
                // 上级区域编码
                parentCode: ''
                // 父节点ID
                // parentId: -1,
                // 顺序
                // sort: ''
            },
            // 验证规则
            rules: {
                areaName: [
                    { required: true, message: '请输入区域名称', trigger: 'blur' }
                    // { validator: checkName, trigger: 'blur' }
                ],
                areaCode: [
                    { required: true, message: '请输入区域编码', trigger: 'blur' },
                    { validator: checkCode, trigger: 'blur' }
                ]
                // sort: [{ required: true, message: '请输入顺序', trigger: 'blur' }]
            },
            // 父节点名称
            parentOrgName: '中华人民共和国'
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
        openDialog(data) {
            this.addForm.parentCode = data.parentCode;
            this.addDialogVisible = true;
            // 判断有没有父节点 如果没有 默认父节点为根节点
            JSON.stringify(data) == '{}' ? (this.addForm.parentCode = '0') : (this.addForm.parentCode = data.areaCode);
            JSON.stringify(data) == '{}' ? (this.parentOrgName = '中华人民共和国') : (this.parentOrgName = data.areaName);
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮提交表单
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToSave() {
            this.$refs.addForm.validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('sys/area/save', this.addForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 添加成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
                        this.$emit('getTreeData');
                        // if (this.addForm.parentId == 1) {
                        //     this.$emit('getTreeData');
                        // }
                        // this.$emit('getTreeData');
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
                        this.handleToClose();
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
            // 重置表单
            this.$refs.addForm.resetFields();
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
