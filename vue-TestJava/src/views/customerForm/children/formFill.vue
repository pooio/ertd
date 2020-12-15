<template>
    <div class="right-preview">
        <div id="editorHtml" style="display:none;"></div>
        <div id="editorJs" style="display:none;"></div>
        <div id="editorCss" style="display:none;"></div>
        <iframe v-if="isIframeLoaded" v-show="isIframeShow" ref="previewPage" width="100%" frameborder="0" src="preview.html" @load="iframeLoad" id="external-frame"></iframe>
    </div>
</template>
<script>
import { parse } from '@babel/parser';
import { makeUpHtml, vueTemplate, vueScript, cssStyle } from '@/components/generator/html';
import { makeUpJs, makeUpSubmitJs } from '@/components/generator/js';
import { makeUpCss } from '@/components/generator/css';
import { exportDefault, beautifierConf, titleCase } from '@/utils/index';
import loadMonaco from '@/utils/loadMonaco';
import loadBeautifier from '@/utils/loadBeautifier';

const editorObj = {
    html: null,
    js: null,
    css: null
};
const mode = {
    html: 'html',
    js: 'javascript',
    css: 'css'
};
let beautifier;
let monaco;

export default {
    props: {
        data: {},
        formId: '',
        fillId: '',
        parentDataId: ''
    },
    data() {
        return {
            htmlCode: '',
            jsCode: '',
            cssCode: '',
            generateConf: {
                fileName: undefined,
                type: 'file'
            },
            formData: {},
            isIframeShow: false,
            isIframeLoaded: false,
            isInitcode: false, // 保证open后两个异步只执行一次runcode
            isRefreshCode: false, // 每次打开都需要重新刷新代码
            dicList: []
        };
    },
    computed: {},
    watch: {},
    created() {
        var _this = this;
        window.submitIframe = function (mark, data) {
            if (mark == 'cancel') {
                _this.$emit('dialogFill', false);
            } else if (mark == 'submit') {
                _this.handleToSave(data);
            }
        };

        if (this.data.formJson != null && this.data.formJson != '') {
            this.formData = JSON.parse(this.data.formJson);

            this.formData.fields.forEach(async (item, index) => {
                //回显用户名
                if (item.__config__.tag === 'el-input') {
                    if (item.isCurrentUser) {
                        item.__config__.defaultValue = sessionStorage.userName;
                    }
                }

                //回显级联和多选
                if (
                    item.__config__.tag === 'el-cascader' ||
                    item.__config__.tag === 'el-checkbox-group' ||
                    (item.__config__.tag === 'el-time-picker' && item['is-range'] == true) ||
                    (item.__config__.tag === 'el-date-picker' && item.type == 'daterange')
                ) {
                    var value = item.__config__.defaultValue;
                    if (value != null && value != '') {
                        var arr = [];
                        if (value.indexOf('，') > -1) {
                            arr = value.split('，');
                            arr.forEach((item) => {
                                arr.push(item.split('/'));
                            });
                        } else {
                            arr = value.split('/');
                        }
                        item.__config__.defaultValue = arr;
                    }
                }
                //回显开关
                if (item.__config__.tag === 'el-switch') {
                    var value = item.__config__.defaultValue;
                    item.__config__.defaultValue = value == 'true';
                }
                //回显滑块
                if (item.__config__.tag === 'el-slider') {
                    var value = item.__config__.defaultValue;
                    item.__config__.defaultValue = value * 1;
                }
                //回显上传
                if (item.__config__.tag === 'el-upload') {
                    var value = item.__config__.defaultValue;
                    if (value == '') {
                        item.__config__.defaultValue = null;
                    }
                    // if(value!=null && value.length>0){

                    //     eval('('+value+')').forEach(ele =>{
                    //         item.__config__.regList.push({
                    //             id: ele['id'],
                    //             name: ele['name'],
                    //             url: ele['url'],
                    //         })
                    //     });
                    //     console.log(item.__config__.regList);
                    // }
                }
                //设置上传为自动上传
                if (item.__config__.tag === 'el-upload') {
                    item['auto-upload'] = true;
                }
                if (item.__config__.tag === 'el-select' && item.dataType == '1') {
                    const res = await this.getDictList(item.__config__.func);
                    item.__slot__.options = res;
                }
                if (item.__config__.tag === 'el-select' && item.dataType == '2') {
                    const res = await this.getDataDictList(item.businessId, item.dataTypeName);
                    item.__slot__.options = res;
                    console.log(this.formId, '数据');
                }
                if (index == this.formData.fields.length - 1) {
                    this.generateCode();
                    this.isIframeLoaded = true;
                }
            });
            //设置所有表单禁用
            if (this.formId == null) {
                this.formData.formBtns = false;
                this.formData.disabled = true;
            }

            // console.log(this.formData);
            // this.generateCode();
            // this.isIframeLoaded = true;
        }
    },
    mounted() {},
    methods: {
        generateCode() {
            const { type } = this.generateConf;
            const html = vueTemplate(makeUpHtml(this.formData, type));
            const script = vueScript(makeUpSubmitJs(this.formData, type));
            const css = cssStyle(makeUpCss(this.formData));
            loadBeautifier((btf) => {
                beautifier = btf;
                this.htmlCode = beautifier.html(html, beautifierConf.html);
                this.jsCode = beautifier.js(makeUpSubmitJs(this.formData, type), beautifierConf.js);
                this.cssCode = beautifier.css(css, beautifierConf.html);
                loadMonaco((val) => {
                    monaco = val;
                    this.setEditorValue('editorHtml', 'html', this.htmlCode);
                    this.setEditorValue('editorJs', 'js', this.jsCode);
                    this.setEditorValue('editorCss', 'css', this.cssCode);
                    if (!this.isInitcode) {
                        this.isRefreshCode = true;
                        this.isIframeShow && (this.isInitcode = true) && this.runCode();
                    }
                });
            });
            return null;
        },
        setEditorValue(id, type, codeStr) {
            if (editorObj[type] != null) {
                editorObj[type].setValue(codeStr);
            } else {
                editorObj[type] = monaco.editor.create(document.getElementById(id), {
                    value: codeStr,
                    theme: 'vs-dark',
                    language: mode[type],
                    automaticLayout: true
                });
            }
            // ctrl + s 刷新
            editorObj[type].onKeyDown((e) => {
                if (e.keyCode === 49 && (e.metaKey || e.ctrlKey)) {
                    this.runCode();
                }
            });
        },
        runCode() {
            const jsCodeStr = editorObj.js.getValue();

            try {
                const ast = parse(jsCodeStr, { sourceType: 'module' });
                const astBody = ast.program.body;

                if (astBody.length > 1) {
                    this.$confirm('js格式不能识别，仅支持修改export default的对象内容', '提示', {
                        type: 'warning'
                    });
                    return;
                }
                if (astBody[0].type === 'ExportDefaultDeclaration') {
                    const postData = {
                        type: 'refreshFrame',
                        data: {
                            generateConf: this.generateConf,
                            html: editorObj.html.getValue(),
                            js: jsCodeStr.replace(exportDefault, ''),
                            css: editorObj.css.getValue(),
                            scripts: this.scripts,
                            links: this.links
                        }
                    };
                    this.$refs.previewPage.contentWindow.postMessage(postData, location.origin);
                } else {
                    this.$message.error('请使用export default');
                }
            } catch (err) {
                this.$message.error(`js错误：${err}`);
            }
        },
        iframeLoad() {
            if (!this.isInitcode) {
                this.isIframeShow = true;
                this.isRefreshCode && (this.isInitcode = true) && this.runCode();
                this.$nextTick(() => {
                    setTimeout(() => {
                        var oIframe = document.getElementById('external-frame');
                        var list = oIframe.contentWindow.document.getElementsByClassName('el-form');
                        if (list.length > 0) {
                            oIframe.height = list[0].offsetHeight + 60;
                        }
                        oIframe.height = oIframe.height < 400 ? 400 : oIframe.height;
                    }, 500);
                });
            }
        },
        async handleToSave(data) {
            // console.log(data);
            for (let key in data) {
                if (data[key] != null && typeof data[key] == 'object') {
                    // console.log(key);
                    var itemStr = '';
                    data[key].forEach((item) => {
                        if (typeof item == 'object') {
                            var eleStr = '';
                            item.forEach((ele) => {
                                eleStr += (eleStr == '' ? '' : '/') + ele;
                            });
                            itemStr += (itemStr == '' ? '' : '，') + eleStr;
                        } else {
                            itemStr += (itemStr == '' ? '' : '/') + item;
                        }
                    });
                    data[key] = itemStr;
                }
            }
            var info = {
                dataId: this.fillId,
                formId: this.formId,
                dataJson: JSON.stringify(data),
                parentDataId: this.parentDataId ? this.parentDataId : ''
            };
            const res = await this.$axios.post('formData/saveData', info);
            if (res.data.code == 0) {
                this.$message.success('提交成功！');
                this.$emit('tableRefrash');
                this.$emit('dialogFill', false);
            } else {
                this.$message.error(`${res.data.data}`);
            }
        },
        async getDictList(val) {
            var arr = [];
            const res = await this.$axios.post('sys/dictInfo/getDictList', { dicCode: val });
            res.data.data.forEach((element) => {
                arr.push({
                    label: element.dicName,
                    value: element.dicCode
                });
            });
            return arr;
        },
        async getDataDictList(formId, column) {
            var arr = [];
            const res = await this.$axios.post('formData/getCustomDirData', { formId, column });
            res.data.data.forEach((element) => {
                arr.push({
                    label: element.columns,
                    value: element.columns
                });
            });
            return arr;
        }
    }
};
</script>
<style lang="scss" scoped>
@import '@/styles/mixin.scss';
.tab-editor {
    position: absolute;
    top: 33px;
    bottom: 0;
    left: 0;
    right: 0;
    font-size: 14px;
}
.left-editor {
    position: relative;
    height: 100%;
    background: #1e1e1e;
    overflow: hidden;
}
.setting {
    position: absolute;
    right: 15px;
    top: 3px;
    color: #a9f122;
    font-size: 18px;
    cursor: pointer;
    z-index: 1;
}
.right-preview {
    height: 100%;
    .result-wrapper {
        height: calc(100% - 33px);
        width: 100%;
        overflow: auto;
        padding: 12px;
        box-sizing: border-box;
    }
}
@include action-bar;
::v-deep .el-drawer__header {
    display: none;
}
</style>