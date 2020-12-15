// import Vue from 'vue'
// import Vuex from 'vuex'

// Vue.use(Vuex)

// const modulesFiles = require.context('./modules', true, /\.js$/)

// const modules = modulesFiles.keys().reduce((modules, modulePath) => {
//   const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
//   const value = modulesFiles(modulePath)
//   modules[moduleName] = value.default
//   return modules
// }, {})

// const store = new Vuex.Store({
//   modules,
// //   getters
// })

// export default store

// import Vue from "vue"
// import Vuex from "vuex"
// import routerData from "./modules/routerData"
// import role from "./modules/role"
// Vue.use(Vuex)

// const store = new Vuex.Store({
//   // data
//   state: {
//     token: ''
//   },
//   mutations: {
//     setToken(state, token) {
//       state.token = token
//     },
//   },
//   actions: {
//     setToken({ commit }, token) {
//       return new Promise((resolve, reject) => {
//         commit("setToken", token)
//         resolve()
//       })
//     }
//   },
//   // 计算属性
//   getters: {
//     token: state => state.token
//   }
// })

// export default store

import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    rightList: JSON.parse(sessionStorage.getItem('rightList') || '[]'),
  },
  mutations: {
    setRightList(state, data) {
      state.rightList = data
      sessionStorage.setItem('rightList', JSON.stringify(data))
    }
  },
  actions: {
  },
  getters: {
  }
})
