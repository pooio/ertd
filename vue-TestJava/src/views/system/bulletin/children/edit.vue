<template>
  <el-dialog title="修改公告" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="70%" :before-close="handleToClose">
    <el-form ref="form" :model="form" label-width="80px" :rules="rules">
      <el-form-item label="公告方式" prop="bulletinMode">
        <el-radio-group v-model="form.bulletinMode" @change="handleToChange" disabled>
          <el-radio :label="0" id="system-bulletin-edit-all">全体</el-radio>
          <el-radio :label="1" id="system-bulletin-edit-department">部门</el-radio>
          <el-radio :label="2" id="system-bulletin-edit-personal">个人</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接收对象" prop="receiveInfo">
        <treeselect v-model="receiveInfo" :multiple="true" :options="selectData" :normalizer="normalizer" placeholder="当公告方式为部门或个人时可以选择" :disabled="form.bulletinMode == 0" id="system-bulletin-edit-receiveInfo"/>
      </el-form-item>
      <el-form-item label="公告标题" prop="bulletinTitle">
        <el-input v-model.trim="form.bulletinTitle" placeholder="请输入公告标题" maxlength="20" show-word-limit id="system-bulletin-edit-bulletinTitle"></el-input>
      </el-form-item>
      <el-form-item label="公告类型" prop="bulletinType">
        <el-select v-model.trim="form.bulletinType" placeholder="请选择公告类型" style="width:100%" id="system-bulletin-edit-bulletinType">
          <el-option :label="item.dicName" :value="item.id" v-for="item in select" :key="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="截至日期" prop="closeDate">
        <el-date-picker v-model="form.closeDate" type="date" placeholder="请选择截至日期" value-format="yyyy-MM-dd" style="width:100%" :editable="false" id="system-bulletin-edit-closeDate"></el-date-picker>
      </el-form-item>
      <el-form-item label="公告内容" prop="bulletinNote">
        <editor id="tinymce1" v-model.trim="form.bulletinNote" :init="richTextInit"></editor>
      </el-form-item>
      <el-form-item label="附件">
        <el-input v-model="form.fileName" readonly @focus="handleToSend"></el-input>
      </el-form-item>
      <el-form-item label="附件上传">
        <el-upload class="upload-demo" :action="uploadUrl" :limit="1" :file-list="fileList" :on-change="handleChange" :on-remove="handleToRemove" :http-request="handleHttpRequest">
          <el-button size="small" type="primary" id="system-bulletin-edit-upload">点击上传</el-button>
        </el-upload>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button type="text" @click="handleToClose" id="system-bulletin-edit-cancel">取 消</el-button>
      <el-button type="primary" @click="handleToSave" id="system-bulletin-edit-save">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import Treeselect from '@riophae/vue-treeselect';
import '@riophae/vue-treeselect/dist/vue-treeselect.css';
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
    components: {
        Treeselect,
        Editor
    },
    data() {
        var checkTitle = (rule, value, callback) => {
            let data = {};
            data.bulletinTitle = this.form.bulletinTitle;
            data.bulletinId = this.form.bulletinPk;
            this.$axios.post('bulletinInfo/checkBulletinTitle', data).then(res => {
                if (res.data.code != 0) {
                    callback(new Error(res.data.data));
                    this.form.bulletinTitle = '';
                } else {
                    callback();
                }
            });
        };
        return {
            // 控制dialog的显示与隐藏
            addDialogVisible: false,
            // treeSelect绑定的数据
            selectData: [],
            // 表单信息
            receiveInfo: null,
            form: {
                // id
                bulletinPk: '',
                // 公告方式
                bulletinMode: 0,
                // 接收对象
                receiveInfo: [],
                // 公告类型
                bulletinType: '',
                // 公告标题
                bulletinTitle: '',
                // 截止日期
                closeDate: '',
                // 公告内容
                bulletinNote: '',
                // 附件id
                fileId: '',
                // 之前附件内容
                fileName: '',
                // 附件url
                url: ''
            },
            // 表单验证规则
            rules: {
                bulletinMode: [
                    {
                        required: true,
                        message: '请选择公告方式',
                        trigger: 'blur'
                    }
                ],
                receiveInfo: [
                    {
                        required: true,
                        message: '请选择接收对象',
                        trigger: 'blur'
                    }
                ],
                bulletinType: [
                    {
                        required: true,
                        message: '请选择公告类型',
                        trigger: 'blur'
                    }
                ],
                bulletinTitle: [
                    {
                        required: true,
                        message: '请输入公告标题',
                        trigger: 'blur'
                    },
                    {
                        validator: checkTitle,
                        trigger: 'blur'
                    }
                ],
                closeDate: [
                    {
                        required: true,
                        message: '请选择截至日期',
                        trigger: 'blur'
                    }
                ],
                bulletinNote: [
                    {
                        required: true,
                        message: '请输入公告内容',
                        trigger: 'blur'
                    }
                ]
            },
            // 选择器绑定的数据
            select: [],
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
            },
            // 文件上传路径
            uploadUrl: '',
            // 文件列表
            fileList: []
        };
    },
    created() {},
    methods: {
        // 打开dialog
        async openDialog(data, select, roleData, departmentData) {
            // if (data.bulletinMode == 1) {
            //     this.selectData = departmentData;
            //     this.receiveInfo = data.receiveInfos;
            // } else if (data.bulletinMode == 2) {
            //     this.selectData = roleData;
            //     this.receiveInfo = data.receiveInfos;
            // } else {
            //     this.selectData = [];
            //     this.receiveInfo = null;
            // }
            const res = await this.$axios.post('bulletinInfo/getBulletinInfo', {
                bulletinPk: data.bulletinPk
            });
            if (res.data.code == 0) {
                this.select = select;
                this.form = res.data.data;
                if (this.form.bulletinType == 0) {
                    this.form.bulletinType = null
                }
                if (res.data.data.bulletinMode == 1) {
                    this.selectData = departmentData;
                    this.receiveInfo = res.data.data.receiveInfos;
                } else if (res.data.data.bulletinMode == 2) {
                    this.selectData = roleData;
                    this.receiveInfo = res.data.data.receiveInfos;
                } else {
                    this.selectData = [];
                    this.receiveInfo = null;
                }
                this.addDialogVisible = true;
            }
            // this.select = select;
            // this.form.bulletinPk = data.bulletinPk;
            // this.form.bulletinMode = data.bulletinMode;
            // this.form.bulletinType = data.bulletinType;
            // this.form.bulletinTitle = data.bulletinTitle;
            // this.form.closeDate = data.closeDate;
            // this.form.bulletinNote = data.bulletinNote;
            // this.form.fileName = data.fileName;
            // this.form.url = data.url;
            // this.addDialogVisible = true;
        },
        // 关闭dialog
        handleToClose() {
            this.addDialogVisible = false;
            this.fileList = [];
            this.$refs.form.resetFields();
        },
        // 点击按钮保存
        handleToSave() {
            if (this.form.bulletinMode == 0) {
                this.form.receiveInfo = '全部';
            } else {
                this.form.receiveInfo = this.receiveInfo.join(',');
            }
            this.$refs.form.validate(async valid => {
                if (valid) {
                    const res = await this.$axios.post('bulletinInfo/update', this.form);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.handleToClose();
                        this.$emit('getTableData');
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        // 切换公告方式的回调
        async handleToChange(data) {
            if (data == 0) {
                this.selectData = [];
            } else if (data == 1) {
                const res = await this.$axios.post('sys/org/treeData');
                if (res.data.code == 0) {
                    this.selectData = res.data.data;
                }
            } else if (data == 2) {
                const res = await this.$axios.post('/sys/user/listAll');
                if (res.data.code == 0) {
                    this.selectData = res.data.data;
                }
            }
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
        },
        /**
         * @Author: Misaka.chen
         * @description: 文件上传
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleChange(file, fileList) {
            if (fileList.length > 0) {
                this.fileList = [fileList[fileList.length - 1]]; // 这一步，是 展示最后一次选择的csv文件
            }
        },
        handleToRemove(file, fileList) {
            if (fileList.length == 0) {
                this.form.fileId = '';
            }
        },
        // 附件上传
        async handleHttpRequest(file) {
            let formData = new FormData();
            formData.append('file', this.fileList[0].raw);
            // formData.append('FileType', 1);
            let config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
            const res = await this.$axios.post('upload/fileupload', formData, config);
            if (res.data.code == 0) {
                this.form.fileId = res.data.data.id;
            }
        },
        // 点击附件查看附件
        handleToSend() {
            window.open(this.form.url);
        }
    }
};
</script>

<style lang="less" scoped>
.dialog-footer {
    text-align: right;
}
</style>