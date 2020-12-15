<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:55:01
 * @Description: 岗位管理
 * @Version: 1.0
 -->
<template>
  <div class="box">
    <!-- 表格区域 -->
    <el-row :gutter="10">
      <el-col :span="4">
        <el-input v-model="searchInput" placeholder="请输入公告标题" clearable @keyup.enter.native="enterSearch" id="notice-searchInput"></el-input>
      </el-col>
      <el-col :span="12">
        <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch" id="notice-search">搜索</el-button>
        <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="handleToReset" id="notice-reset">重置</el-button>
      </el-col>
      <el-col :span="8" style="text-align: right;">
        <el-button
            type="primary"
            @click="$router.push({path:'/'})"
            id="system-permission-back"
            icon="el-icon-back"
        >返回</el-button>
      </el-col>
    </el-row>

    <el-table :data="tableData" style="width: 100%" border>
      <el-table-column :resizable="false" label="序号" align="center" type="index" width="50">
        <template slot-scope="scope">
          {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
        </template>
      </el-table-column>
      <el-table-column :resizable="false" prop="bulletinTitle" label="公告标题">
        <template slot-scope="scope">
          <div class="bulletinTitle el-tag__close" style="cursor: pointer;" @click="handleToSend(scope.row)" id="notice-bulletinTitle">{{scope.row.bulletinTitle}}</div>
        </template>
      </el-table-column>
      <el-table-column :resizable="false" prop="bulletinName" label="类型"></el-table-column>
      <el-table-column :resizable="false" prop="createTime" label="发布时间"></el-table-column>
      <el-table-column :resizable="false" prop="createName" label="创建人"></el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
    <!-- 查看公告 -->
    <el-dialog title="查看公告" :visible.sync="dialogVisible" width="30%" :before-close="handleClose" :close-on-click-modal="false">
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
        附件：<span>{{content.fileName == null ? '暂无' : content.fileName}}</span>
      </div>
      <!-- <span slot="footer" class="dialog-footer">
                <el-button type="text" @click="handleClose">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span> -->
    </el-dialog>
  </div>
</template>

<script>
export default {
    data() {
        return {
            // 搜索条件
            searchInput: '',
            // 表格绑定的数据
            tableData: [],
            // 当前页
            pageNum: 1,
            // 每页显示的数量
            pageSize: 10,
            // 总条数
            total: 0,
            // 控制dialog的显示与隐藏
            dialogVisible: false,
            // 公告内容
            content: ''
        };
    },
    created() {
        this.getTableData();
    },

    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取表格数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getTableData() {
            let info = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                bulletinTitle: this.searchInput
            };
            const res = await this.$axios.post('bulletinInfo/getUserBulletinInfoList', info);

            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换每页显示的数量
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        handleSizeChange(newSize) {
            this.pageSize = newSize;
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击切换当前页
         * @param {type}
         * @return:
         * @Version: 1.0
         */

        handleCurrentChange(newNum) {
            this.pageNum = newNum;
            this.getTableData();
        },
        /**
         * @Author: Misaka.chen
         * @description: 点击按钮重置input
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleToReset() {
            this.searchInput = '';
            this.getTableData();
        },
        // 点击按钮搜索
        handleToSearch() {
            this.pageNum = 1;
            this.getTableData();
        },
        // 点击查看公告
        async handleToSend(data) {
            // this.content = data.bulletinNote;
            this.content = data;
            this.dialogVisible = true;
            const res = await this.$axios.post('bulletinInfo/toBulletinInfo', {
                sendPk: data.sendPk
            });
        },
        // diaolog关闭回调
        handleClose() {
            this.dialogVisible = false;
        },
        // 点击查看附件
        async handleToEnclosure() {
            if (this.content.fileId) {
                // window.open(this.content.url);
                // const res = await this.$axios.post('download/downloadFile',{
                //     fileId:this.content.fileId
                // })
                // console.log(res);
                // console.log(this.$download);
                this.$download.downloadFile(this.content.fileId, this.content.fileName);
            } else {
                return false;
            }
        },
        //input回车搜索
        enterSearch() {
            this.handleToSearch();
        }
    }
};
</script>

<style lang="less" scoped>
.el-table {
    margin-top: 20px;
}

.el-pagination {
    text-align: center;
    margin-top: 20px;
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

.dialog-footer {
    display: flex;
    margin-bottom: 2px solid #ccc;
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

/deep/.el-dialog__body {
    padding-top: 5px;
}

.info {
    border-top: 2px solid #ccc;
    text-align: center;

    span {
        display: inline-block;
        margin: 10px 5px 0;
    }
}
.bulletinTitle:hover {
    color: #409efe;
    font-weight: 700;
}
</style>