发送ajax请求
this.$axios.post('URL',{参数}).then(res=>{
    console.log(res)
}).catch(err=>{
    console.log(err)
})
使用async await语法糖  可以简化promise操作
async handleToClick(){
    const res = await this.$axios.post('URL',{参数})
    console.log(res)
}

文件下载方法
this.$download.downloadFile(fileId,fileName)

按钮权限添加
<el-button type="primary" plain v-has="'权限标识'">按钮权限</el-button> 注意v-has后面是双引号嵌套单引号

时间过滤器使用(更多使用方法参考 http://momentjs.cn/)
{{date | moment('YYYY-MM-DD HH:mm:ss')}}

修改标签导航栏上限个数
components-->common-->Tags-->setTags方法中修改