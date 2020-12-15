import {
  isArray
} from 'util'
import {
  exportDefault,
  titleCase,
  deepClone
} from '@/utils/index'
import ruleTrigger from './ruleTrigger'
import urlLink from '../../constants/index'

const units = {
  KB: '1024',
  MB: '1024 / 1024',
  GB: '1024 / 1024 / 1024'
}
let confGlobal
const inheritAttrs = {
  file: '',
  dialog: 'inheritAttrs: false,'
}

/**
 * 组装js 【入口函数】
 * @param {Object} formConfig 整个表单配置
 * @param {String} type 生成类型，文件或弹窗等
 */
export function makeUpJs(formConfig, type) {
  confGlobal = formConfig = deepClone(formConfig)
  const dataList = []
  const ruleList = []
  const optionsList = []
  const propsList = []
  const methodList = mixinMethod(type)
  const uploadVarList = []

  formConfig.fields.forEach(el => {
    buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList)
  })

  const script = buildexport(
    formConfig,
    type,
    dataList.join('\n'),
    ruleList.join('\n'),
    optionsList.join('\n'),
    uploadVarList.join('\n'),
    propsList.join('\n'),
    methodList.join('\n')
  )
  confGlobal = null
  return script
}
/**
 * 组装js 【入口函数】
 * @param {Object} formConfig 整个表单配置
 * @param {String} type 生成类型，文件或弹窗等
 */
export function makeUpSubmitJs(formConfig, type, formId) {
  confGlobal = formConfig = deepClone(formConfig)
  const dataList = []
  const ruleList = []
  const optionsList = []
  const propsList = []
  const methodList = mixinSubmitMethod(type, formId)
  const uploadVarList = []
  formConfig.fields.forEach(el => {
    buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList)
  })

  const script = buildexport(
    formConfig,
    type,
    dataList.join('\n'),
    ruleList.join('\n'),
    optionsList.join('\n'),
    uploadVarList.join('\n'),
    propsList.join('\n'),
    methodList.join('\n')
  )
  confGlobal = null
  return script
}

// 构建组件属性
function buildAttributes(scheme, dataList, ruleList, optionsList, methodList, propsList, uploadVarList) {
  const config = scheme.__config__
  const slot = scheme.__slot__
  buildData(scheme, dataList)
  buildRules(scheme, ruleList)

  // 特殊处理options属性
  if (scheme.options || (slot && slot.options && slot.options.length)) {
    buildOptions(scheme, optionsList)
    if (config.dataType === 'dynamic') {
      const model = `${scheme.__vModel__}Options`
      const options = titleCase(model)
      buildOptionMethod(`get${options}`, model, methodList)
    }
  }

  // 处理props
  if (scheme.props && scheme.props.props) {
    buildProps(scheme, propsList)
  }

  // 处理el-upload的action
  if (scheme.action && config.tag === 'el-upload') {
    uploadVarList.push(
      `${scheme.__vModel__}Action: '${urlLink.baseURL}${scheme.action}',
      ${scheme.__vModel__}fileList: ${config.defaultValue==null?'[]':config.defaultValue},`
    )
    methodList.push(buildBeforeUpload(scheme))
    methodList.push(buildUploadOnSuccess(scheme))
    methodList.push(buildUploadOnRemove(scheme))
    // 非自动上传时，生成手动上传的函数
    if (!scheme['auto-upload']) {
      methodList.push(buildSubmitUpload(scheme))
    }
  }

  // 构建子级组件属性
  if (config.children) {
    config.children.forEach(item => {
      buildAttributes(item, dataList, ruleList, optionsList, methodList, propsList, uploadVarList)
    })
  }
}

// 混入处理函数
function mixinMethod(type) {
  const list = [];
  const
    minxins = {
      file: confGlobal.formBtns ? {
        submitForm: `submitForm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          // TODO 提交表单
          this.$message.success(JSON.stringify(this.$refs['${confGlobal.formRef}'].model));
        })
      },`,
        resetForm: `resetForm() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`
      } : null,
      dialog: {
        onOpen: 'onOpen() {},',
        onClose: `onClose() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`,
        close: `close() {
        this.$emit('update:visible', false)
      },`,
        handelConfirm: `handelConfirm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          this.close()
        })
      },`
      }
    }

  const methods = minxins[type]
  if (methods) {
    Object.keys(methods).forEach(key => {
      list.push(methods[key])
    })
  }

  return list
}
// 混入处理函数
function mixinSubmitMethod(type, formId) {
  const list = [];
  const url = urlLink.baseURL;
  const token = sessionStorage.getItem('token');
  const userId = sessionStorage.getItem('userId');
  const sessionId = sessionStorage.getItem('sessionId');
  const
    minxins = {
      file: confGlobal.formBtns ? {
        submitForm: `submitForm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          window.parent.submitIframe('submit', this.$refs['${confGlobal.formRef}'].model);
  //         // TODO 提交表单
  //         var param = {
  //           formId: '${formId}',
  //           dataJson: JSON.stringify(this.$refs['${confGlobal.formRef}'].model)
  //         }
  //         console.log(JSON.stringify(this.$refs['${confGlobal.formRef}'].model))
  //         let xhr = new XMLHttpRequest();
  //         xhr.onreadystatechange = () => {
  //             if (xhr.readyState === 4) {
  //                 if (xhr.status === 200) {
  //                     console.log(xhr.responseText)
  //                     if(JSON.parse(xhr.responseText).code==0){
  //                       window.parent.resizeWindow(0);
  //                     }else{
  //                       this.$message.error('保存失败！');
  //                     }
  //                 }
  //             }
  //         }
  //         xhr.open('POST', '${url}formData/saveData', true);
  //         xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset=utf-8");
  //         xhr.setRequestHeader("Authorization",'${token}');
  //         xhr.setRequestHeader("USERID",'${userId}');
  //         xhr.setRequestHeader("sessionId",'${sessionId}');
  //         xhr.send("formId="+"${formId}"+"&"+"dataJson="+JSON.stringify(this.$refs['${confGlobal.formRef}'].model));
        })
      },`,
      resetForm: `resetForm() {
        this.$refs['${confGlobal.formRef}'].resetFields();
        window.parent.submitIframe('cancel');
        //window.parent.resizeWindow(1);
        //parent.location.reload();
      },`
      } : null,
      dialog: {
        onOpen: 'onOpen() {},',
        onClose: `onClose() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`,
      close: `close() {
        this.$emit('update:visible', false)
      },`,
      handelConfirm: `handelConfirm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          this.close()
        })
      },`
      }
    }

  const methods = minxins[type]
  if (methods) {
    Object.keys(methods).forEach(key => {
      list.push(methods[key])
    })
  }

  return list
}

// 构建data
function buildData(scheme, dataList) {
  const config = scheme.__config__
  if (scheme.__vModel__ === undefined) return
  const defaultValue = JSON.stringify(config.defaultValue)
  dataList.push(`${scheme.__vModel__}: ${defaultValue},`)
}

// 构建校验规则
function buildRules(scheme, ruleList) {
  const config = scheme.__config__
  if (scheme.__vModel__ === undefined) return
  const rules = []
  if (ruleTrigger[config.tag]) {
    if (config.required) {
      const type = isArray(config.defaultValue) ? 'type: \'array\',' : ''
      let message = isArray(config.defaultValue) ? `请至少选择一个${config.label}` : scheme.placeholder
      if (message === undefined) message = `${config.label}不能为空`
      rules.push(`{ required: true, ${type} message: '${message}', trigger: '${ruleTrigger[config.tag]}' }`)
    }
    if (config.regList && isArray(config.regList)) {
      config.regList.forEach(item => {
        if (item.pattern) {
          rules.push(
            `{ pattern: ${eval(item.pattern)}, message: '${item.message}', trigger: '${ruleTrigger[config.tag]}' }`
          )
        }
      })
    }
    ruleList.push(`${scheme.__vModel__}: [${rules.join(',')}],`)
  }
}

// 构建options
function buildOptions(scheme, optionsList) {
  if (scheme.__vModel__ === undefined) return
  // el-cascader直接有options属性，其他组件都是定义在slot中，所以有两处判断
  let {
    options
  } = scheme

  if (['el-cascader'].indexOf(scheme.__config__.tag)>-1 && scheme.__slot__==null){
    options = scheme.options
  }else{
    options = scheme.__slot__.options
  } 

  // if (scheme.__config__.dataType === 'dynamic') {
  //   options = []
  // }
  const str = `${scheme.__vModel__}Options: ${JSON.stringify(options)},`
  optionsList.push(str)
  
}

function buildProps(scheme, propsList) {
  const str = `${scheme.__vModel__}Props: ${JSON.stringify(scheme.props.props)},`
  propsList.push(str)
}

// el-upload的BeforeUpload
function buildBeforeUpload(scheme) {
  const config = scheme.__config__
  const unitNum = units[config.sizeUnit];
  let rightSizeCode = '';
  let acceptCode = '';
  const
    returnList = []
  if (config.fileSize) {
    rightSizeCode = `let isRightSize = file.size / ${unitNum} < ${config.fileSize}
    if(!isRightSize){
      this.$message.error('文件大小超过 ${config.fileSize}${config.sizeUnit}')
    }`
    returnList.push('isRightSize')
  }
  if (scheme.accept) {
    acceptCode = `let isAccept = new RegExp('${scheme.accept}').test(file.type)
    if(!isAccept){
      this.$message.error('应该选择${scheme.accept}类型的文件')
    }`
    returnList.push('isAccept')
  }
  const str = `${scheme.__vModel__}BeforeUpload(file) {
    ${rightSizeCode}
    ${acceptCode}
    return ${returnList.join('&&')}
  },`
  return returnList.length ? str : ''
}

// el-upload的submit
function buildSubmitUpload(scheme) {
  const str = `submitUpload() {
    // window.parent.submitFile(${scheme},this.$refs['${scheme.__vModel__}'].model);
    this.$refs['${scheme.__vModel__}'].submit()
  },`
  return str
}
// el-upload的on-success
function buildUploadOnSuccess(scheme) {
  const str = `uploadOnSuccess(response, file, fileList) {
    this.$refs['${scheme.__vModel__}'].submit()
    if(response.code==0){

      this.${scheme.__vModel__}fileList.push({
        id: response.data.id,
        name: response.data.oldFileName,
        url: response.data.url,
      });
      this.formData.${scheme.__vModel__} = JSON.stringify(this.${scheme.__vModel__}fileList);
    }else{
      this.$message.error('上传失败！');
    }
  },`
  return str
}
// el-upload的on-success
function buildUploadOnRemove(scheme) {
  const str = `uploadOnRemove(file, fileList) {
    console.log(file)
    console.log(fileList)
    this.${scheme.__vModel__}fileList.forEach((item, i) => {
      if(item.id==file.id){
        this.${scheme.__vModel__}fileList.splice(i, 1);
        return true;
      }
    });
    this.formData.${scheme.__vModel__} = JSON.stringify(this.${scheme.__vModel__}fileList);
  },`
  return str
}

function buildOptionMethod(methodName, model, methodList) {
  const str = `${methodName}() {
    // TODO 发起请求获取数据
    this.${model}
  },`
  methodList.push(str)
}

// js整体拼接
function buildexport(conf, type, data, rules, selectOptions, uploadVar, props, methods) {
  const str = `${exportDefault}{
  ${inheritAttrs[type]}
  components: {},
  props: [],
  data () {
    return {
      headers: {
        //"Content-type":"multipart/form-data",
        "Authorization":sessionStorage.getItem('token'),
        "USERID":sessionStorage.getItem('userId'),
        "sessionId":sessionStorage.getItem('sessionId')
      },
      ${conf.formModel}: {
        ${data}
      },
      ${conf.formRules}: {
        ${rules}
      },
      ${uploadVar}
      ${selectOptions}
      ${props}
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    ${methods}
  }
}`
  return str
}