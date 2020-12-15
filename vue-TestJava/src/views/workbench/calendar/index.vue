<template>
    <div class="calendar">
        <!-- 按钮区域 -->
        <el-row>
            <el-col :span="12" style="padding-left: 20px;">
                <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-has="'calendar-add'">新增</el-button>
            </el-col>
            <el-col :span="12" style="text-align: right;padding-right: 20px;">
                <el-button type="primary" @click="$router.push({path:'/'})" id="system-permission-back" icon="el-icon-back">返回</el-button>
            </el-col>
        </el-row>
        <!-- 日历区域 -->
        <el-calendar v-model="currentDate" currentmonth="true" id="calendar">
            <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
            <template slot="dateCell" slot-scope="{ date, data }">
                <!--自定义内容-->
                <div>
                    <div class="calendar-day">
                        {{data.day.split('-').slice(2).join('-')}}
                    </div>
                    <div v-for="(item, index) in calendarData" :key="index">
                        <div v-if="item.months.indexOf(data.day.split('-').slice(1)[0]) != -1">
                            <div v-if="item.days.indexOf(data.day.split('-').slice(2).join('-')) != -1">
                                <el-tooltip class="item" effect="dark" :content="item.timetitle" placement="right">
                                    <div class="color1" v-if="item.dicName == '低'" v-text="item.timetitle" @click="openDialog(item)"></div>
                                    <div class="color2" v-else-if="item.dicName == '中'" v-text="item.timetitle" @click="openDialog(item)"></div>
                                    <div class="color3" v-else-if="item.dicName == '高'" v-text="item.timetitle" @click="openDialog(item)"></div>
                                    <div class="color4" v-else v-text="item.timetitle" @click="openDialog(item)"></div>
                                </el-tooltip>
                            </div>
                            <div v-else></div>
                        </div>
                        <div v-else></div>
                    </div>
                </div>
            </template>
        </el-calendar>
        <!-- 新增弹框 -->
        <el-dialog title="新增事件" :visible.sync="dialogVisible" width="30%" :before-close="handleClose" :close-on-click-modal="false">
            <el-form ref="form" :model="form" label-width="80px" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model.trim="form.title" placeholder="请输入事件标题" maxlength="10" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="时间" prop="date">
                    <el-date-picker v-model="form.date" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" default-time="08:00:00" type="datetime" placeholder="选择日期时间" style="width:100%">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="级别">
                    <el-select v-model="form.level" placeholder="请选择级别" style="width:100%">
                        <!-- <el-option label="低" value="1"></el-option>
            <el-option label="中" value="2"></el-option>
            <el-option label="高" value="3"></el-option> -->
                        <el-option v-for="item in levelList" :key="item.id" :label="item.dicName" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model.trim="form.content" type="textarea" placeholder="请输入备注" maxlength="100" show-word-limit rows="5" resize="none"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="text" @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="handleToSave">确 定</el-button>
            </span>
        </el-dialog>
        <!-- 编辑弹框 -->
        <el-dialog title="编辑事件" :visible.sync="editDialogVisible" width="30%" :before-close="handleToClose" :close-on-click-modal="false">
            <el-form ref="editForm" :model="editForm" label-width="80px" :rules="rules">
                <el-form-item label="标题" prop="title">
                    <el-input v-model.trim="editForm.title" placeholder="请输入事件标题" maxlength="10" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="时间" prop="date">
                    <el-date-picker v-model="editForm.date" :editable="false" value-format="yyyy-MM-dd HH:mm:ss" type="datetime" placeholder="选择日期时间" style="width:100%">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="级别">
                    <el-select v-model="editForm.level" placeholder="请选择级别" style="width:100%">
                        <el-option v-for="item in levelList" :key="item.id" :label="item.dicName" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="content">
                    <el-input v-model.trim="editForm.content" type="textarea" placeholder="请输入备注" maxlength="100" show-word-limit rows="5" resize="none"></el-input>
                </el-form-item>
            </el-form>
            <div class="ed">
                <div>
                    <el-button type="danger" @click="handleToDelete">删 除</el-button>
                </div>
                <div>
                    <el-button type="text" @click="handleToClose">取 消</el-button>
                    <el-button type="primary" @click="handleToUpdata">确 定</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'calendar',
    data() {
        return {
            calendarData: [],
            // 当前时间
            currentDate: new Date(),
            // 时间选择器绑定的数据
            value: '',
            // 控制dialog的显示与隐藏
            dialogVisible: false,
            editDialogVisible: false,
            form: {
                // 事件标题
                title: '',
                // 时间
                date: '',
                // 事件级别
                level: '',
                // 内容
                content: ''
            },
            editForm: {
                id: '',
                // 事件标题
                title: '',
                // 时间
                date: '',
                // 事件级别
                level: '',
                // 内容
                content: ''
            },
            // 验证规则
            rules: {
                title: [
                    {
                        required: true,
                        message: '请输入活动名称',
                        trigger: 'blur'
                    }
                ],
                date: [
                    {
                        required: true,
                        message: '请选择时间',
                        trigger: 'blur'
                    }
                ],
                content: [
                    {
                        required: false,
                        message: '请输入活动名称',
                        trigger: 'blur'
                    }
                ]
            },
            level: '',
            levelList: []
        };
    },
    created() {
        this.getDictList();
        this.getDataList();
    },
    methods: {
        // 获取级别
        async getDictList() {
            const res = await this.$axios.post('sys/dictInfo/getDictList', {
                dicCode: 'level'
            });
            if (res.data.code == 0) {
                this.levelList = res.data.data;
                this.form.level = res.data.data[0].id;
            }
        },
        // 获取日程列表
        async getDataList() {
            const res = await this.$axios.post('sys/calendar/list');
            if (res.data.code == 0) {
                this.calendarData = res.data.data;
            }
        },
        // 点击按钮保存
        handleToSave() {
            this.$refs.form.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('sys/calendar/save', this.form);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.getDataList();
                        this.handleClose();
                        this.form.content = '';
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        // 点击新增
        handleAdd() {
            this.getDictList();
            this.dialogVisible = true;
        },
        // 打开编辑框
        openDialog(item) {
            this.getDictList();
            this.editForm = item;
            this.editForm.level = item.level;
            this.level = item.level;
            this.editDialogVisible = true;
        },
        // 点击按钮更新
        handleToUpdata() {
            this.$refs.editForm.validate(async (valid) => {
                if (valid) {
                    const res = await this.$axios.post('sys/calendar/updata', this.editForm);
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.getDataList();
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        // 点击按钮删除
        handleToDelete() {
            this.$confirm('此操作将永久删除该事件, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/calendar/del', {
                        id: this.editForm.id
                    });
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.getDataList();
                        this.handleToClose();
                    } else {
                        this.$message.error(`${res.data.data}`);
                    }
                })
                .catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
        },
        // dialog关闭回调
        handleClose() {
            this.dialogVisible = false;
            this.$refs.form.resetFields();
            this.form.content = '';
            this.form.level = this.levelList[0].id;
        },
        handleToClose() {
            this.editDialogVisible = false;
            this.$refs.editForm.resetFields();
            this.editForm.level = this.level;
            this.form.level = this.levelList[0].id;
        }
    }
};
</script>

<style lang="less" scoped>
.calendar {
    height: 100%;
    overflow-y: auto;
    .el-calendar {
        height: 100%;

        /deep/.el-calendar__body {
            height: 100%;

            .el-calendar-table {
                height: 100%;
            }
        }
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
</style>