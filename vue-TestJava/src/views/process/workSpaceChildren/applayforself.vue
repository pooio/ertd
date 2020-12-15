<!--
 * @页面名称：我发起的
 * @Autor: flynn.yang
 * @Version: 1.0
 * @Date: 2020-02-27 10:48:54
 * @LastEditTime: 2020-07-01 16:17:34
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
                <el-select
                    v-model="searchInfo.approvalType"
                    placeholder="请选择审批状态"
                    clearable
                    style="width:100%"
                    @change="changeValue"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    ></el-option>
                </el-select>
            </el-col>
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
                ></el-date-picker>
            </el-col>
            <el-col :span="12">
                <el-button
                    type="primary"
                    title="根据输入条件搜索"
                    icon="el-icon-search"
                    @click="handleToSearch"
                >搜索</el-button>
                <el-button
                    type="primary"
                    plain
                    title="清空搜索条件"
                    icon="el-icon-refresh"
                    @click="getSysInfoListRe"
                >重置</el-button>
            </el-col>
        </el-row>
        <el-table
            :data="tableData"
            style="width: 100%"
            border
            v-loading="loading"
            height="calc(100% - 160px)"
            :row-class-name="tableRowClassName"
        >
            <el-table-column
                :resizable="false"
                :label="$t('i18n.number')"
                align="center"
                type="index"
                width="50"
            >
                <template
                    slot-scope="scope"
                >{{ scope.$index + 1 + searchInfo.pageSize * (searchInfo.pageNum - 1) }}</template>
            </el-table-column>
            <el-table-column prop="processName" label="流程名称" :resizable="false">
                <template slot-scope="scope">
                    <span
                        @click="viewModal(scope.row)"
                        class="bulletinTitle cursorPoint"
                    >{{scope.row.processName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="currNode" label="当前环节" :resizable="false"></el-table-column>
            <el-table-column prop="currAppUserName" label="当前审批人"></el-table-column>
            <el-table-column prop="createTime" label="发起时间" :resizable="false">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime |moment('YYYY-MM-DD HH:mm:ss') }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="endTime" label="完成时间"></el-table-column>
            <el-table-column prop="businessStatueName" label="状态" class-name="statusClass"></el-table-column>
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
        <!-- 审批记录 -->
        <history
            @dialogHide="dialogHanderHide"
            :tableDialog="tableDialog"
            :pic="pic"
            :formKey="formKey"
            v-if="approvalVisible"
        ></history>
    </div>
</template>

<script>
import history from '../pendingChildren/history';
export default {
  components: { history },
  data () {
    return {
      tableDialog: '',
      pic: '',
      formKey: '',
      approvalVisible: false,
      // 查询参数
      searchInfo: {
        // 默认请求第一页
        pageNum: 1,
        // 默认请求10条数据
        pageSize: 10,
        startDate: '',
        // 模块
        endDate: '',
        approvalType: null,
        Date: [],
        processDefinitionName: ''
      },
      options: [
        {
          value: 0,
          label: '待审批'
        },
        {
          value: 1,
          label: '审批中'
        },
        {
          value: 2,
          label: '审批通过 '
        },
        {
          value: 3,
          label: '审批未通过'
        }
      ],
      // 日期
      date: null,
      // 数据列表
      tableData: [],
      loading: true,
      // 数据总数
      total: 0,
      // 下拉框的数据列表
      moduleList: []
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
        // // 获取表单
        //  this.$axios
        // .post('formData/getDataJson', { businessKey : val.businessKey ,businessName:val.businessName })
        // .then(res => {
        //   console.log('666', res);
        //   if (res != null) {
        //     // that.tableDialog = res.data.data;
        //   }
        // })
        // .catch(err => {
        //   // console.info('报错的信息', err);
        // });
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
      this.searchInfo.startDate = this.searchInfo.Date ? this.searchInfo.Date[0] : '';
      this.searchInfo.endDate = this.searchInfo.Date ? this.searchInfo.Date[1] : '';
      const res = await this.$axios.post('allFormApp/findApp', this.searchInfo);

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
    // 点击搜索
    handleToSearch () {
      this.searchInfo.pageNum = 1;
      this.getSysInfoList();
    },
    // 状态不同，颜色高亮
    tableRowClassName ({ row }) {
      if (row.businessStatueName === "待审批") {
        return 'wait-row';
      } else if (row.businessStatueName === "审批中") {
        return 'panding-row';
      } else if (row.businessStatueName === "审批通过") {
        return 'success-row';
      } else if (row.businessStatueName === "审批不通过") {
        return 'fail-row';
      }
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
//审批状态不同显示背景色
/deep/.el-table .panding-row .statusClass {
    color: #409eff;
}
/deep/.el-table .fail-row .statusClass {
    color: #f56c6c;
}
/deep/.el-table .success-row .statusClass {
    color: #67c23a;
}
/deep/.el-table .wait-row .statusClass {
    color: #e6a23c;
}
</style>
