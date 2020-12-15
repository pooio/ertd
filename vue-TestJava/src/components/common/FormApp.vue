<template>
    <div class="box">
        <div>
            <!-- <formFill :data="formData" :formId="formId" v-if="flag"></formFill> -->
        </div>
        <div class="right-preview">
            <div id="editorHtml" style="display:none;"></div>
            <div id="editorJs" style="display:none;"></div>
            <div id="editorCss" style="display:none;"></div>
            <iframe v-if="isIframeLoaded" v-show="isIframeShow" ref="previewPage" width="100%" frameborder="0" src="preview.html" @load="iframeLoad" id="external-frame"></iframe>
        </div>
        <div v-if="imgShow" class="back-tip">
            <img style="width:100%" src="../../assets/img/back.png" alt="">
        </div>
    </div>
</template>

<script>
// import formFill from '../../views/customerForm/children/formFill';
import { parse } from '@babel/parser';
import { makeUpHtml, makeUpAppHtml, vueTemplate, vueScript, cssStyle } from '@/components/generator/html';
import { makeUpJs, makeUpSubmitJs } from '@/components/generator/js';
import { makeUpCss } from '@/components/generator/css';
import { exportDefault, beautifierConf, titleCase } from '@/utils/index';
import loadMonaco from '@/utils/loadMonaco';
import loadBeautifier from '@/utils/loadBeautifier';
import axios from 'axios';
import constants from '../../constants/index';
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
    // components: {
    //     formFill
    // },
    data: function () {
        return {
            flag: false,
            formId: '',
            formData: {},

            htmlCode: '',
            jsCode: '',
            cssCode: '',
            generateConf: {
                fileName: undefined,
                type: 'file'
            },
            formJson: {},
            isIframeShow: false,
            isIframeLoaded: false,
            isInitcode: false, // 保证open后两个异步只执行一次runcode
            isRefreshCode: false, // 每次打开都需要重新刷新代码

            config: {},
            imgShow: false
        };
    },
    created() {
        var _that = this;
        this.formId = this.getQueryString('formId') || '0';
        this.config = {
            headers: {
                Authorization: this.getQueryString('token') || '',
                USERID: this.getQueryString('userId') || '',
                sessionId: this.getQueryString('sessionId') || '' //是否暂停
            }
        };
        console.log(this.formId);
        this.getFormJson(this.formId);

        window.submitIframe = function (mark, data) {
            if (mark == 'cancel') {
                console.log(mark);
                _that.handleToBack(mark);
            } else if (mark == 'submit') {
                _that.handleToSave(data);
            }
        };
    },
    mounted() {
        this.$nextTick(() => {
            // var Title = document.getElementsByTagName('title')[0]
            // console.log(Title)
            document.addEventListener('UniAppJSBridgeReady', function () {
                console.log('初始化成功');
            });
        });
    },
    methods: {
        getQueryString(name) {
            var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
            var r = window.location.href.match(reg);
            if (r != null) {
                return decodeURIComponent(r[2]);
            }
            return null;
        },
        async getFormJson(id) {
            const params = new FormData();
            params.append('id', id);
            const res = await axios.post(constants.baseURL + 'form/getFormById', params, this.config);
            if (res.data.code == 0) {
                this.formData = res.data.data;
                document.title = this.formData.businessName;
                if (this.formData.formJson != null && this.formData.formJson != '') {
                    this.formJson = JSON.parse(this.formData.formJson);
                    this.formJson.fields.forEach((item, i) => {
                        if (item.__config__.tag === 'el-upload') {
                            this.formJson.fields.splice(i, 1);
                            return true;
                        }
                    });
                }
                this.generateCode();
                this.isIframeLoaded = true;
            } else {
                this.$message.error(res.msg);
            }
        },
        generateCode() {
            console.log(this.formJson);
            const { type } = this.generateConf;
            const html = vueTemplate(makeUpAppHtml(this.formJson, type));
            const script = vueScript(makeUpSubmitJs(this.formJson, type));
            const css = cssStyle(makeUpCss(this.formJson));
            loadBeautifier((btf) => {
                beautifier = btf;
                this.htmlCode = beautifier.html(html, beautifierConf.html);
                this.jsCode = beautifier.js(makeUpSubmitJs(this.formJson, type), beautifierConf.js);
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
            console.log(data);
            for (let key in data) {
                if (data[key] != null && typeof data[key] == 'object') {
                    console.log(key);
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

            const params = new FormData();
            params.append('formId', this.formId);
            params.append('dataJson', JSON.stringify(data));
            const res = await axios.post(constants.baseURL + 'formData/saveData', params, this.config);
            if (res.data.code == 0) {
                // this.$message.success('提交成功！');
                // window.parent.close();
                this.isIframeLoaded = !this.isIframeLoaded;
                this.imgShow = !this.imgShow;
                // this.handleToBack();
            } else {
                this.$message.error(`${res.data.data}`);
            }
        },
        handleToBack(val) {
            // document.addEventListener('UniAppJSBridgeReady', function() {
            //     console.log(val)
            //     uni.navigateBack();
            //     //向uniapp传值
            //     uni.postMessage({
            //         data: {
            //             action: 'message'
            //         }
            //     });
            //     uni.getEnv(function(res) {
            //         console.log('当前环境：' + JSON.stringify(res));
            //     });
            // });
        }
    }
};
</script>

<style lang="less" scoped>
.back-tip {
    width: 100%;
    height: 100%;
    background-color: #767676;
}
</style>