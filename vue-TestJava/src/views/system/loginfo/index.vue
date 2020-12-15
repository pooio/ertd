<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:04:34
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:05:38
 * @Description: 日志管理
 * @Version: 1.0
 -->
 
<template>
  <div class="box">
    <!-- 表格区域 -->
    <el-row :gutter="10">
      <!-- 搜索框 -->
      <el-col :span="4">
        <el-input v-model="searchInfo.userName" placeholder="请输入操作人" clearable  @keyup.enter.native="enterSearch"></el-input>
      </el-col>
      <!-- 日期选择器 -->
      <el-col :span="4">
        <el-date-picker v-model="date" type="daterange" range-separator="—" start-placeholder="开始日期" end-placeholder="结束日期" size="small" value-format="yyyy-MM-dd" format="yyyy-MM-dd" style="width:100%" :editable="false"></el-date-picker>
      </el-col>
      <!-- 下拉框 -->
      <el-col :span="4">
        <el-select v-model="searchInfo.module" placeholder="请选择模块" clearable style="width:100%" @change="changeValue">
          <el-option v-for="(item,index) in moduleList" :key="index" :label="item.value" :value="item.value"></el-option>
        </el-select>
      </el-col>
      <el-col :span="12">
        <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search" @click="handleToSearch">搜索</el-button>
        <el-button type="primary" plain title="清空搜索条件" icon="el-icon-refresh" @click="getSysInfoListRe">重置</el-button>
      </el-col>
    </el-row>

    <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
      <el-table-column label="序号" align="center" type="index" width="50">
        <template slot-scope="scope">
          {{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" :resizable="false"></el-table-column>
      <el-table-column prop="createTime" label="时间" :resizable="false"></el-table-column>
      <el-table-column prop="module" label="模块" :resizable="false"></el-table-column>
      <el-table-column prop="info" label="信息" :resizable="false"></el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="searchInfo.pageNum" :page-size="searchInfo.pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
  </div>
</template>

<script>
export default {
    data() {
        return {
            // 查询参数
            searchInfo: {
                // 默认请求第一页
                pageNum: 1,
                // 默认请求10条数据
                pageSize: 10,
                // 操作人
                userName: '',
                // 模块
                module: '',
                // 开始时间
                startDate: '',
                // 结束时间
                endDate: ''
            },
            // 日期
            date: null,
            // 日志数据列表
            tableData: [],
            // 数据总数
            total: 0,
            // 下拉框的数据列表
            moduleList: [],
            loading: true
        };
    },
    created() {
        // 获取模块数据列表
        this.getModuleList();
        // 获取日志数据列表
        this.getSysInfoList();
    },
    methods: {
        /**
         * @Author: Misaka.chen
         * @description: 获取模块数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getModuleList() {
            const res = await this.$axios.post('sys/info/getModuleList');
            if (res.data.code == 0) {
                this.moduleList = res.data.data;
            }
        },
        /**
         * @Author: Misaka.chen
         * @description: 获取日志数据列表
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        async getSysInfoList() {
            this.loading = true;
            if (this.date != null) {
                this.searchInfo.startDate = this.date[0];
                this.searchInfo.endDate = this.date[1];
            } else {
                this.searchInfo.startDate = '';
                this.searchInfo.endDate = '';
            }
            const res = await this.$axios.post('sys/info/getSysInfoList', this.searchInfo);
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
            // console.log(res);
        },
        /**
         * @Author: flynn.yang
         * @description: 重置
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        getSysInfoListRe() {
            this.searchInfo.pageNum = 1;
            this.searchInfo.pageSize = 10;
            this.searchInfo.userName = '';
            this.searchInfo.module = '';
            this.date = [];

            this.getSysInfoList();
        },
        /**
         * @Author: Misaka.chen
         * @description: 分页--每页显示几条数据
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleSizeChange(pageSize) {
            this.searchInfo.pageSize = pageSize;
            this.getSysInfoList();
        },
        /**
         * @Author: Misaka.chen
         * @description: 分页--当前显示第几页
         * @param {type}
         * @return:
         * @Version: 1.0
         */
        handleCurrentChange(pageNum) {
            this.searchInfo.pageNum = pageNum;
            this.getSysInfoList();
        },
        // 点击搜索
        handleToSearch() {
            this.searchInfo.pageNum = 1;
            this.getSysInfoList();
        }, //input回车搜索
        enterSearch() {
            this.handleToSearch();
        },
        // 模块change事件
        changeValue() {
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
    margin-top: 20px;
    text-align: center;
}
</style>
