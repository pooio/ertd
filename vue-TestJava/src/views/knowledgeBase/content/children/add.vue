<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 新增知识库内容的表单
 * @Version: 1.0
 -->
<template>
  <!-- 新增表格数据的dialog -->
  <el-dialog title="新增内容" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="70%" :before-close="handleToClose">
    <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px">
      <el-form-item label="标题" prop="kbName">
        <el-input v-model.trim="addForm.kbName" placeholder="请输入标题" maxlength="20" show-word-limit id="kb-content-add-kbName"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="kbTypePk">
        <el-select v-model="addForm.kbTypePk" placeholder="请选择类型" style="width: 100%;" id="kb-content-add-kbTypePk">
          <el-option :label="item.dicName" :value="item.id" v-for="item in selectData" :key="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="关键字" prop="keyWord">
        <el-input v-model.trim="addForm.keyWord" placeholder="请输入关键字" maxlength="10" show-word-limit id="kb-content-add-keyWord"></el-input>
      </el-form-item>
      <el-form-item label="内容" prop="kbContent">
        <editor id="tinymce" v-model.trim="addForm.kbContent" :init="richTextInit" class="kb-content-add-kbContent"></editor>
      </el-form-item>
      <div class="dialog-footer">
        <el-button type="text" @click="handleToClose" id="kb-content-add-cancel">取 消</el-button>
        <el-button type="primary" @click="handleToSave" id="kb-content-add-save">确 定</el-button>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
// 导入富文本编辑器
import tinymce from 'tinymce/tinymce';
import Editor from '@tinymce/tinymce-vue';
import 'tinymce/themes/silver';
import 'tinymce/plugins/image'; // 插入上传图片插件
import 'tinymce/plugins/media'; // 插入视频插件
import 'tinymce/plugins/table'; // 插入表格插件
import 'tinymce/plugins/lists'; // 列表插件
import 'tinymce/plugins/wordcount'; // 字数统计插件
import 'tinymce/icons/default/icons';
export default {
    // 注册富文本组件
    components: { Editor },
    data() {
        var checkDicinfoName = (rule, value, callback) => {
            let data = {};
            data.KbDirGbcode = this.addForm.KbDirGbcode;
            data.KbDirName = this.addForm.KbDirName;
            data.id = '';
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
            // 控制新增dialog的显示与隐藏
            addDialogVisible: false,
            // 新增表单数据
            addForm: {
                // 标题
                kbName: '',
                // 目录编码
                kbDirCode: '',
                // 父节点编码
                // KbDirGbcode: "000000000000",
                // 知识库类型
                kbTypePk: '',
                // 内容
                kbContent: '',
                // 关联知识点
                kbLinkCode: ''
            },
            // 验证规则
            rules: {
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
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 打开dialog的方法
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        openDialog(data, selectData) {
            // JSON.stringify(data) == '{}' ? (this.addForm.kbDirCode = '000000000000') : (this.addForm.kbDirCode = data.kbDirCode);
            if (JSON.stringify(data) == '{}') {
                return this.$message.warning('知识库目录不能为空,请在左侧选择目录');
            }
            this.addForm.kbDirCode = data.kbDirCode
            this.selectData = selectData;
            this.addDialogVisible = true;
            // 判断有没有父节点 如果没有 默认父节点为根节点
            // console.log(data);
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
                    if (this.addForm.kbDirCode == '000000000000') {
                        this.$message.warning('知识库目录不能为空,请在左侧选择目录');
                    } else {
                        const res = await this.$axios.post('kb/content/save', this.addForm);
                        if (res.data.code == 0) {
                            this.$message.success(`${res.data.data}`);
                            // 添加成功后通过$emit触发父元素的获取数据列表的方法
                            this.$emit('getTableData');
                            // this.$emit('getTreeData');
                            // if (this.addForm.KbDirGbcode == '000000000000') {
                            //     this.$emit('getTreeData');
                            // }
                            this.handleToClose();
                        } else {
                            this.$message.error(`${res.data.data}`);
                            this.handleToClose();
                        }
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