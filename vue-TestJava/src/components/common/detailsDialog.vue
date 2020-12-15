<template>
   <div class="detailsDialog">
     <el-dialog title="提示" :visible.sync="dialogVisible" width="50%">
        <span slot="title" class="dialog-footer">
            <div class="biaoti">{{content.kbName}}</div>
        </span>
        <div>
            <div class="info">
                <span>
                    分类:
                    <span v-text="content.kbTypeName"></span>
                </span>
                <span>
                    作者:
                    <span v-text="content.createName"></span>
                </span>
                <span>
                    创建时间:
                    <span v-text="content.createTime"></span>
                </span>
                <span>
                    关键字:
                    <span v-text="content.keyWord"></span>
                </span>
            </div>
            <div style="overflow:hidden" v-html="content.kbContent"></div>
        </div>
    </el-dialog>
   </div>
</template>

<script>
export default {
  props: {
      content:"",//详情数据
  },
  data () {
    return {
       dialogVisible:false,
    };
  },
  created () {
  },
  methods: {
    openDialog(){
        this.dialogVisible = true;
    }
  },
  watch: {

  }
};
</script>

<style lang="less" scoped>
@import '../../assets/css/style.css';
/deep/.el-dialog__header {
    border-bottom: 2px solid rgba(229, 229, 229, 1);
}
</style>
