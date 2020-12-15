<template>
    <div class="box">
        <el-dialog title="查看子表" :visible.sync="dialogVisible" width="80%" :before-close="handleClose">
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane v-for="item in tabData" :label="item.businessName" :key="item.id" :name="item.id">
                    <el-table :data="tableData" style="width: 100%">
                        <el-table-column v-for="(item,index) in tableTitle" :key="index" :prop="item.fieldName" :label="item.label" width="180"></el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </el-dialog>
        <!-- <el-drawer append-to-body title="" :visible.sync="drawer" direction="rtl" :before-close="handleClose" size="80%">
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-table :data="tableData" border style="width: 100%">
                    <el-table-column v-for="(item,index) in tableTitle" :key="index" :prop="item.fieldName" :label="item.label" width="180"></el-table-column>
                </el-table>
            </el-tabs>
        </el-drawer> -->
    </div>
</template>

<script>
export default {
    props: ['sonInfo'],
    data() {
        return {
            drawer: false,
            // 控制dialog的显示与隐藏
            dialogVisible: false,
            activeName: '',
            loading: true,
            tabData: [],
            // 搜索条件
            searchInfo: {
                // 当前页
                pageNum: 1,
                // 每页显示的数量
                pageSize: 10,
                id: '',
                // 当前行id
                dataId: '',
                date: '',
                fieldValue: '',
                fieldColumn: ''
            },
            // 表格数据
            tableData: [],
            // 表头数据
            tableTitle: []
        };
    },
    created() {
        this.searchInfo = this.sonInfo;
    },
    methods: {
        openDialog(rowId, id) {
            this.searchInfo.dataId = rowId;
            this.searchInfo.id = id;
            this.getSonForm();
            this.dialogVisible = true;

            this.drawer = true;
        },
        // 获取子表
        async getSonForm(rowId) {
            const res = await this.$axios.post('form/getChildrenCustomTable', {
                formId: this.searchInfo.id
            });
            if (res.data.code == 0) {
                this.tabData = res.data.data;
                this.activeName = res.data.data[0].id;
                this.searchInfo.id = res.data.data[0].id;
                // console.log(JSON.parse(res.data.data[0].formJson));
                let title = this.tableTitleFilter(res.data.data[0].formJson);
                this.tableTitle = title;
                this.getTableData();
            }
        },
        // 获取表头
        async getTableData() {
            const res = await this.$axios.post('formData/getDataList', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        handleClose() {
            this.dialogVisible = false;
            this.drawer = false;
        },
        handleClick(tab) {
            this.tabData.forEach(async (item) => {
                if (item.id == tab.name) {
                    const res = await this.$axios.post('form/getFormByIdentification', {
                        id: item.formIdentification
                    });
                    this.searchInfo.id = res.data.data.id;
                    let title = this.tableTitleFilter(res.data.data.formJson);
                    this.tableTitle = title;
                    this.getTableData();
                }
            });
        },
        tableTitleFilter(val) {
            let arr = [];
            JSON.parse(val).fields.forEach((item) => {
                if (item.__config__.showStatistic == null || item.__config__.showStatistic == true) {
                    arr.push({
                        fieldName: item.__vModel__,
                        label: item.__config__.label,
                        fieldType: item.__config__.tag
                    });
                }
            });
            return arr;
        }
    }
};
</script>

<style lang="less" scoped>
</style>
