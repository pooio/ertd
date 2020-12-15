<!--
 * @Author: Misaka.chen
 * @Date: 2020-01-11 16:12:32
 * @LastEditors  : Misaka.chen
 * @LastEditTime : 2020-01-18 16:46:20
 * @Description: 菜单授权的表单
 * @Version: 1.0
 -->
 <template>
    <el-row :gutter="10" class="box" style="border:1px solid #ccc;">
        <!-- 下拉框 -->
        <el-col :span="6" style="border-right:1px solid #ccc;" class="box" >
            <div style="text-align:center;line-height:40px;">菜单授权</div>
            <el-scrollbar style="height:calc(100% - 100px);">
                <el-tree
                    id="system-role-menu-tree"
                    :data="menuData"
                    show-checkbox
                    node-key="id"
                    :default-expand-all="true"
                    :props="defaultProps"
                    ref="menutree"
                    @check-change="handleTocheckBtn"
                ></el-tree>
            </el-scrollbar>
            <!-- <div style="text-align:center;line-height:40px;">
                <el-button id="system-role-menu-save" type="primary" @click="handleToSaveMenu">确 定</el-button>
            </div> -->
            
        </el-col>
        <el-col :span="6"  class="box" >
            <div style="text-align:center;line-height:40px;">按钮授权</div>
            <el-scrollbar style="height:calc(100% - 100px);">
                <el-tree
                    id="system-role-btn-tree"
                    :data="btnData"
                    show-checkbox
                    node-key="id"
                    :default-expand-all="true"
                    :props="defaultProps"
                    ref="btntree"
                ></el-tree>
            </el-scrollbar>
            <div style="text-align:center;line-height:40px;">
                <el-button id="system-role-btn-save" type="primary" @click="handleToSaveBtn">确 定</el-button>
            </div>
        </el-col>
        <el-col :span="8" style="padding:20px;float:right;" class="box" >
            <el-card class="box-card">
                <div slot="header" class="clearfix">
                    <span>说明：</span>
                </div>
                <div style="line-height:30px;">
                    <p>权限分配分为两级：</p>
                    <p>1、【菜单授权】为角色分配可访问的功能菜单</p>
                    <p>2、【按钮授权】为可访问的功能设置可进行操作权限</p>
                </div>
            </el-card>
        </el-col>
    </el-row>
</template>

<script>
export default {
    props: {
        roleId: ''
    },
    data() {
        return {
            // 菜单树数据
            menuData: [],
            btnData: [],
            // tree树形控件的配置项
            defaultProps: {
                children: 'children',
                label: 'name'
            },
            // 当前角色拥有的菜单权限
            checkedKeys: [],
            // 当前角色拥有的按钮权限
            checkedBtnKeys: []
        };
    },
    created() {},
    mounted() {
        // this.getRoleResource(this.roleId);
        // 获取菜单树
        console.log(this.roleId);
        this.getTreeData();
    },
    methods: {
        // 获取菜单树
        async getTreeData() {
            let info = {
                roleId: 1,
                type: 2
            };
            const res = await this.$axios.post('sys/menu/treeData', info);
            if (res.data.code == 0) {
                this.menuData = res.data.data.children;
                this.getRoleResource(this.roleId);
            }
            const res2 = await this.$axios.post('sys/menu/btnTreeData', {roleId: this.roleId});
            if (res2.data.code == 0) {
                this.btnData = res2.data.data.children;
                this.getRoleBtn(this.roleId);
            }
        },

        // 获取当前角色拥有的菜单权限
        async getRoleResource(id) {
            this.checkedKeys = [];
            const res = await this.$axios.post('sys/role/getRoleResource', {
                id
            });
            if (res.data.code == 0) {
                this.checkedKeys = res.data.data.split(',');
                // 通过keys设置复选框的选中状态
                this.$refs.menutree.setCheckedKeys(this.checkedKeys);
            }
        },
        // 获取当前角色拥有的按钮权限
        async getRoleBtn(id) {
            this.checkedBtnKeys = [];
            const res = await this.$axios.post('sys/menu/getRoleBtn', {
                id
            });
            if (res.data.code == 0) {
                this.checkedBtnKeys = res.data.data.split(',');
                // 通过keys设置复选框的选中状态
                this.$refs.btntree.setCheckedKeys(this.checkedBtnKeys);
            }
        },
        async handleTocheckBtn(val, flag, child){
            let arr = this.$refs.menutree.getCheckedNodes(false,true);
            
            let ids = [];
            arr.forEach(item => {
                ids.push(item.id);
            });
            ids = ids.join(',');
            const res = await this.$axios.post('sys/menu/getBtnTree', {
                menuIds: ids
            });
            if (res.data.code == 0) {
                console.log(res.data.data);
                this.btnData = res.data.data.children;
                this.getRoleBtn(this.roleId);
            }
        },

        // 点击按钮保存
        async handleToSaveMenu() {
            let arr = this.$refs.menutree.getCheckedNodes(false,true);
            console.log(arr);
            let ids = [];
            arr.forEach(item => {
                ids.push(item.id);
            });
            ids = ids.join(',');
            const res = await this.$axios.post('sys/role/addMenusForRole', {
                roleId: this.roleId,
                menuIds: ids
            });
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
                this.getTreeData();
            }else{
                this.$message.error(`${res.data.data}`);
            }
        },
        // 点击按钮保存
        async handleToSaveBtn() {
            let arrmenu = this.$refs.menutree.getCheckedNodes(false,true);
            let ids = [];
            arrmenu.forEach(item => {
                ids.push(item.id);
            });
            ids = ids.join(',');

            let arrbtn = this.$refs.btntree.getCheckedNodes(true);
            let btnTypeArr = [];
            arrbtn.forEach(item => {
                btnTypeArr.push(item.btnType);
            });
            if (btnTypeArr.length == 0) {
                btnTypeArr = '';
            } else {
                btnTypeArr = btnTypeArr.join(',');
            }
            
            const res = await this.$axios.post('sys/role/addBtnForRole', {
                roleId: this.roleId,
                btnName: btnTypeArr,
                menuIds: ids
            });
            if (res.data.code == 0) {
                this.$message.success(`${res.data.data}`);
            } else {
                this.$message.error(`${res.data.data}`);
            }
        }
    }
};
</script>

<style lang="less" scoped>
.box-card{
    border-left: 8px solid red;
    border-radius: 6px;
}
/deep/.el-scrollbar__view{
    overflow-x: auto;
    white-space: nowrap;
}
.el-tree{
    display: inline-block;
}
</style>
