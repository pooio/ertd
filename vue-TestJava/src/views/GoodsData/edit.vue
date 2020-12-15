<template>
  <el-dialog title="编辑" :visible.sync="editDialogVisible" :close-on-click-modal="false" width="30%" :before-close="handleToClose" :fullscreen="fullscreen">
        <div slot="title" class="dialog-header">
            <span>编辑</span>
            <i class="el-icon-full-screen" style="cursor: pointer;" @click="fullscreen = !fullscreen"></i>
        </div>
        <el-form ref="editForm" :model="editForm" :rules="rules" label-width="80px" >
            <el-form-item label="Name" prop="Name">
                <el-input v-model.trim="editForm.Name" placeholder="请输入内容" maxlength="50" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="SaleQuantity" prop="SaleQuantity">
                <el-input-number v-model="editForm.SaleQuantity" ></el-input-number>
            </el-form-item>
            <el-form-item label="CollectionQuantity" prop="CollectionQuantity">
                <el-input-number v-model="editForm.CollectionQuantity" ></el-input-number>
            </el-form-item>
            <el-form-item label="ViewQuantity" prop="ViewQuantity">
                <el-input-number v-model="editForm.ViewQuantity" ></el-input-number>
            </el-form-item>
            <el-form-item label="LikeQuantity" prop="LikeQuantity">
                <el-input-number v-model="editForm.LikeQuantity" ></el-input-number>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">            <el-button type="text" @click="handleToClose">取 消</el-button>            <el-button type="primary" @click="handleToUpdate">确 定</el-button>        </span>    </el-dialog></template>
<script>export default {    props:[],    data() {        return {            editDialogVisible: false,            editForm: {
                Name:'',
                SaleQuantity: 0,
                CollectionQuantity: 0,
                ViewQuantity: 0,
                LikeQuantity: 0,
                id:''
            },
            rules: {
            },
            fullscreen: false
        };
    },
    created() {},
    methods: {
        async openDialog(row) {
                    const res = await this.$axios.post('generate/GoodsData/getInfo', {id: row.id});
            if (res.data.code == 0) {
                this.editForm.Name = res.data.data.Name;
            this.editForm.SaleQuantity = res.data.data.SaleQuantity;
            this.editForm.CollectionQuantity = res.data.data.CollectionQuantity;
            this.editForm.ViewQuantity = res.data.data.ViewQuantity;
            this.editForm.LikeQuantity = res.data.data.LikeQuantity;
                this.editForm.id = row.id;	                this.editDialogVisible = true;	            }        },        handleToClose() {            this.editDialogVisible = false;            this.$refs.editForm.resetFields();
        },        async handleToUpdate() {            this.$refs.editForm.validate(async (valid) => {                if (valid) {
                    const res = await this.$axios.post('generate/GoodsData/update', this.editForm);
                    if (res.data.code == 0) {                        this.$message.success(`${res.data.data}`);                        this.$emit('getTableData');                        this.handleToClose();                    } else {                        this.$message.error(`${res.data.data}`);                        this.handleToClose();                    }                } else {                    return false;                }            });				        },
    }};</script><style lang="less" scoped>/deep/.el-input-number .el-input__inner {    text-align: left;}.dialog-footer {    text-align: right;}.el-select {    width: 100%;}.el-date-editor.el-input,.el-date-editor.el-input__inner {    width: 100%;}.el-dialog-div {    max-height: 500px;    overflow: auto;}.el-form-children {    .children-header {        display: flex;        justify-content: space-between;        align-items: center;        line-height: 24px;        font-size: 18px;        color: #303133;    }}.dialog-header {    display: flex;    justify-content: space-between;    padding-right: 30px;    line-height: 24px;    font-size: 18px;    color: #303133;}</style>