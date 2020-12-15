
<template>
    <div class="box">
        <el-row :gutter="10">
            <el-col :span="16">
                <el-steps :active="active" simple>
                    <el-step title="编辑表单" icon="el-icon-edit"></el-step>
                    <el-step title="设计表单" icon="el-icon-menu"></el-step>
                    <el-step title="绑定流程" icon="el-icon-rank"></el-step>
                </el-steps>
            </el-col>
            <el-col :span="8" style="text-align: right;line-height: 46px;">
                <el-button type="primary" @click="$router.push({path:'form-edit',query:{data:JSON.stringify(preData)}})" id="form-home-back">上一步</el-button>
                <el-button type="primary" @click="submitForm('next')" id="form-home-next">下一步</el-button>
            </el-col>
        </el-row>

        <div class="line"></div>

        <div class="container">
            <div class="left-board">
                <el-scrollbar class="left-scrollbar">
                    <div class="components-list">
                        <div v-for="(item, listIndex) in leftComponents" :key="listIndex">
                            <div class="components-title">
                                <svg-icon icon-class="component" />
                                {{ item.title }}
                            </div>
                            <draggable class="components-draggable" :list="item.list" :group="{ name: 'componentsGroup', pull: 'clone', put: false }" :clone="cloneComponent" draggable=".components-item" :sort="false" @end="onEnd">
                                <div v-for="(element, index) in item.list" :key="index" class="components-item" @click="addComponent(element)">
                                    <div class="components-body">
                                        <svg-icon :icon-class="element.__config__.tagIcon" />
                                        {{ element.__config__.label }}
                                    </div>
                                </div>
                            </draggable>
                        </div>
                    </div>
                </el-scrollbar>
            </div>

            <div class="center-board">
                <div class="action-bar">
                    <!-- <el-button icon="el-icon-back" type="text" @click="$router.push({path:'form-list'})">
          返回
        </el-button> -->
                    <!-- <el-button icon="el-icon-folder-checked" type="text" @click="submitForm('save')">
          保存
        </el-button> -->
                    <el-button icon="el-icon-video-play" type="text" @click="run" id="form-home-run">
                        运行
                    </el-button>
                    <el-button icon="el-icon-view" type="text" @click="showJson" id="form-home-showJson">
                        查看json
                    </el-button>
                    <!-- <el-button icon="el-icon-download" type="text" @click="download" id="form-home-download">
          导出vue文件
        </el-button>
        <el-button class="copy-btn-main" icon="el-icon-document-copy" type="text" @click="copy" id="form-home-copy">
          复制代码
        </el-button> -->
                    <el-button class="delete-btn" icon="el-icon-delete" type="text" @click="empty" id="form-home-empty">
                        清空
                    </el-button>
                </div>
                <el-scrollbar class="center-scrollbar">
                    <el-row class="center-board-row" :gutter="formConf.gutter">
                        <el-form :size="formConf.size" :label-position="formConf.labelPosition" :disabled="formConf.disabled" :label-width="formConf.labelWidth + 'px'">
                            <draggable class="drawing-board" :list="drawingList" :animation="340" group="componentsGroup">
                                <draggable-item v-for="(element, index) in drawingList" :key="element.renderKey" :drawing-list="drawingList" :element="element" :index="index" :active-id="activeId" :form-conf="formConf" @activeItem="activeFormItem" @copyItem="drawingItemCopy" @deleteItem="drawingItemDelete" />
                            </draggable>
                            <div v-show="!drawingList.length" class="empty-info">
                                从左侧拖入或点选组件进行表单设计
                            </div>
                        </el-form>
                    </el-row>
                </el-scrollbar>
            </div>

            <right-panel :active-data="activeData" :form-conf="formConf" :parent-id="preData.parentId" :show-field="!!drawingList.length" @tag-change="tagChange" />

            <form-drawer :visible.sync="drawerVisible" :form-data="formData" size="100%" :generate-conf="generateConf" />
            <json-drawer size="60%" :visible.sync="jsonDrawerVisible" :json-str="JSON.stringify(formData)" @refresh="refreshJson" />
            <code-type-dialog :visible.sync="dialogVisible" title="选择生成类型" :show-file-name="showFileName" @confirm="generate" />
            <input id="copyNode" type="hidden">
        </div>
    </div>
</template>

<script>
import draggable from 'vuedraggable';
import { debounce } from 'throttle-debounce';
import { saveAs } from 'file-saver';
import ClipboardJS from 'clipboard';
import render from '@/components/render/render';
import FormDrawer from './FormDrawer';
import JsonDrawer from './JsonDrawer';
import RightPanel from './RightPanel';
import { inputComponents, selectComponents, layoutComponents, formConf } from '@/components/generator/config';
import { exportDefault, beautifierConf, isNumberStr, titleCase, deepClone } from '@/utils/index';
import { makeUpHtml, vueTemplate, vueScript, cssStyle } from '@/components/generator/html';
import { makeUpJs } from '@/components/generator/js';
import { makeUpCss } from '@/components/generator/css';
// import drawingDefalut from '@/components/generator/drawingDefalut';
// import logo from "@/assets/logo.png";
import CodeTypeDialog from './CodeTypeDialog';
import DraggableItem from './DraggableItem';
import { getDrawingList, saveDrawingList, getIdGlobal, saveIdGlobal, getFormConf } from '@/utils/db';
import loadBeautifier from '@/utils/loadBeautifier';

let beautifier;
const emptyActiveData = { style: {}, autosize: {} };
let oldActiveId;
let tempActiveData;
const drawingListInDB = getDrawingList();
const formConfInDB = getFormConf();
const idGlobal = getIdGlobal();

export default {
    components: {
        draggable,
        render,
        FormDrawer,
        JsonDrawer,
        RightPanel,
        CodeTypeDialog,
        DraggableItem
    },
    data() {
        return {
            // logo,
            active: 2,
            idGlobal,
            formConf: formConf,
            inputComponents,
            selectComponents,
            layoutComponents,
            labelWidth: 100,
            drawingList: [],
            drawingData: {},
            activeId: '',
            drawerVisible: false,
            formData: {},
            preData: {},
            dialogVisible: false,
            jsonDrawerVisible: false,
            generateConf: null,
            showFileName: false,
            activeData: {},
            saveDrawingListDebounce: debounce(340, saveDrawingList),
            saveIdGlobalDebounce: debounce(340, saveIdGlobal),
            leftComponents: [
                {
                    title: '输入型组件',
                    list: inputComponents
                },
                {
                    title: '选择型组件',
                    list: selectComponents
                }
                // {
                //     title: '布局型组件',
                //     list: layoutComponents
                // }
            ]
        };
    },
    computed: {},
    watch: {
        // eslint-disable-next-line func-names
        'activeData.__config__.label': function (val, oldVal) {
            if (this.activeData.placeholder === undefined || !this.activeData.__config__.tag || oldActiveId !== this.activeId) {
                return;
            }
            this.activeData.placeholder = this.activeData.placeholder.replace(oldVal, '') + val;
        },
        activeId: {
            handler(val) {
                oldActiveId = val;
            },
            immediate: true
        },
        drawingList: {
            handler(val) {
                this.saveDrawingListDebounce(val);
                if (val.length === 0) this.idGlobal = 100;
            },
            deep: true
        },
        idGlobal: {
            handler(val) {
                this.saveIdGlobalDebounce(val);
            },
            immediate: true
        }
    },
    created() {
        this.getFormJson(this.$route.query.data);
    },
    mounted() {
        if (this.drawingList.length > 0) {
            this.activeFormItem(this.drawingList[0]);
        }

        loadBeautifier((btf) => {
            beautifier = btf;
        });
        const clipboard = new ClipboardJS('#copyNode', {
            text: (trigger) => {
                const codeStr = this.generateCode();
                this.$notify({
                    title: '成功',
                    message: '代码已复制到剪切板，可粘贴。',
                    type: 'success'
                });
                return codeStr;
            }
        });
        clipboard.on('error', (e) => {
            this.$message.error('代码复制失败');
        });
    },
    methods: {
        getFormJson(id) {
            this.$axios.post('form/getFormById', { id }).then((res) => {
                if (res.data.code == 0) {
                    var jsonData = res.data.data;
                    this.preData = {
                        id: jsonData.id,
                        businessName: jsonData.businessName,
                        icon: jsonData.icon,
                        description: jsonData.description,
                        processDefinitionKey: jsonData.processDefinitionKey,
                        parentId: jsonData.parentId,
                    };
                    if (jsonData.formJson != null && jsonData.formJson != '') {
                        var formJson = JSON.parse(jsonData.formJson);
                        this.drawingList = formJson.fields;
                        delete formJson.fields;
                        this.formConf = formJson;
                        var maxFormId = 0;
                        this.drawingList.forEach((element) => {
                            if (maxFormId < element.__config__.formId) {
                                maxFormId = element.__config__.formId;
                            }
                        });
                        this.idGlobal = maxFormId;
                    } else {
                        this.drawingList = [];
                        this.idGlobal = 100;
                    }
                } else {
                    this.$message.error('数据获取失败');
                }
            });
        },
        activeFormItem(element) {
            this.activeData = element;
            this.activeId = element.__config__.formId;
        },
        onEnd(obj) {
            if (obj.from !== obj.to) {
                this.activeData = tempActiveData;
                this.activeId = this.idGlobal;
            }
        },
        addComponent(item) {
            const clone = this.cloneComponent(item);
            this.drawingList.push(clone);
            this.activeFormItem(clone);
        },
        cloneComponent(origin) {
            const clone = deepClone(origin);
            const config = clone.__config__;
            config.span = this.formConf.span; // 生成代码时，会根据span做精简判断
            this.createIdAndKey(clone);
            // clone.placeholder !== undefined && (clone.placeholder = clone.placeholder + clone.placeholder.indexOf(config.label)>-1?'':config.label);
            clone.placeholder !== undefined && (clone.placeholder += config.label);
            tempActiveData = clone;
            return tempActiveData;
        },
        createIdAndKey(item) {
            const config = item.__config__;
            config.formId = ++this.idGlobal;
            config.renderKey = +new Date(); // 改变renderKey后可以实现强制更新组件
            if (config.layout === 'colFormItem') {
                item.__vModel__ = `field${this.idGlobal}`;
            } else if (config.layout === 'rowFormItem') {
                config.componentName = `row${this.idGlobal}`;
                !Array.isArray(config.children) && (config.children = []);
                delete config.label; // rowFormItem无需配置label属性
            }
            if (Array.isArray(config.children)) {
                config.children = config.children.map((childItem) => this.createIdAndKey(childItem));
            }
            return item;
        },
        AssembleFormData() {
            this.formData = {
                fields: deepClone(this.drawingList),
                ...this.formConf
            };
        },
        generate(data) {
            const func = this[`exec${titleCase(this.operationType)}`];
            this.generateConf = data;
            func && func(data);
        },
        execRun(data) {
            this.AssembleFormData();
            this.drawerVisible = true;
        },
        execDownload(data) {
            const codeStr = this.generateCode();
            const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' });
            saveAs(blob, data.fileName);
        },
        execCopy(data) {
            document.getElementById('copyNode').click();
        },
        empty() {
            this.$confirm('确定要清空所有组件吗？', '提示', { type: 'warning' }).then(() => {
                this.drawingList = [];
                this.idGlobal = 100;
            });
        },
        drawingItemCopy(item, parent) {
            let clone = deepClone(item);
            clone = this.createIdAndKey(clone);
            parent.push(clone);
            this.activeFormItem(clone);
        },
        drawingItemDelete(index, parent) {
            parent.splice(index, 1);
            this.$nextTick(() => {
                const len = this.drawingList.length;
                if (len) {
                    this.activeFormItem(this.drawingList[len - 1]);
                }
            });
        },
        generateCode() {
            const { type } = this.generateConf;
            this.AssembleFormData();
            const script = vueScript(makeUpJs(this.formData, type));
            const html = vueTemplate(makeUpHtml(this.formData, type));
            const css = cssStyle(makeUpCss(this.formData));
            return beautifier.html(html + script + css, beautifierConf.html);
        },
        showJson() {
            this.AssembleFormData();
            this.jsonDrawerVisible = true;
        },
        download() {
            this.dialogVisible = true;
            this.showFileName = true;
            this.operationType = 'download';
        },
        run() {
            this.dialogVisible = true;
            this.showFileName = false;
            this.operationType = 'run';
        },
        copy() {
            this.dialogVisible = true;
            this.showFileName = false;
            this.operationType = 'copy';
        },
        tagChange(newTag) {
            newTag = this.cloneComponent(newTag);
            const config = newTag.__config__;
            newTag.__vModel__ = this.activeData.__vModel__;
            config.formId = this.activeId;
            config.span = this.activeData.__config__.span;
            this.activeData.__config__.tag = config.tag;
            this.activeData.__config__.tagIcon = config.tagIcon;
            this.activeData.__config__.document = config.document;
            if (typeof this.activeData.__config__.defaultValue === typeof config.defaultValue) {
                config.defaultValue = this.activeData.__config__.defaultValue;
            }
            Object.keys(newTag).forEach((key) => {
                if (this.activeData[key] !== undefined) {
                    newTag[key] = this.activeData[key];
                }
            });
            this.activeData = newTag;
            this.updateDrawingList(newTag, this.drawingList);
        },
        updateDrawingList(newTag, list) {
            const index = list.findIndex((item) => item.__config__.formId === this.activeId);
            if (index > -1) {
                list.splice(index, 1, newTag);
            } else {
                list.forEach((item) => {
                    if (Array.isArray(item.__config__.children)) this.updateDrawingList(newTag, item.__config__.children);
                });
            }
        },
        refreshJson(data) {
            this.drawingList = deepClone(data.fields);
            delete data.fields;
            this.formConf = data;
        },
        submitForm(item) {
            this.AssembleFormData();
            if (this.formData.fields == null || this.formData.fields.length == 0) {
                this.$message.error('表单内容不能为空');
                return;
            }
            var arr = [];
            this.formData.fields.forEach((item) => {
                if (arr.indexOf(item.__vModel__) == -1) {
                    arr.push(item.__vModel__);
                }
            });
            if (arr.length < this.formData.fields.length) {
                this.$message.error('表单内容字段名不能重复');
                return;
            }
            var addForm = {
                id: this.$route.query.data,
                json: JSON.stringify(this.formData)
            };
            this.$axios.post('form/saveCustomForm', addForm).then((res) => {
                if (res.data.code == 0) {
                    this.$message.success('保存成功');
                    if (item == 'next') {
                        this.$confirm('确定去绑定流程吗？', '提示', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        })
                            .then(async () => {
                                this.$router.push({
                                    path: 'form-bind',
                                    query: { data: this.$route.query.data, bindId: this.preData.processDefinitionKey }
                                });
                            })
                            .catch(() => {
                                this.$router.push({ path: 'form-list' });
                            });
                    }
                } else {
                    this.$message.error('保存失败');
                }
                console.log(res);
            });
        }
    }
};
</script>

<style lang='scss'>
@import '@/styles/home';
.container {
    height: calc(100% - 100px);
    width: calc(100% - 2px);
}
.line {
    margin: 18px 0;
    border-top: 1px solid #ccc;
}
</style>
