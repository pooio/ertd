<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-06 11:30:55
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 14:53:59
 * @Description: 消息中心
 * @Version: 1.0
 -->
<template>
    <div class="box">
        <el-tabs v-model="message" @tab-click="handleToClick" class="message-tabs">
            <el-tab-pane :label="`未读消息(${unread.length})`" name="first">
                <el-table :data="unread" :show-header="false" style="width: 100%" height="calc(100% - 110px)">
                    <el-table-column>
                        <template slot-scope="scope">
                            <!-- <span class="message-title" @click="handleToSee(scope.row)">{{ scope.row.subject }}</span> -->
                            <span class="message-title" @click="handleToSee(scope.row)" v-html="scope.row.subject"></span>
                        </template>
                    </el-table-column>
                    <el-table-column width="180">
                        <template slot-scope="scope">
                            <span>{{ scope.row.createTime | moment('YYYY-MM-DD HH:mm:ss') }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column width="120">
                        <template slot-scope="scope">
                            <el-button size="mini" title="标记为已读" type="primary" icon="el-icon-circle-check" @click="handleRead(scope.row.id)">已读</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="handle-row">
                    <el-button type="primary" title="全部未读消息标记为已读" icon="el-icon-circle-check" @click="handleAllRead" :disabled="unread.length == 0">全部标为已读</el-button>
                </div>
            </el-tab-pane>
            <el-tab-pane :label="`已读消息(${read.length})`" name="second">
                <template v-if="message === 'second'">
                    <el-table :data="read" :show-header="false" style="width: 100%" height="calc(100% - 110px)">
                        <el-table-column>
                            <template slot-scope="scope">
                                <span class="message-title" @click="handleToSee(scope.row)" v-html="scope.row.subject"></span>
                            </template>
                        </el-table-column>
                        <el-table-column width="150">
                            <template slot-scope="scope">
                                <span>{{ scope.row.createTime | moment('YYYY-MM-DD HH:mm:ss') }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column width="120">
                            <template slot-scope="scope">
                                <el-button type="danger" size="mini" title="删除数据" icon="el-icon-delete" @click="handleDel(scope.row.id)"></el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="handle-row">
                        <el-button type="danger" title="删除全部数据" icon="el-icon-delete" @click="handleAllDel" :disabled="read.length == 0">删除全部</el-button>
                    </div>
                </template>
            </el-tab-pane>
            <!-- <el-tab-pane :label="`回收站(${recycle.length})`" name="third">
                    <template v-if="message === 'third'">
                        <el-table :data="recycle" :show-header="false" style="width: 100%">
                            <el-table-column>
                                <template slot-scope="scope">
                                    <span class="message-title">{{scope.row.title}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column prop="date" width="150"></el-table-column>
                            <el-table-column width="120">
                                <template slot-scope="scope">
                                    <el-button @click="handleRestore(scope.$index)">还原</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="handle-row">
                            <el-button type="danger">清空回收站</el-button>
                        </div>
                    </template>
                </el-tab-pane>-->
        </el-tabs>
        <!-- 查看内容 -->
        <el-dialog :title="messageInfo.title" :visible.sync="dialogVisible" width="30%" :before-close="handleSave" :close-on-click-modal="false">
            <span v-text="messageInfo.content"></span>
            <!-- <span slot="footer" class="dialog-footer">
                <el-button type="text" @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="handleSave">确 定</el-button>
            </span> -->
        </el-dialog>
    </div>
</template>

<script>
import bus from '../../components/common/bus';
export default {
    data() {
        return {
            message: 'first',
            showHeader: false,
            // 未读列表
            unread: [],
            // 已读列表
            read: [],
            // 当前页
            pageNum: 1,
            // 每页显示的数量
            pageSize: 999,
            // 控制dialog的显示与隐藏
            dialogVisible: false,
            // 消息内容
            messageInfo: {
                title: '',
                content: ''
            },
            // 获取id，点击标题消息已读
            messageId: ''
        };
    },
    created() {
        this.getMessageList0();
        this.getMessageList1();
    },
    methods: {
        // 获取未读消息列表
        async getMessageList0() {
            const res = await this.$axios.post('sys/message/personList', {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                status: 0,
                subject: '',
                content: ''
            });
            if (res.data.code == 0) {
                this.unread = res.data.data;
            }
        },
        // 获取已读消息列表
        async getMessageList1() {
            const res = await this.$axios.post('sys/message/personList', {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                status: 1,
                subject: '',
                content: ''
            });
            if (res.data.code == 0) {
                this.read = res.data.data;
            }
        },
        async handleToClick(data) {
            if (data.name == 'first') {
                this.getMessageList0();
            } else {
                this.getMessageList1();
            }
        },
        // 标为已读
        async handleRead(id) {
            const res = await this.$axios.post('sys/message/readMessage', { id });
            if (res.data.code == 0) {
                this.getMessageList0();
                this.getMessageList1();
                bus.$emit('getMessageNumber', event.target);
            }
        },
        // 全部标为已读
        async handleAllRead() {
            const res = await this.$axios.post('sys/message/readMessage', { id: '' });
            if (res.data.code == 0) {
                this.getMessageList0();
                this.getMessageList1();
                bus.$emit('getMessageNumber', event.target);
            }
        },
        // 删除单个消息
        handleDel(id) {
            this.$confirm('此操作将永久删除该消息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/message/deleted', { id });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getMessageList0();
                        this.getMessageList1();
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
        // 点击按钮删除全部消息
        handleAllDel() {
            this.$confirm('此操作将永久删除所有消息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('sys/message/deleted', { id: '' });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getMessageList0();
                        this.getMessageList1();
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
        // handleRestore(index) {
        //     const item = this.recycle.splice(index, 1);
        //     this.read = item.concat(this.read);
        // }
        // 点击标题查看
        handleToSee(row) {
            this.messageInfo.title = row.subject;
            this.messageInfo.content = row.content;
            this.dialogVisible = true;
            this.messageId = row.id;
        },
        handleClose() {
            this.dialogVisible = false;
        },
        handleSave() {
            if (this.message == 'first') {
                this.dialogVisible = false;
                this.handleRead(this.messageId);
            }else {
                this.dialogVisible = false;
            }
            // console.log(this.messageId)
            // return;
            // this.dialogVisible = false;
            // this.handleRead(this.messageId);
        }
    },
    computed: {
        unreadNum() {
            return this.unread.length;
        }
    }
};
</script>

<style lang="less" scoped>
.message-title {
    cursor: pointer;
}

.handle-row {
    margin-top: 30px;
}

.message-tabs {
    height: 100%;
    /deep/.el-tabs__content {
        height: calc(~'100% - 55px') !important;
        .el-tab-pane {
            height: 100%;
        }
    }
}
</style>
