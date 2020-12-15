<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 新增组织机构的表单
 * @Version: 1.0
 -->
<template>
    <!-- 新增表格数据的dialog -->
    <el-dialog
        title="新增组织机构"
        :visible.sync="addDialogVisible"
        :close-on-click-modal="false"
        width="500"
        :before-close="handleToClose"
    >
        <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
            <el-form-item label="上级">
                <el-input
                    v-model.number="parentOrgName"
                    readonly
                    id="system-department-add-parentOrgName"
                ></el-input>
            </el-form-item>
            <el-form-item label="名称" prop="orgName">
                <el-input
                    v-model.trim="addForm.orgName"
                    placeholder="请输入机构名称"
                    maxlength="6"
                    show-word-limit
                    id="system-department-add-orgName"
                ></el-input>
            </el-form-item>
            <el-form-item label="编码" prop="code">
                <el-input
                    v-model.trim="addForm.code"
                    placeholder="请输入机构编码"
                    maxlength="16"
                    show-word-limit
                    id="system-department-add-code"
                ></el-input>
            </el-form-item>
            <el-form-item label="顺序" prop="sort">
                <el-input-number
                    v-model.number="addForm.sort"
                    :min="0"
                    controls-position="right"
                    style="width: 100%;"
                    id="system-department-add-sort"
                ></el-input-number>
                <!-- <el-input v-model.number="addForm.sortNo" type="number" :min="0" :max="999"></el-input> -->
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input
                    v-model.trim="addForm.remark"
                    placeholder="请输入备注"
                    type="textarea"
                    rows="5"
                    maxlength="100"
                    show-word-limit
                    resize="none"
                    id="system-department-add-remark"
                ></el-input>
            </el-form-item>
            <div class="dialog-footer">
                <el-button type="text" @click="handleToClose" id="system-department-add-cancel">取 消</el-button>
                <el-button type="primary" @click="handleToSave" id="system-department-add-save">确 定</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
export default {
  data () {
    var checkCode = (rule, value, callback) => {
      let data = {};
      data.code = this.addForm.code;
      data.parentId = this.addForm.parentId;
      data.id = '';
      this.$axios.post('sys/org/checkOrgCode', data).then(res => {
        if (res.data.code != 0) {
          callback(new Error(res.data.data));
          this.addForm.code = '';
        } else {
          callback();
        }
      });
    };
    var checkName = (rule, value, callback) => {
      let data = {};
      data.orgName = this.addForm.orgName;
      data.parentId = this.addForm.parentId;
      data.id = '';
      this.$axios.post('sys/org/checkOrgName', data).then(res => {
        if (res.data.code != 0) {
          callback(new Error(res.data.data));
          this.addForm.orgName = '';
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
        // 机构编码
        code: '',
        // 机构描述
        remark: '',
        //机构名称
        orgName: '',
        // 父节点ID
        parentId: -1,
        // 顺序
        sort: ''
      },
      // 验证规则
      rules: {
        orgName: [
          { required: true, message: '请输入机构名称', trigger: 'blur' },
          { validator: checkName, trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入机构编码', trigger: 'blur' },
          { validator: checkCode, trigger: 'blur' }
        ],
        sort: [{ required: true, message: '请输入顺序', trigger: 'blur' }]
      },
      // 父节点名称
      parentOrgName: '组织机构'
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
    openDialog (data) {
      this.addDialogVisible = true;
      // 判断有没有父节点 如果没有 默认父节点为根节点
      JSON.stringify(data) == '{}' ? (this.addForm.parentId = 1) : (this.addForm.parentId = data.id);
      JSON.stringify(data) == '{}' ? (this.parentOrgName = '组织机构') : (this.parentOrgName = data.orgName);
    },
    /**
     * @Author: Misaka.chen
     * @description: 点击按钮提交表单
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    handleToSave () {
      this.$refs.addForm.validate(async valid => {
        if (valid) {
          const res = await this.$axios.post('sys/org/save', this.addForm);
          if (res.data.code == 0) {
            this.$message.success(`${res.data.data}`);
            // 添加成功后通过$emit触发父元素的获取数据列表的方法
            this.$emit('getTableData');
            // if (this.addForm.parentId == 1) {
            //     this.$emit('getTreeData');
            // }
            this.$emit('getTreeData');
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
    handleToClose () {
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
