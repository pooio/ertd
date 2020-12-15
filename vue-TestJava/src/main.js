/*
 * @Author: Misaka.chen
 * @Date: 2020-01-06 09:32:59
 * @LastEditors: flynn.yang
 * @LastEditTime: 2020-06-28 14:46:43
 * @Description: MetaShare
 * @Version: 1.0
 */
import 'babel-polyfill';
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import ElementUI, { Message } from 'element-ui'
import '@/assets/css/global.css';
import VueI18n from 'vue-i18n';
import { messages } from './components/common/i18n';
import 'element-ui/lib/theme-chalk/index.css'; // 默认主题
// import './assets/css/theme-green/index.css'; // 浅绿色主题
import './assets/css/icon.css';
// 导入axios
import axios from './api';
Vue.prototype.$axios = axios;

// 导入btnControl
import btnControl from './assets/js/btnControl'
Vue.use(btnControl)
import comments from '@/constants/index.js'
Vue.prototype.BASE_URL = comments

// 导入moment.js(过滤时间)
import moment from 'moment';
Vue.prototype.$moment = moment;
// 导入图片裁剪
import { VueCropper } from 'vue-cropper'
Vue.component('VueCropper', VueCropper)

import './assets/icons'; // icon

// 导入Lodash
import _ from 'lodash'
Vue.prototype._ = _

// 导入vuex
import store from './store'

// 导入文件加载
import download from './utils/download'
Vue.prototype.$download = download

// 导入Tinymce富文本编辑器
import Tinymce from '@/components/tinymce/index.vue'
Vue.component('tinymce', Tinymce)

import '@/styles/index.scss'

Vue.config.productionTip = false;

// 导入'vue-echarts'
// import ECharts from 'vue-echarts'
// import 'echarts/lib/chart/line';
// import 'echarts/lib/component/polar';
// Vue.component('v-chart', ECharts)

// 为了实现Class的私有属性
const showMessage = Symbol('showMessage')
/** 
 *  重写ElementUI的Message
 *  single默认值true，因为项目需求，默认只弹出一个，可以根据实际需要设置
 */
class DonMessage {
    success(options, single = true) {
        this[showMessage]('success', options, single)
    }
    warning(options, single = true) {
        this[showMessage]('warning', options, single)
    }
    info(options, single = true) {
        this[showMessage]('info', options, single)
    }
    error(options, single = true) {
        this[showMessage]('error', options, single)
    }

    [showMessage](type, options, single) {
        if (single) {
            // 判断是否已存在Message
            if (document.getElementsByClassName('el-message').length === 0) {
                Message[type](options)
            }
        } else {
            Message[type](options)
        }
    }
}

// 命名根据需要
Vue.prototype.$message = new DonMessage()

Vue.use(VueI18n);
Vue.use(ElementUI, {
    size: 'small'
});
const i18n = new VueI18n({
    locale: 'zh',
    messages
});


// 日期格式化过滤器 默认显示年月日
Vue.filter('moment', function (dataStr, pattern = 'YYYY-MM-DD') {
    if (dataStr) {
        return moment(dataStr).format(pattern);
    } else {
        return dataStr;
    }
});

new Vue({
    router,
    i18n,
    store,
    render: h => h(App)
}).$mount('#app');