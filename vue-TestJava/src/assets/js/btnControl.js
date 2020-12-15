export default (Vue) => {
    /** 权限指令,对按钮权限的控制 **/
    Vue.directive('has', {
        inserted: function (el, binding) {
            // 获取按钮权限
            if (!Vue.prototype.$_has(binding.value)) {
                //移除不匹配的按钮
                el.parentNode.removeChild(el);
            }
        },
    })
    Vue.prototype.$_has = function (value) {
        let isExist = false
        // 从浏览器缓存中获取权限数组
        var buttonpermsStr = sessionStorage.getItem('btnContext').split(',');
        if (buttonpermsStr == undefined || buttonpermsStr == null) {
            return false
        } else {
            buttonpermsStr.forEach(element => {
                //匹配缓存中的数据中有没有匹配的值
                if (element == value) {
                    // 若在按钮中定义的权限字段能在后端返回的权限数组中能找到，则该按钮可显示
                    isExist = true
                }
            });
        }
        return isExist
    }
}