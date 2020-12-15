<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-06 10:48:50
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-11 14:53:38
 * @Description: 404错误页面
 * @Version: 1.0
 -->

<template>
  <div class="error-page">
    <div class="error-code">
      <span>4</span>
      <span>0</span>
      <span>4</span>
    </div>
    <div class="error-desc">你所访问的页面不存在~</div>
    <div class="error-handle">
      <router-link to="/">
        <el-button type="primary" size="large" icon="el-icon-refresh-right">返回首页</el-button>
      </router-link>
      <!-- <el-button class="error-btn" type="primary" size="large" @click="goBack">返回上一页</el-button> -->
    </div>
  </div>
</template>

<script>
export default {
    methods: {
        goBack() {
            this.$router.go(-1);
        }
    }
};
</script>

<style lang="less" scoped>
.error-page {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 100%;
    height: 100%;
    background: #f3f3f3;
    box-sizing: border-box;
}
.error-code {
    line-height: 1;
    font-size: 250px;
    font-weight: bolder;
    color: #7579e7;
}
.error-code span:nth-child(2) {
    color: #9ab3f5;
}
.error-desc {
    font-size: 30px;
    color: #777;
}
.error-handle {
    margin-top: 30px;
    padding-bottom: 200px;
}
.error-btn {
    margin-left: 100px;
}
</style>
