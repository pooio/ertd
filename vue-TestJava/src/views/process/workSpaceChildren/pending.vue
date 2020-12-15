<!--
 * @页面名称：待我审批
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-27 10:48:54
 * @LastEditTime: 2020-07-01 16:17:41
 * @Autor:serena.li
 * @Version: 1.0
 * @Desc: 规范修改文件名称，增加表格的loading属性，删除冗余文件
 * @Date: 2020-07-2 10:48:54
 * @LastEditTime: 2020-07-02 16:17:41
 -->
<template>
    <div class="box">
        <!-- 表格区域 -->
        <el-row :gutter="10">
            <el-col :span="4">
                <el-input v-model.trim="searchInfo.processDefinitionName" placeholder="请输入流程名称" clearable @keyup.enter.native="changeValue" id="workSpace-name"></el-input>
            </el-col>
            <el-col :span="4">
                <el-date-picker
                    style="width:100%"
                    v-model="searchInfo.Date"
                    type="daterange"
                    range-separator="—"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    placeholder="选择日期"
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    id="workSpace-date"
                ></el-date-picker>
            </el-col>
            <el-col :span="16">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                    id="workSpace-search"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="getSysInfoListRe"
                    id="workSpace-reset"
                >重置</el-button>
            </el-col>
        </el-row>
        <el-table :data="tableData" height="calc(100% - 160px)" style="width: 100%" border v-loading="loading">
            <el-table-column
                :resizable="false"
                :label="$t('i18n.number')"
                align="center"
                type="index"
                width="50"
            >
                <template slot-scope="scope">{{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}</template>
            </el-table-column>
            <el-table-column prop="processDefinitionName" label="流程名称" :resizable="false">
                <template slot-scope="scope">
                    <span
                        @click="viewModal(scope.row)"
                        class="bulletinTitle cursorPoint"
                    >{{scope.row.processDefinitionName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="name" label="当前环节" :resizable="false"></el-table-column>
            <el-table-column prop="assigneeName" label="当前审批人" :resizable="false"></el-table-column>
            <el-table-column prop="createTime" label="发起时间" :resizable="false">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100px">
                <template slot-scope="scope">
                    <el-button
                        @click="approve(scope.row)"
                        type="primary"
                        title="审批申请单"
                        icon="el-icon-success"
                        size="mini"
                    >审批</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页区域 -->
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchInfo.pageNum"
            :page-size="searchInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
        ></el-pagination>
        <!-- 审批 -->
        <el-dialog title="审批" :visible.sync="addDialogFormVisible" width="900px">
            <add @dialog="dialogForm" v-bind:editDataProp="editData" v-if="addDialogFormVisible"></add>
        </el-dialog>
        <!-- 审批记录 -->

        <history
            @dialogHide="dialogHanderHide"
            :tableDialog="tableDialog"
            :pic="pic"
            v-if="approvalVisible"
            :formKey="formKey"
        ></history>
       
    </div>
</template>

<script>
import add from './approve';
import history from '../pendingChildren/history';

export default {
  components: { 
    add,
   history ,
   },
  data () {
    return {
      options: [
        {
          value: 0,
          label: '待审批'
        },
        {
          value: 1,
          label: '审批中'
        }
      ],
      addDialogFormVisible: false,

      tableDialog: [],
      pic: '',
      approvalVisible: false,
      editData: {},
      // 查询参数
      searchInfo: {
        // 默认请求第一页
        pageNum: 1,
        // 默认请求10条数据
        pageSize: 10,
        // 操作人
        startDate: null,
        // 模块
        endDate: null,
        approvalType: null,
        Date: [],
        type: 1,
        processDefinitionName: ''
      },
      // 日期
      date: null,
      // 数据列表
      tableData: [],
      loading: true,
      // 数据总数
      total: 0,
      // 下拉框的数据列表
      moduleList: [],
      formKey:"",//form表单key
    };
  },
  created () {
    // 获取数据列表
    this.getSysInfoList();
  },
  methods: {
    /**
     * @Author: flynn.yang
     * @description:查看审批记录
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    viewModal (val) {
      console.log(val);
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
        .then(res => {
          return (
            'data:image/png;base64,' + btoa(new Uint8Array(res.data).reduce((data, key) => data + String.fromCharCode(key), ''))
          );
        })
        .then(data => {
          //这一次箭头函数是依赖于第一次.then函数处理的data值
          console.log(data);
          var oStu = document.getElementById('images');
          oStu.setAttribute('src', data);
        });
      this.$axios
        .post('bpm/instance/listHistoricTasks', { processInstanceId: val.processInstanceId })
        .then(res => {
          console.log('555', res);
          if (res != null) {
            that.tableDialog = res.data.data;
          }
        })
        .catch(err => {
          console.info('报错的信息', err);
        });
    },
    /**
     * @Author: flynn.yang
     * @description:审批记录回调
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    dialogHanderHide () {
      this.approvalVisible = false;
    },
    /**
     * @Author: flynn.yang
     * @description: 获取日志数据列表
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    async getSysInfoList () {
      // if (this.searchInfo.Date) {
      this.searchInfo.startDate = this.searchInfo.Date ? this.searchInfo.Date[0] : '';
      this.searchInfo.endDate = this.searchInfo.Date ? this.searchInfo.Date[1] : '';
      // } else {
      //     this.searchInfo.startDate = '';
      //     this.searchInfo.endDate = '';
      // }

      const res = await this.$axios.post('bpm/task/todo/listPersonTasks', this.searchInfo);
      if (res.data.code == 0) {
        this.tableData = res.data.data.data;
        this.total = res.data.data.total;
      }
      this.loading = false;
      // console.log(res);
    },
    /**
     * @Author: flynn.yang
     * @description: 重置
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    getSysInfoListRe () {
      this.searchInfo.pageNum = 1;
      this.searchInfo.pageSize = 10;
      this.searchInfo.startDate = '';
      this.searchInfo.endDate = '';
      this.searchInfo.approvalType = null;
      this.searchInfo.processDefinitionName = '';
      this.searchInfo.Date = [];

      this.getSysInfoList();
    },
    /**
     * @Author: flynn.yang
     * @description: 分页--每页显示几条数据
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    handleSizeChange (pageSize) {
      this.searchInfo.pageNum = 1;
      this.searchInfo.pageSize = pageSize;
      this.getSysInfoList();
    },
    /**
     * @Author: flynn.yang
     * @description: 分页--当前显示第几页
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    handleCurrentChange (pageNum) {
      this.searchInfo.pageNum = pageNum;
      this.getSysInfoList();
    },
    /**
     * @Author: flynn.yang
     * @description:点击搜索
     * @param {type}
     * @return:
     * @Version: 1.0
     */
    handleToSearch () {
      this.searchInfo.pageNum = 1;
      this.getSysInfoList();
    },
    /**
     * @Author: flynn.yang
     * @description:点击审批
     * @param {type}
     * @return:
     * @Version: 1.0
     */

    approve (row) {
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
    dialogForm (data) {
      this.addDialogFormVisible = data;
      this.getSysInfoList();
    },
    // 审批状态change事件
    changeValue () {
      this.handleToSearch();
    }
  }
};
</script>

<style lang="less" scoped>
@import '../../../assets/css/style.css';
.el-table {
    margin-top: 20px;
}
.el-pagination {
    margin-top: 20px;
    text-align: center;
}
.el-range-editor {
    width: 100%;
}
.el-select {
    width: 100%;
}
</style>
