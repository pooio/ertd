<!--
 * @Author: flynn.yang
 * @Date: 2019-11-18 13:42:43
 * @LastEditors: flynn.yang
 * @LastEditTime: 2020-07-07 10:59:48
 * @Description: MetaShare
 * @Version: 1.0
 -->
<template>
    <el-dialog
        title="流程审批信息"
        width="900"
        class="ln_dialog"
        :visible.sync="dialogTableVisiblee"
        @close="close"
        :close-on-click-modal="false"
    >
        <div>
            <el-tabs v-model="activeName">
                <el-tab-pane label="审批表单流程" name="first" class="tabPane">
                    <!-- 审批Form表单 -->
                    <formFill :data="formData" v-if="flag"></formFill>
                    <!-- 审批流程 -->
                    <div classs="table">
                        <el-table
                            ref="multipleTable"
                            :data="tableDialog"
                            border
                            stripe
                            :header-cell-style="{'padding-top': '9px','padding-bottom': '9px'}"
                            :cell-style="{'padding-top': '3px','padding-bottom': '3px'}"
                        >
                            <el-table-column prop="processDefinitionName" label="名称"></el-table-column>
                            <el-table-column prop="startTime" label="开始时间">
                                <template slot-scope="scope">
                                    <span>{{ scope.row.startTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="endTime" label="结束时间">
                                <template slot-scope="scope">
                                    <span>{{ scope.row.endTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="assigneeName" label="审批人"></el-table-column>
                            <el-table-column prop="commentMessage" label="审批意见">
                                <template slot-scope="scope">
                                    <span v-html="scope.row.commentMessage"></span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="审批流程图" name="second">
                    <!-- 审批流程图 -->
                    <div class="img">
                        <img
                            :src="pic"
                            id="images"
                            alt
                            style="margin: 0 auto;display: block;margin-bottom: 20px;width:100%"
                        />
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </el-dialog>
</template>

<script>
import formFill from '../../customerForm/children/formFill';
export default {
    name: 'tender',
    props: ['tableDialog', 'formKey'],

    components: {
        formFill
    },
    data() {
        return {
            vis: false,
            dialogTableVisiblee: true,
            pic: '',
            formData: {}, //form表单数据
            flag: false,
            activeName:"first"
        };
    },
    updated() {},
    created() {
        // this.getFormJson();
    },
    mounted() {
        this.getFormJson();
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description: 关闭弹框
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        close() {
            this.$emit('dialogHide');
        },
        // 获取表单页面
        getFormJson() {
            this.$axios
                .post('form/getFormData', { businessKey: this.formKey })
                .then((res) => {
                    console.log(res);
                    if (res != null) {
                        this.formData = res.data.data;
                        this.$axios
                            .post('formData/getDataJson', { businessKey: this.formKey })
                            .then((val) => {
                                if (val != null) {
                                    var valData = JSON.parse(val.data.data.customData);
                                    if (this.formData.formJson != null && this.formData.formJson != '') {
                                        var newFormData = JSON.parse(this.formData.formJson);
                                        newFormData.fields.forEach((item) => {
                                            if (item) {
                                                item.disabled = true;
                                                item.__config__.defaultValue = valData[item.__vModel__];
                                            }
                                        });
                                        this.formData.formJson = JSON.stringify(newFormData);
                                        this.flag = true;
                                    }
                                }
                            })
                            .catch((data) => {
                                // console.info('报错的信息', err);
                            });
                    }
                })
                .catch((err) => {
                    // console.info('报错的信息', err);
                });
        }
    }
};
</script>

<style scoped lang="less">

</style>

