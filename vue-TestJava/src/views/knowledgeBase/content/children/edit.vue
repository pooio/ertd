<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-15 14:50:12
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:45:35
 * @Description: 修改知识库内容的表单
 * @Version: 1.0
 -->
<template>
  <el-dialog title="修改内容" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="70%" :before-close="handleToClose">
    <el-form ref="editForm" :model="editForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="标题" prop="kbName">
        <el-input v-model.trim="editForm.kbName" placeholder="请输入标题" maxlength="20" show-word-limit id="kb-content-edit-kbName"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="kbTypePk">
        <el-select v-model="editForm.kbTypePk" placeholder="请选择类型" style="width: 100%;" id="kb-content-edit-kbTypePk">
          <el-option v-for="item in selectData" :key="item.id" :label="item.dicName" :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="关键字" prop="keyWord">
        <el-input v-model.trim="editForm.keyWord" placeholder="请输入关键字" maxlength="10" show-word-limit id="kb-content-edit-keyWord"></el-input>
      </el-form-item>
      <el-form-item label="内容" prop="kbContent">
        <editor id="tinymce1" v-model.trim="editForm.kbContent" :init="richTextInit"></editor>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button type="text" @click="handleToClose" id="kb-content-edit-cancel">取 消</el-button>
      <el-button type="primary" @click="handleToUpdata" id="kb-content-edit-save">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
// 导入富文本编辑器
import tinymce from 'tinymce/tinymce';
import Editor from '@tinymce/tinymce-vue';
// import 'tinymce/icons/default/icons';
import 'tinymce/themes/silver';
import 'tinymce/plugins/image'; // 插入上传图片插件
import 'tinymce/plugins/media'; // 插入视频插件
import 'tinymce/plugins/table'; // 插入表格插件
import 'tinymce/plugins/lists'; // 列表插件
import 'tinymce/plugins/wordcount'; // 字数统计插件
import 'tinymce/icons/default/icons';
export default {
    // 注册富文本组件
    components: { Editor},
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.kbDirGbcode = this.editForm.KbDirGbcode;
            data.kbDirName = this.editForm.KbDirName;
            data.id = this.editForm.id;
            this.$axios.post('kb/dir/checkDirName', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.addForm.dicName = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 控制编辑表单对话框的显示隐藏
            editDialogVisible: false,
            // 编辑表单的信息
            editForm: {
                // id
                kbPk: '',
                // 标题
                kbName: '',
                // 目录编码
                kbDirCode: '',
                // 知识库类型
                kbTypePk: '',
                // 关键字
                keyWord: '',
                // 内容
                kbContent: '',
                // 关联知识点
                kbLinkCode: ''
            },
            // 编辑表单的验证规则
            editFormRules: {
                kbName: [
                    { required: true, message: '请输入标题', trigger: 'blur' }
                    // { validator: checkDicinfoName, trigger: 'blur' }
                ],
                kbTypePk: [{ required: true, message: '请选择类型', trigger: 'blur' }],
                keyWord: [{ required: true, message: '请输入关键字', trigger: 'blur' }],
                kbContent: [{ required: true, message: '请输入内容', trigger: 'blur' }]
            },
            // 选择器绑定的数据
            selectData: [],
            // 富文本配置项
            richTextInit: {
                language_url: '/tinymce/langs/zh_CN.js', // 语言包的路径
                language: 'zh_CN', //语言
                skin_url: '/tinymce/skins/ui/oxide', // skin路径
                height: 300, //编辑器高度
                branding: false, //是否禁用“Powered by TinyMCE”
                menubar: false, //顶部菜单栏显示
                plugins: 'lists image media table wordcount',
                toolbar:
                    'undo redo |  formatselect | bold italic forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | lists image media table | removeformat'
            }
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
        openDialog(row, selectData) {
            this.selectData = selectData;
            this.editForm.kbPk = row.kbPk;
            this.editForm.kbDirCode = row.kbDirCode;
            this.editForm.kbName = row.kbName;
            this.editForm.kbTypePk = (row.kbTypePk - 0) == 0 ? null : (row.kbTypePk - 0);
            this.editForm.keyWord = row.keyWord;
            this.editForm.kbContent = row.kbContent;
            this.editForm.kbLinkCode = '';
            this.editDialogVisible = true;
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
            this.$refs.editForm.validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('kb/content/doUpdate', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        // 修改成功后通过$emit触发父元素的获取数据列表的方法
                        this.$emit('getTableData');
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
</style>