<template>
    <div class="detailsDialog">
        <el-dialog
            title="公告"
            :visible.sync="dialogVisible"
            width="30%"
            :close-on-click-modal="false"
        >
            <span slot="title" class="dialog-footer">
                <div class="gonggao">公告</div>
                <div class="biaoti">{{content.bulletinTitle}}</div>
            </span>
            <!-- 新闻信息 -->
            <div class="info">
                <span>{{content.bulletinName}}</span>
                <span style="color: #ccc;">{{content.createName}}</span>
                <span style="color: #ccc;">{{content.createTime}}</span>
            </div>
            <!-- 内容 -->
            <div class="content" v-html="content.bulletinNote"></div>
            <div class="enclosure" @click="handleToEnclosure">
                附件：
                <span>{{content.fileName == null ? '暂无' : content.fileName}}</span>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    props: {
        // 公告内容
        content: '',
    },
    data() {
        return {
            dialogVisible:false
        };
    },
    created() {},
    methods: {
        openDialog() {
            this.dialogVisible = true;
        },
        // 点击查看附件
        handleToEnclosure() {
            if (this.content.url) {
                this.$download.downloadFile('download/downloadFile', { fileId: this.content.fileId }, this.content.fileName)
            } else {
                return false;
            }
        },
    },
    watch: {}
};
</script>

<style lang="less" scoped>
@import '../../assets/css/style.css';
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
.enclosure {
    margin-top: 20px;
    cursor: pointer;

    span {
        color: #408eff;
    }
}
</style>
