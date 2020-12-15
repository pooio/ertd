<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-08 17:54:07
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-08 17:55:09
 * @Description: 公告管理
 * @Version: 1.0
 -->
<template>
  <div class="box">
    <el-row>
      <el-row :gutter="10">
        <el-col :span="4">
          <el-input v-model="searchInput" placeholder="请输入公告标题" clearable @keyup.enter.native="enterSearch" id="system-bulletin-searchInput"></el-input>
        </el-col>
        <el-col :span="20">
          <el-button type="primary" title="根据输入条件搜索" icon="el-icon-search"  @click="handleToSearch" id="system-bulletin-search">搜索</el-button>
          <el-button type="primary" plain  title="清空搜索条件" icon="el-icon-refresh"   @click="handleToReset" id="system-bulletin-reset">重置</el-button>
          <el-button type="primary" title="新增公告" icon="el-icon-plus"  @click="$refs.add.openDialog(select)" v-has="'system-bulletin-add'" id="system-bulletin-add">新增</el-button>
        </el-col>
      </el-row>
    </el-row>

    <el-table :data="tableData" style="width: 100%" height="calc(100% - 110px)" border v-loading="loading">
      <el-table-column label="序号" align="center" type="index" width="50">
        <template slot-scope="scope">
          {{ scope.$index + 1 + pageSize * (pageNum - 1) }}
        </template>
      </el-table-column>
      <el-table-column prop="bulletinTitle" label="公告标题">
          <template slot-scope="scope">
           <div class="bulletinTitle" style="cursor: pointer;" @click="bulletinTitleClick(scope.row)">{{scope.row.bulletinTitle}}</div>
        </template>
      </el-table-column>
      <el-table-column prop="receiveNo" label="接收人数" width="80px"></el-table-column>
      <el-table-column prop="readNo" label="已读人数" width="80px"></el-table-column>
      <!-- <el-table-column label="发布状态" width="80px"><template slot-scope="scope">{{ scope.row.publishState == 1 ? '已发布' : '未发布' }}</template></el-table-column> -->
      <el-table-column prop="closeDate" label="截止时间" width="140px"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="140px"></el-table-column>
      <el-table-column prop="createName" label="创建人" width="100px"></el-table-column>
   <!--   <el-table-column label="启用状态" width="80px">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.state" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)"></el-switch>
        </template>
      </el-table-column>-->
      <el-table-column :resizable="false" label="发布" width="120">
            <template slot-scope="scope">
                <el-tooltip :content="scope.row.publishState==0?'未发布':'已发布'" placement="right">
                    <el-switch v-model="scope.row.publishState" :active-value="1" :inactive-value="0" @change="handleToChange(scope.row)" id="system-bulletin-publishState" :disabled="switchIsDisabled"></el-switch>
                </el-tooltip>
            </template>
        </el-table-column>
      <el-table-column label="操作" width="300px">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" title="编辑数据" icon="el-icon-edit"  v-if="scope.row.publishState != 1" @click="$refs.edit.openDialog(scope.row, select, roleData, departmentData)" v-has="'system-bulletin-edit'" id="system-bulletin-edit">编辑</el-button>
          <el-button size="mini" type="success" title="查看详情" icon="el-icon-view"   @click="getSendList(scope.row.bulletinPk)" v-has="'system-bulletin-check'" id="system-bulletin-check">查看</el-button>
          <!-- <el-button size="mini" type="warning" icon="el-icon-sort-up"  @click="handleToFb(scope.row)" v-if="scope.row.publishState != 1" v-has="'system-bulletin-publish'" id="system-bulletin-publish">发布</el-button>
          <el-button size="mini" type="info" icon="el-icon-sort-down"  @click="handleToUnFb(scope.row)" v-if="scope.row.publishState == 1"  v-has="'system-bulletin-unpublish'" id="system-bulletin-unpublish">撤销</el-button> -->
          <el-button size="mini" type="danger" title="删除数据"  icon="el-icon-delete"  @click="handleToDel(scope.row)" v-has="'system-bulletin-del'" id="system-bulletin-del">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
    <!-- 新增区域的子组件 -->
    <Add ref="add" @getTableData="getTableData"></Add>
    <!-- 编辑区域的子组件 -->
    <Edit ref="edit" @getTableData="getTableData"></Edit>
    <!-- 查看详情区域的dialog -->
    <el-dialog title="查看详情" :visible.sync="dialogVisible" width="30%" :before-close="handleClose" :close-on-click-modal="false">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="已读详情" name="R">
          <el-table :data="RTableData" style="width: 100%" border height="250">
            <el-table-column type="index" label="序号" width="80"> </el-table-column>
            <el-table-column prop="LOGIN_NAME" label="接收人"> </el-table-column>
            <el-table-column prop="create_time" label="查看时间"> </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="未读详情" name="U">
          <el-table :data="RTableData" style="width: 100%" border height="250">
            <el-table-column type="index" label="序号" width="80"> </el-table-column>
            <el-table-column prop="LOGIN_NAME" label="接收人"> </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <!-- 查看公告 -->
    <bulletinInfo ref="bulletinInfo" :content="content"></bulletinInfo>
  </div>
</template>

<script>
import Add from './children/add';
import Edit from './children/edit';
import bulletinInfo from '../../../components/common/bulletinInfo';
export default {
    // 注册子组件
    components: { Add, Edit,bulletinInfo },
    data() {
        return {
            // 表格绑定的数据
            tableData: [],
            // 当前页
            pageNum: 1,
            // 每页显示的数量
            pageSize: 10,
            // 总条数
            total: 0,
            // 选择器绑定的数据
            select: [],
            // 个人信息
            roleData: [],
            // 搜索条件
            searchInput: '',
            // 部门信息
            departmentData: [],
            // 控制dialog的显示与隐藏
            dialogVisible: false,
            // 当前选中的tab页签
            activeName: 'R',
            // 已读table数据
            RTableData: [],
            // 未读table数据
            // UTableData: [],
            // id
            bulletinPk: '',
            loading: false,
            //switch开关权限
            switchIsDisabled: true,
            content:'',//公告详情
        };
    },
    created() {
        //switch开关权限
        this.switchDisabled('system-bulletin-publish');
        this.getTableData();
        this.getDictList();
        this.getRoleData();
        this.getDepartmentData();
    },
    methods: {
        // 获取表格数据
        async getTableData() {
            this.loading = true;
            const res = await this.$axios.post('bulletinInfo/getList', {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                bulletinTitle: this.searchInput
            });
            if (res.data.code == 0) {
                this.tableData = res.data.data;
                this.total = res.data.total;
                this.loading = false;
            } else {
                this.loading = false;
            }
        },
        // 点击按钮搜索
        handleToSearch() {
            this.loading = true;
            this.pageNum = 1;
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
            this.loading = true;
            this.searchInput = '';
            this.pageNum = 1;
            this.pageSize = 10;
            this.getTableData();
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
        // 获取选择器数据
        async getDictList() {
            const res = await this.$axios.post('sys/dictInfo/getDictList', { dicCode: 'BULLETIN_TYPE' });
            if (res.data.code == 0) {
                this.select = res.data.data;
            }
        },
        // 标题禁用or启用
        async handleSetInUse(data) {
            const res = await this.$axios.post('bulletinInfo/setInUse', {
                inUse: data.state,
                bulletinId: data.bulletinPk
            });
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
            }
        },
        // 点击按钮删除
        handleToDel(data) {
            this.$confirm('确定删除该公告吗？此操作不能撤销！', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post('bulletinInfo/doDel', { bulletinIds: data.bulletinPk });
                    if (res.data.code == 0) {
                        this.$message.success('删除成功');
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.msg}`);
                    }
                })
                .catch(() => {
                    this.$message.info('已取消删除');
                });
        },
        // 发布
        // async handleToFb(data) {
        //     const res = await this.$axios.post('bulletinInfo/toPublish', {
        //         bulletinId: data.bulletinPk
        //     });
        //     if (res.data.code == 0) {
        //         this.$message.success(`${res.data.data}`);
        //         this.getTableData();
        //     } else {
        //         this.$message.error(`${res.data.data}`);
        //     }
        // },
        // // 取消发布
        // async handleToUnFb(data) {
        //     const res = await this.$axios.post('bulletinInfo/toUnPublish', {
        //         bulletinId: data.bulletinPk
        //     });
        //     if (res.data.code == 0) {
        //         this.$message.success(`${res.data.data}`);
        //         this.getTableData();
        //     } else {
        //         this.$message.error(`${res.data.data}`);
        //     }
        // },
        async handleToChange(data) {
            console.log(data);
            var str = data.publishState==0?"撤消发布":"发布";
            var url = data.publishState==0?"bulletinInfo/toUnPublish":"bulletinInfo/toPublish";
            this.$confirm('确定'+str+'吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(async () => {
                    const res = await this.$axios.post(url, { bulletinId: data.bulletinPk });
                    if (res.data.code == 0) {
                        this.$message.success(`${res.data.data}`);
                        this.getTableData();
                    } else {
                        this.$message.error(`${res.data.data}`);
                        data.publishState = data.publishState==0?1:0
                    }
                })
                .catch(() => {
                    data.publishState = data.publishState==0?1:0
                });
        },
        // 获取所有用户数据
        async getRoleData() {
            const res = await this.$axios.post('/sys/user/listAll');
            if (res.data.code == 0) {
                this.roleData = res.data.data;
            }
        },
        // 获取所有部门数据
        async getDepartmentData() {
            const res = await this.$axios.post('sys/org/treeData');
            if (res.data.code == 0) {
                this.departmentData = res.data.data;
            }
        },
        // 获取已读/未读人员列表
        async getSendList(bulletinPk, isRead) {
            this.bulletinPk = bulletinPk;
            const res = await this.$axios.post('bulletinInfo/getSendList', { bulletinPk: this.bulletinPk, isRead: this.activeName });
            if (res.data.code == 0) {
                this.RTableData = res.data.data;
                this.dialogVisible = true;
            }
        },
        // dialog关闭的回调
        handleClose() {
            this.dialogVisible = false;
            this.activeName = 'R';
        },
        // tab页签点击的回调
        handleClick(data) {
            this.getSendList(this.bulletinPk, data);
        },
         //input回车搜索
        enterSearch(){
            this.handleToSearch();
        },
        switchDisabled(item){
            var permissionList = sessionStorage.getItem('btnContext');
            if(permissionList!=null){
                if(permissionList.split(',').indexOf(item)>-1){
                    this.switchIsDisabled = false;
                }
            }
        },
        // 公告标题-进入详情
        async bulletinTitleClick(data){
            this.content = data;
            this.$refs.bulletinInfo.openDialog()
            const res = await this.$axios.post('bulletinInfo/toBulletinInfo', {
                sendPk: data.sendPk
            });
            console.log(res);
        }
    }
};
</script>

<style lang="less" scoped>
.el-col {
    margin-bottom: 20px;
}
.el-pagination {
    text-align: center;
    margin-top: 20px;
}
</style>
