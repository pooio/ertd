<template>
  <el-dialog title="新增" :visible.sync="addDialogVisible" :close-on-click-modal="false" width="50%" :before-close="handleToClose" :fullscreen="fullscreen">
        <div slot="title" class="dialog-header">
            <span>新增</span>
            <i class="el-icon-full-screen" style="cursor: pointer;" @click="fullscreen = !fullscreen"></i>
        </div>
        <div class="el-dialog-div" :style="{maxHeight: fullscreen ? '100%' : '500px'}">
            <el-scrollbar>
                <el-form :model="addForm" ref="addForm" :rules="rules" label-width="80px" >
                    <el-form-item label="Name" prop="Name">
                        <el-input v-model.trim="addForm.Name" placeholder="请输入内容" show-word-limit></el-input>
                    </el-form-item>
                    <el-form-item label="ViewQuantity" prop="ViewQuantity">
                        <el-input-number v-model="addForm.ViewQuantity"></el-input-number>
                    </el-form-item>
                    <el-form-item label="LikeQuantity" prop="LikeQuantity">
                        <el-input-number v-model="addForm.LikeQuantity"></el-input-number>
                    </el-form-item>
                    <el-form-item label="SaleQuantity" prop="SaleQuantity">
                        <el-input-number v-model="addForm.SaleQuantity"></el-input-number>
                    </el-form-item>
                    <el-form-item label="CollectionQuantity" prop="CollectionQuantity">
                        <el-input-number v-model="addForm.CollectionQuantity"></el-input-number>
                    </el-form-item>
                </el-form>
            </el-scrollbar>
        </div>
        <span slot= "footer" class="dialog-footer">            <el-button type="text" @click="handleToClose">取 消</el-button>            <el-button type="primary" @click="handleToSave">确 定</el-button>        </span>    </el-dialog></template>

<script>export default {
    props: [],    data() {
        return {
            addDialogVisible: false,
            addForm: {
                Name:'',
                ViewQuantity: 0,
                LikeQuantity: 0,
                SaleQuantity: 0,
                CollectionQuantity: 0,
            },
            rules: {
            },
            fullscreen:false
        };
    },
    methods: {
                openDialog() {            this.addDialogVisible = true;        },
        handleToClose() {            this.addDialogVisible = false;            this.$refs.addForm.resetFields();			this.fullscreen = false;
        },        async handleToSave() {            this.$refs.addForm.validate(async (valid) => {                if (valid) {
                    const res = await this.$axios.post('generate/GoodsData/save', this.addForm);
                    if (res.data.code == 0) {                        this.$message.success(`${res.data.data}`);                        this.$emit('getTableData');                        this.handleToClose();                    } else {                        this.$message.error(`${res.data.msg}`);                        this.handleToClose();                    }                } else {                    return false;                }	            });				        },
    }};</script><style lang="less" scoped>.dialog-footer {    text-align: right;}/deep/.el-input-number .el-input__inner {    text-align: left;}.el-select {    width: 100%;}.el-date-editor.el-input,.el-date-editor.el-input__inner {    width: 100%;}.el-dialog-div {    overflow: auto;}.el-input-number {    width: 100%;}.el-form-children {    .children-header {        display: flex;        justify-content: space-between;        align-items: center;        line-height: 24px;        font-size: 18px;        color: #303133;    }}.dialog-header {    display: flex;    justify-content: space-between;    padding-right: 30px;    line-height: 24px;    font-size: 18px;    color: #303133;}</style>