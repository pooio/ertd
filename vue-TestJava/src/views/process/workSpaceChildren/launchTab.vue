<template>
    <!-- 发起页面-->
    <div>
        <ul class="launchList">
            <li v-for="item in launchList" :key="item.id" @click="toFormFill(item)" :title="item.businessName" class="bulletinTitle">
                <i :class="item.icon==null?'el-icon-grape':item.icon" :style="{backgroundColor:item.color ? item.color : '#408eff'}"></i>
                <span>{{item.businessName}}</span>
            </li>
            <!-- <li class="launch" v-for="item in launchList" :key="item.id" @click="toFormFill(item.id)">
                <img src="@/assets/img/headImg.png" alt />
                <span :title="item.businessName">{{item.businessName}}</span>
            </li> -->
        </ul>
        <!-- 填写 -->
        <el-dialog title="填写" :visible.sync="fillVisible" width="900px">
            <!-- <fill @dialogFill="dialogFormFill" v-bind:data="formId" v-if="fillVisible"></fill> -->
            <formFill
                @dialogFill="dialogFormFill"
                v-bind:data="formData"
                v-bind:formId="formId"
                v-if="fillVisible"
            ></formFill>
        </el-dialog>
    </div>
</template>

<script>
import formFill from '../../customerForm/children/formFill';
export default {
    components: { formFill },
    props: {},
    data() {
        return {
            // 发起数据
            launchList: [],
            fillVisible: false,
            formId: '',
            formData: {}
        };
    },
    created() {
        this.getLaunchList();
    },
    mounted() {},
    methods: {
        // 发起数据
        async getLaunchList() {
            const res = await this.$axios.post('form/getCustomFormList', {
                pageNum: 1,
                pageSize: 999,
                type:1
            });
            if (res.data.code == 0) {
                this.launchList = res.data.data;
            }
        },
        // 发起跳转自定义表单填写页面
        toFormFill(item) {
            this.formData = item;
            this.formId = item.id;
            this.fillVisible = true;
            // this.$router.push({ path: 'form-fill', query: { data: item } });
        },
        dialogFormFill(){
          this.fillVisible = false;
        },
    }
};
</script>

<style lang="less" scoped>
// 发起
.launchList li{
  float: left;
  width: 20%;
  cursor: pointer;
  list-style: none;
  margin: 13px 0;
}
.launchList li i{
  float: left;
  width: 32px;
  height: 32px;
  border-radius:32px;
  background-color: #408eff;
  color: #fff;
  text-align: center;
  line-height: 32px;
  font-size: 20px;
}
.launchList li span{
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
