<template>
    <div class="home">
        <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="12">
                <el-card class="box-card el-table-name">
                    <el-tabs v-model="activeName" class @tab-click="handleClick">
                        <el-tab-pane label="流程表单" name="first">
                            <div>
                                <ul class="launchList">
                                    <li v-for="item in launchList" :key="item.id" @click="toFormFill(item)" :title="item.businessName" class="bulletinTitle">
                                        <i :class="item.icon" :style="{backgroundColor:item.color ? item.color : '#408eff'}"></i>
                                        <span>{{item.businessName}}</span>
                                    </li>
                                </ul>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane label="待我审批" name="second">
                            <el-table :data="tableDataPending" style="width: 100%;margin-top: 6px;" border>
                                <el-table-column prop="processDefinitionName" label="流程名称" :resizable="false">
                                    <template slot-scope="scope">
                                        <span @click="viewModal(scope.row)" class="bulletinTitle cursorPoint" :title="scope.row.processDefinitionName">{{scope.row.processDefinitionName}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="name" label="当前环节" :resizable="false" width="150"></el-table-column>
                                <el-table-column prop="createTime" label="发起时间" :resizable="false" width="150">
                                    <template slot-scope="scope">
                                        <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                                    </template>
                                </el-table-column>
                                <!-- <el-table-column prop="assigneeName" label="负责人" :resizable="false"></el-table-column>-->
                                <el-table-column label="操作" :resizable="false" width="100" align="center">
                                    <template slot-scope="scope">
                                        <span @click="approve(scope.row)" title="审批申请单" class="approveBtn"><i class="el-icon-success"></i> 审批</span>
                                        <!-- <el-button @click="approve(scope.row)" type="primary" title="审批申请单" icon="el-icon-success" size="mini">审批</el-button> -->
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-tab-pane>
                        <el-tab-pane label="我已审批" name="third">
                            <el-table :data="tableDataProcessed" style="width: 100%;margin-top: 6px;" border>
                                <el-table-column prop="processDefinitionName" label="流程名称" :resizable="false">
                                    <template slot-scope="scope">
                                        <span @click="viewModal(scope.row)" class="bulletinTitle cursorPoint" :title="scope.row.processDefinitionName">{{scope.row.processDefinitionName}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="endTime" label="完成时间" :resizable="false" width="300">
                                    <template slot-scope="scope">
                                        <span>{{ scope.row.endTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-tab-pane>
                        <el-tab-pane label="我发起的" name="four">
                            <el-table :data="tableDataApplayForSelf" style="width: 100%;margin-top: 6px;" border :row-class-name="statusClass">
                                <el-table-column prop="processName" label="流程名称" :resizable="false">
                                    <template slot-scope="scope">
                                        <span @click="viewModal(scope.row)" class="bulletinTitle cursorPoint" :title="scope.row.processName">{{scope.row.processName}}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="currNode" label="当前环节" :resizable="false" width="100"></el-table-column>
                                <el-table-column prop="createTime" label="发起时间" :resizable="false" width="140">
                                    <template slot-scope="scope">
                                        <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="endTime" label="完成时间" width="140"></el-table-column>
                                <el-table-column prop="businessStatueName" label="状态" width="100" class-name="statusClass"></el-table-column>
                            </el-table>
                        </el-tab-pane>
                    </el-tabs>
                    <el-button style="float: right; padding: 3px 0" type="text" class="more-bth-right" @click="$router.push({path:'workSpace',query:{data:activeName}})">更多>></el-button>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span class="el-backtop notice">公告通知</span>
                        <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('notice')" id="home-notice-more">更多>></el-button>
                    </div>
                    <el-table :data="tableDataNotice" style="width: 100%" border>
                        <el-table-column :resizable="false" align="center" type="index" label="序号" width="50"></el-table-column>
                        <el-table-column :resizable="false" label="公告标题">
                            <template slot-scope="scope">
                                <!-- 表格hover字体颜色跟随主题变换 -->
                                <div class="bulletinTitle el-checkbox-button__inner" style="cursor: pointer;" @click="handleToSend(scope.row)">{{scope.row.bulletinTitle}}</div>
                                <!-- <div class="bulletinTitle" style="cursor: pointer;" @click="handleToSend(scope.row)" id="home-notice-bulletinTitle">{{scope.row.bulletinTitle}}</div> -->
                            </template>
                        </el-table-column>
                        <el-table-column :resizable="false" prop="createTime" label="时间" width="140"></el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="24">
                <el-card class="box-card">
                    <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('calendar')" id="home-calendar-more">更多>></el-button>
                    <el-calendar v-model="currentDate" currentmonth="true" id="calendar">
                        <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
                        <template slot="dateCell" slot-scope="{ data }">
                            <!--自定义内容-->
                            <div>
                                <div class="calendar-day">
                                    {{data.day.split('-').slice(2).join('-')}}
                                </div>
                                <div v-for="(item, index) in calendarData" :key="index">
                                    <div v-if="item.months.indexOf(data.day.split('-').slice(1)[0]) != -1">
                                        <div v-if="item.days.indexOf(data.day.split('-').slice(2).join('-')) != -1">
                                            <el-tooltip class="item" effect="dark" :content="item.timetitle" placement="right">
                                                <div class="color1" v-if="item.dicName == '低'" v-text="item.timetitle"></div>
                                                <div class="color2" v-else-if="item.dicName == '中'" v-text="item.timetitle"></div>
                                                <div class="color3" v-else-if="item.dicName == '高'" v-text="item.timetitle"></div>
                                                <div class="color4" v-else v-text="item.timetitle"></div>
                                            </el-tooltip>
                                        </div>
                                        <div v-else></div>
                                    </div>
                                    <div v-else></div>
                                </div>
                            </div>
                        </template>
                    </el-calendar>
                </el-card>
            </el-col>
        </el-row>
        <!-- 查看公告 -->
        <bulletinInfo ref="bulletinInfo" :content="content"></bulletinInfo>
        <!-- 审批 -->
        <el-dialog title="审批" :visible.sync="addDialogFormVisible" width="900px">
            <approve @dialog="dialogForm" v-bind:editDataProp="editData" v-if="addDialogFormVisible"></approve>
        </el-dialog>
        <!-- 填写 -->
        <el-dialog title="填写" :visible.sync="fillVisible" width="900px" :close-on-click-modal="false">
            <formFill @dialogFill="dialogFormFill" v-bind:data="formData" v-bind:formId="formId" v-if="fillVisible"></formFill>
        </el-dialog>
        <!-- 审批记录 -->
        <history @dialogHide="dialogHanderHide" :tableDialog="tableDialog" :formKey="formKey" v-if="approvalVisible"></history>

    </div>
</template>

<script>
import approve from '../process/workSpaceChildren/approve';
import history from '../process/pendingChildren/history';
import bulletinInfo from '../../components/common/bulletinInfo';
import formFill from '../customerForm/children/formFill';
export default {
    components: { approve, history, bulletinInfo, formFill },
    data() {
        return {
            tableDialog: [],
            pic: '',
            formKey: '',
            // 控制dialog的显示与隐藏
            fillVisible: false,
            approvalVisible: false, //审批记录弹窗
            addDialogFormVisible: false,
            activeName: 'first',
            // 表格绑定的数据
            tableDataNotice: [],
            tableDataPending: [],
            tableDataProcessed: [],
            tableDataApplayForSelf: [],
            // 日历数据
            calendarData: [],
            // 当前时间
            currentDate: new Date(),
            // 公告内容
            content: '',
            // 发起数据
            launchList: [],
            formId: '',
            formData: {}
        };
    },
    created() {
        this.getTableDataNotice();
        this.getDataList();
        this.getData();
    },
    methods: {
        /**
         * @Author: flynn.yang
         * @description:点击审批
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        approve(row) {
            this.addDialogFormVisible = true;
            this.editData = row;
        },
        /**
         * @Author: flynn.yang
         * @description:审批回调
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        dialogForm(data) {
            this.addDialogFormVisible = data;
            this.getSysInfoListPending();
        },
        dialogFormFill() {
            this.fillVisible = false;
        },
        handleClick() {
            console.log(this.activeName);
            if (this.activeName == 'first') {
                this.getLaunchList();
            } else if (this.activeName == 'second') {
                this.getSysInfoListPending();
            } else if (this.activeName == 'third') {
                this.getSysInfoListProcessed();
            } else if (this.activeName == 'four') {
                this.getSysInfoListApplayForSelf();
            }
        },
        /**
         * @Author: flynn.yang
         * @description: 获取我审批列表
         * @param {type}
         * @return:
         * @Version: 1.0
         * @Author: serena.li
         * @description: 修改命名规范
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        getData() {
            // this.getSysInfoListPending();
            // this.getSysInfoListProcessed();
            // this.getSysInfoListApplayForSelf();
            this.getLaunchList();
        },

        async getSysInfoListPending() {
            const res = await this.$axios.post('bpm/task/todo/listPersonTasks', {
                pageNum: 1,
                pageSize: 5,
                type: 1
            });
            if (res.data.code == 0) {
                this.tableDataPending = res.data.data.data;
            }
        },
        async getSysInfoListProcessed() {
            const res = await this.$axios.post('bpm/task/history/listTasks', {
                pageNum: 1,
                pageSize: 5
            });
            if (res.data.code == 0) {
                this.tableDataProcessed = res.data.data.data;
            }
        },
        async getSysInfoListApplayForSelf() {
            const res = await this.$axios.post('allFormApp/findApp', {
                pageNum: 1,
                pageSize: 5
            });
            if (res.data.code == 0) {
                this.tableDataApplayForSelf = res.data.data.data;
            }
        },
        // 发起数据
        async getLaunchList() {
            const res = await this.$axios.post('form/getCustomFormList', {
                pageNum: 1,
                pageSize: 12,
                type: 1
            });
            if (res.data.code == 0) {
                this.launchList = res.data.data;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTableDataNotice() {
            let info = {
                pageNum: 1,
                pageSize: 5,
                bulletinTitle: ''
            };
            const res = await this.$axios.post('bulletinInfo/getUserBulletinInfoList', info);

            if (res.data.code == 0) {
                this.tableDataNotice = res.data.data;
            }
        },
        // 点击查看公告
        async handleToSend(data) {
            this.content = data;
            this.$refs.bulletinInfo.openDialog();
            const res = await this.$axios.post('bulletinInfo/toBulletinInfo', {
                sendPk: data.sendPk
            });
        },
        // 获取日程列表
        async getDataList() {
            const res = await this.$axios.post('sys/calendar/list');
            if (res.data.code == 0) {
                this.calendarData = res.data.data;
            }
        },

        // 状态高亮显示
        statusClass({ row, rowIndex }) {
            if (row.businessStatueName === '审批通过') {
                return 'approvalPass';
            } else if (row.businessStatueName === '审批不通过') {
                return 'approvalNoPass';
            } else if (row.businessStatueName === '待审批') {
                return 'waitApproval';
            } else {
                return 'inApproval';
            }
        },
        // 审批记录回调
        dialogHanderHide() {
            this.approvalVisible = false;
        },
        // 审批流程名称-详情
        viewModal(val) {
            // console.log(val);
            this.formKey = val.businessKey;
            this.approvalVisible = true;
            let that = this;

            let postDataImg = {
                processInstanceId: val.processInstanceId
            };

            this.$axios
                .post('bpm/instance/history/img', postDataImg, {
                    responseType: 'arraybuffer'
                })
                .then((res) => {
                    return (
                        'data:image/png;base64,' + btoa(new Uint8Array(res.data).reduce((data, key) => data + String.fromCharCode(key), ''))
                    );
                })
                .then((data) => {
                    //这一次箭头函数是依赖于第一次.then函数处理的data值
                    // console.log(data);
                    var oStu = document.getElementById('images');
                    oStu.setAttribute('src', data);
                });
            this.$axios
                .post('bpm/instance/listHistoricTasks', { processInstanceId: val.processInstanceId })
                .then((res) => {
                    // console.log('555', res);
                    if (res != null) {
                        that.tableDialog = res.data.data;
                    }
                })
                .catch((err) => {
                    console.info('报错的信息', err);
                });
        },
        // 发起跳转自定义表单填写页面
        toFormFill(item) {
            // let str = null;
            // str = JSON.parse(item.formJson);
            // str.fields.forEach(async (ele, i) => {
            //     if (ele.__config__.tag == 'el-select') {
            //         const res = await this.getDictList(ele.__config__.func);
            //         console.log('1');
            //         ele.__slot__.options = res;
            //         console.log('2');
            //     }
            //     if (i == str.fields.length - 1) {
            //         item.formJson = JSON.stringify(str)
            //         console.log('3');
            //         this.formId = item.id;
            //         this.formData = item;
            //         this.fillVisible = true;
            //         console.log('4');
            //     }
            // });
            this.formId = item.id;
            this.formData = item;
            this.fillVisible = true;
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
        }
    }
};
</script>

<style lang="less" scoped>
@import '../../assets/css/style.css';
.home {
    height: 100%;
    .el-calendar {
        height: 100%;

        /deep/.el-calendar__body {
            height: 100%;

            .el-calendar-table {
                height: 100%;
            }
        }
    }

    /deep/.el-card__header {
        border-bottom: 2px solid #e4e7ed;
    }
}

.calendar-day {
    text-align: center;
    // color: #202535;
    line-height: 30px;
    font-size: 12px;
}

.color1 {
    color: #fff;
    font-size: 14px;
    margin-top: 5px;
    background-color: skyblue;
}

.color2 {
    color: #fff;
    font-size: 14px;
    margin-top: 5px;
    background-color: orange;
}

.color3 {
    color: #fff;
    font-size: 14px;
    margin-top: 5px;
    background-color: red;
}

.color4 {
    color: #fff;
    font-size: 14px;
    margin-top: 5px;
    background-color: #66cc;
}

.color1,
.color2,
.color3,
.color4 {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

#calendar .el-button-group > .el-button:not(:first-child):not(:last-child):after {
    content: '当月';
}

.ed {
    display: flex;
    justify-content: space-between;
}

/deep/.el-calendar-table td.is-selected {
    background-color: #9abbdc;
}

/deep/.el-calendar-table .el-calendar-day:hover {
    cursor: pointer;
    background-color: #9abbdc;
}

/deep/.el-calendar-table .el-calendar-day {
    height: 100%;
}

.notice-title {
    text-align: center;
    margin-bottom: 20px;
}

.enclosure {
    margin-top: 20px;
    cursor: pointer;

    span {
        color: #408eff;
    }
}

.gonggao {
    font-size: 14px;
    padding-left: 5px;
    border-left: 3px solid #408eff;
    width: 31px;
}

.biaoti {
    text-align: center;
    font-weight: 700;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.info {
    border-top: 2px solid #ccc;
    text-align: center;

    span {
        display: inline-block;
        margin: 10px 5px 0;
    }
}
.bulletinTitle {
    background: transparent !important;
    border: none !important;
    padding: 0 !important;
    transition: none !important;
    line-height: normal !important;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.bulletinTitle:hover {
    font-weight: 700;
}
.el-table-name {
    position: relative;
}
.more-bth-right {
    position: absolute;
    z-index: 9;
    top: 20px;
    right: 20px;
}
// 审批状态颜色区分
/deep/.approvalPass .statusClass {
    color: #67c23a;
}
/deep/.approvalNoPass .statusClass {
    color: #f56c6c;
}
/deep/.waitApproval .statusClass {
    color: #e6a23c;
}
/deep/.inApproval .statusClass {
    color: #409eff;
}
//待我审批操作按钮
.approveBtn {
    color: #409eff;
    cursor: pointer;
}
// 发起
.launchList li {
    float: left;
    width: 33%;
    cursor: pointer;
    list-style: none;
    margin: 13px 0;
}
.launchList li i {
    float: left;
    width: 32px;
    height: 32px;
    border-radius: 32px;
    // background-color: #408eff;
    color: #fff;
    text-align: center;
    line-height: 32px;
    font-size: 16px;
}
.launchList li span {
    width: calc(100% - 60px);
    display: inline-block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    padding: 0 12px;
    font-size: 14px;
    line-height: 32px;
}
</style>