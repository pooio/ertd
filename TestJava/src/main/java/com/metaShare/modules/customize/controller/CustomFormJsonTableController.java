package com.metaShare.modules.customize.controller;


import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.*;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.api.APIBpmCommonController;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.customize.entity.CustomData;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.customize.entity.CustomVersionForm;
import com.metaShare.modules.customize.service.CustomDataService;
import com.metaShare.modules.customize.service.CustomFormJsonTableService;
import com.metaShare.modules.customize.service.CustomFormService;
import com.metaShare.modules.customize.service.CustomVersionFormService;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.entity.SysUser;

import com.metaShare.modules.sys.service.SysconfigService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 自定义表单数据表操作Controller
 */
@Controller
@RequestMapping(value = "/api/formData")
public class CustomFormJsonTableController extends BaseController {

    @Autowired
    private CustomFormService customFormService;
    @Autowired
    private CustomFormJsonTableService customFormJsonTableService;
    @Autowired
    private APIBpmCommonController apiBpmCommonController;
    @Autowired
    private CustomDataService customDataService;
    @Autowired
    private CustomVersionFormService customVersionFormService;
    @Autowired
    private SysFileAttachService sysFileAttachService;
    @Autowired
    private SysconfigService sysconfigService;

    /**
     * 查找数据
     *
     * @param id
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/getDataList")
    @ResponseBody
    @ApiOperation(value = "查找数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "表单Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页展示个数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int")
    })
    public Result getDataList(@RequestParam(value = "id") String id,
                              @RequestParam(value = "pageSize") int pageSize,
                              @RequestParam(value = "pageNum") int pageNum,
                              @RequestParam(value = "dataId",required = false)String dataId,
                              @RequestParam(value = "fieldColumn",required = false)String fieldColumn,
                              @RequestParam(value = "fieldValue",required = false)String fieldValue,
                              @RequestParam(value = "startTime",required = false)String startTime,
                              @RequestParam(value = "endTime",required = false)String endTime) {
        try {
            if(StringUtils.isNotEmpty(endTime)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sdf.parse(endTime));
                calendar.add(Calendar.DATE,1);
                endTime = sdf.format(calendar.getTime());
            }
            StringBuffer sql = new StringBuffer("");
            String[] fieldColumns = null;
            String[] fieldValues = null;
            if(StringUtils.isNotEmpty(fieldColumn) && StringUtils.isNotEmpty(fieldValue)){
                fieldColumns = fieldColumn.split(",");
                fieldValues = fieldValue.split(",");
                for(int i = 0; i < fieldColumns.length; i++){
                    if(!"".equals(fieldColumns[i])){
                        sql.append(" and " + fieldValues[i] + " like CONCAT('%','" + fieldColumns[i] + "','%') ");
                    }
                }
            }
            CustomForm customForm = customFormService.selectById(id);
            List<Map<String, Object>> dataList = customFormJsonTableService.getDataList(customForm.getDataTableName(), (pageNum - 1) * pageSize, pageSize,startTime,endTime,dataId,sql.toString());
            int total = customFormJsonTableService.getDataTotal(customForm.getDataTableName(),startTime,endTime,dataId,sql.toString());
            return Result.resultInfo(ResultCode.SUCCESS, total, dataList);
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.resultInfo(ResultCode.FAILURE, "获取数据失败");
        }
    }


    /**
     * 保存自定义表单数据
     *
     * @param dataJson
     * @param formId
     * @return
     */
    @RequestMapping(value = "/saveData")
    @ResponseBody
    @ApiOperation(value = "保存自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formId", value = "表单Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataJson", value = "数据JSON串", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataId", value = "数据Id", required = true, dataType = "String")
    })
    public Result saveData(@RequestParam(value = "dataJson") String dataJson,
                           @RequestParam(value = "formId") String formId,
                           @RequestParam(value = "dataId", required = false) String dataId,
                           @RequestParam(value = "parentDataId",required = false)String parentDataId) {
        //获取到需要存储数据的自定义表单信息
        SysUser sysUser = getUserInfo();
        CustomForm customForm = customFormService.selectById(formId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotEmpty(dataId)) {
            JSONObject dataJSON = JSONObject.parseObject(dataJson);
            Map<String, Object> dataMap = dataJSON.getInnerMap();
            for (String key : dataMap.keySet()) {
                dataMap.put(key, dataMap.get(key));
            }
            dataMap.put("parent_data_id",parentDataId);
            customFormJsonTableService.updateData(customForm.getDataTableName(), dataMap, dataId);
            String operator = getUserInfo().getName();
            String info = "编辑了" + customForm.getBusinessName() + "表单数据";
            saveOperatorInfo(info, StatusEnum.LogInfoType11.getIntValue(), operator);
            return Result.resultInfo(ResultCode.SUCCESS, "编辑成功");
        } else {
            CustomVersionForm customVersionForm = new CustomVersionForm();
            customVersionForm.setFormJson(customForm.getFormJson());
            customVersionFormService.insert(customVersionForm);
            JSONObject dataJSON = JSONObject.parseObject(dataJson);
            Map<String, Object> dataMap = dataJSON.getInnerMap();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            dataMap.put("id", uuid);
            dataMap.put("user_name", sysUser.getName());
            dataMap.put("user_id", sysUser.getId());
            dataMap.put("org_name", sysUser.getOrgName());
            dataMap.put("org_id", sysUser.getOrgId());
            dataMap.put("create_time", sdf.format(new Date()));
            dataMap.put("parent_data_id",parentDataId);
            for (String key : dataMap.keySet()) {
                dataMap.put(key, dataMap.get(key));
            }
            customFormJsonTableService.saveData(customForm.getDataTableName(), dataMap);

            CustomData customData = new CustomData();
            customData.setCustomFormId(formId);
            customData.setCustomData(dataJson);
            customData.setCustomDataId(uuid);
            customData.setCustomVersionId(customVersionForm.getId());
            //判断该表单是否与流程关联，若与流程关联，则启动流程
            customDataService.insert(customData);
            String processDefinitionKey = customForm.getProcessDefinitionKey();
            if (StringUtils.isNotEmpty(processDefinitionKey)) {
                //若存在流程，则增加表单参数，防止删除
                customForm.setIsUseNumber(customForm.getIsUseNumber() + 1);
                customFormService.updateById(customForm);
                String title = customForm.getBusinessName();
                apiBpmCommonController.start(uuid, customForm.getFormIdentification(), processDefinitionKey, null, customForm.getDataTableName(), null, title);
            }

            String operator = getUserInfo().getName();
            String info = "保存了" + customForm.getBusinessName() + "表单数据";
            saveOperatorInfo(info, StatusEnum.LogInfoType11.getIntValue(), operator);
            return Result.resultInfo(ResultCode.SUCCESS, "保存成功");
        }
    }

    @RequestMapping(value = "/getDataJsonById")
    @ResponseBody
    @ApiOperation(value = "获取对应流程的自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String")
    })
    public Result getDataJsonById(@RequestParam(value = "id") String id) {
        CustomData customData = customDataService.getDataJsonById(id);
        return Result.resultInfo(ResultCode.SUCCESS, customData);
    }


    /**
     * 删除数据
     *
     * @return
     */
    @RequestMapping(value = "/deleteData")
    @ResponseBody
    @ApiOperation(value = "删除自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formId", value = "表单Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataId", value = "数据主键", required = true, dataType = "String")
    })
    public Result deleteData(@RequestParam(value = "dataId") String dataId,
                             @RequestParam(value = "formId") String formId) {
        CustomForm customForm = customFormService.selectById(formId);
        customFormJsonTableService.deleteData(customForm.getDataTableName(), dataId);

        String operator = getUserInfo().getName();
        String info = "删除了" + customForm.getBusinessName() + "表单数据";
        saveOperatorInfo(info, StatusEnum.LogInfoType11.getIntValue(), operator);

        return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
    }


    @RequestMapping(value = "/getDataJson")
    @ResponseBody
    @ApiOperation(value = "获取对应流程的自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessKey", value = "表单标识", required = true, dataType = "String")
    })
    public Result getDataJson(@RequestParam(value = "businessKey") String businessKey) {
        CustomData customData = customDataService.getDataJson(businessKey);
        return Result.resultInfo(ResultCode.SUCCESS, customData);
    }

    @RequestMapping(value = "/getDataTable")
    @ResponseBody
    @ApiOperation(value = "获取自定义表单表头")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "表单Id", required = true, dataType = "String")
    })
    public Result getDataTable(@RequestParam(value = "id") String id) {
        CustomForm customForm = customFormService.selectById(id);
        return Result.resultInfo(ResultCode.SUCCESS, customForm.getFormJson());
    }

    @RequestMapping(value = "/exportCustomFormData")
    @ResponseBody
    @ApiOperation(value = "导出自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "表单Id", required = true, dataType = "String")
    })
    public void exportCustomFormData(@RequestParam(value = "id") String id, HttpServletResponse response, HttpServletRequest request) {
        OutputStream outputStream = null;
        try {
            CustomForm customForm = customFormService.selectById(id);
            String fileName = customForm.getBusinessName();
            String userAgent = request.getHeader("User-Agent");
            outputStream = response.getOutputStream();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                //针对IE或者以IE为内核的浏览器：
                fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
            } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
                //doNothing
            } else if (userAgent.contains("android")) {
                //安卓
                fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(customForm.getBusinessName() + "数据表");
            List<Map> dataList = customFormJsonTableService.getAllDataListOnTable(customForm.getDataTableName());
            String formJason = customForm.getFormJson();
            JSONObject form = JSONObject.parseObject(formJason);
            JSONArray jsonArray = form.getJSONArray("fields");
            List<Map> hashMaps = jsonArray.toJavaList(Map.class);
            List<String> headerList = new ArrayList<>();
            List<String> keyList = new ArrayList<>();
            headerList.add("序号");
            keyList.add("seq");
            for (Map map : hashMaps) {
                Map<String, String> configMap = (Map<String, String>) map.get("__config__");
                headerList.add(configMap.get("label"));
                keyList.add(String.valueOf(map.get("__vModel__")));
            }
            headerList.add("创建人");
            headerList.add("创建时间");
            keyList.add("user_name");
            keyList.add("create_time");
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, String> map = dataList.get(i);
                map.put("seq", String.valueOf(i + 1));
            }
            String[] columnKeys = keyList.toArray(new String[keyList.size()]);
            String[] headers = headerList.toArray(new String[headerList.size()]);
            for (int i = 1; i <= headerList.size() - 1; i++) {
                sheet.setColumnWidth(i, 25 * 256);
            }


            response.setHeader("Content-Disposition", "attachment;Filename=" + fileName + sdf.format(new Date()) + ".xls");
            ExcelExportUtil.doExportToExcel(workbook, columnKeys, dataList, sheet, headers);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/getAppCustomData")
    @ResponseBody
    public Result getAppCustomData(@RequestParam(value = "businessKey") String businessKey) {
        //表单数据Id custom_data_id  获取到历史表单JSON 以及数据JSON
        CustomData customData = customDataService.getDataJson(businessKey);
        CustomVersionForm customVersionForm = customVersionFormService.selectById(customData.getCustomVersionId());
        CustomForm customForm = customFormService.selectById(customData.getCustomFormId());
        Map<String, String> userMap = customFormJsonTableService.getUserInfo(customForm.getDataTableName(), businessKey);
        JSONObject jsonObject = JSONObject.parseObject(customVersionForm.getFormJson());
        JSONArray listArray = jsonObject.getJSONArray("fields");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(userMap);
        for (int i = 0; i < listArray.size(); i++) {
            Map<String, String> fieldMap = new HashMap<>();
            Map<String, Object> dataMap = (Map<String, Object>) listArray.get(i);
            String type = String.valueOf(dataMap.get("__vModel__"));
            if (StringUtils.isEmpty(type)) {
                //说明是行容器
                Map<String, String> map = (Map<String, String>) dataMap.get("__config__");
                type = map.get("componentName");
            }
            String keys = String.valueOf(dataMap.get("__config__"));
            JSONObject keyJSON = JSONObject.parseObject(keys);
            String key = keyJSON.getString("label");
            String tagIcon = keyJSON.getString("tagIcon");
            fieldMap.put("column", key);
            fieldMap.put("key", type);
            fieldMap.put("tagIcon", tagIcon);
            dataList.add(fieldMap);
        }
        JSONObject dataJson = JSONObject.parseObject(customData.getCustomData());
        for (Map<String, String> map : dataList) {
            String key = map.get("key");
            String value = dataJson.getString(key);
            if (StringUtils.isEmpty(value)) {
                value = "";
            }
            map.put("value", value);
        }
        return Result.resultInfo(ResultCode.SUCCESS, dataList);
    }

    @RequestMapping(value = "/exportCustomFormDataToWord")
    @ResponseBody
    @ApiOperation(value = "根据模板导出自定义表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formId", value = "表单Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataId", value = "数据Id", required = true, dataType = "String")
    })
    public String docxExportTest(@RequestParam(value = "formId") String formId,
                               @RequestParam(value = "dataId") String dataId,
                               HttpServletResponse response, HttpServletRequest request) throws Exception {
        CustomForm customForm = customFormService.selectById(formId);
        Map<String, Object> map = customFormJsonTableService.getDataById(customForm.getDataTableName(),dataId);
        SysFileAttach sysFileAttach = sysFileAttachService.selectById(customForm.getWordFileId());
        SysConfig sysConfig5 = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());
        String path = sysConfig5.getConfigContent() + sysFileAttach.getFilePath() + sysFileAttach.getFileName();
        File file = new File(path);//改成你本地文件所在目录

        XWPFTemplate xwpfTemplate = null;
        HWPFDocument hwpfDocument = null;
        String suffix = "docx";
        if(file.exists()){
            FileInputStream fileInputStream = new FileInputStream(file);
            String newFileName = UUID.randomUUID().toString().replace("-","");
            int type = 1;
            if(!sysFileAttach.getFileType().equals(suffix)){
                suffix = "doc";
                type = 2;
            }
            XWPFDocument doc = null;
            if(type == 1){
                // 读取word模板
                //template = new WordTemplate(fileInputStream);
                // 替换数据
                //template.replaceDocument1(map);
                //doc = WordExportUtil.exportWord07(path, map);
                xwpfTemplate = XWPFTemplate.compile(file).render(map);
            }else{
                hwpfDocument = WordTemplate.poiWordTableReplace(fileInputStream, map);
            }
            String newFilePath = "D:\\"+newFileName+"." + suffix;
            File outputFile=new File(newFilePath);
            FileOutputStream fos  = new FileOutputStream(outputFile);
            if(type == 1){
                //doc.write(fos);
                xwpfTemplate.writeToFile(newFilePath);
                xwpfTemplate.close();
            }else{
                hwpfDocument.write(fos);
            }
            fos.close();
            String fileName = customForm.getBusinessName() + "." + suffix;
            //传输流
            WebUtils.download(outputFile,fileName,request,response);
            if(outputFile.exists()){
                outputFile.delete();
            }
        }
        return suffix;
    }

    @RequestMapping(value = "/getCustomDirData")
    @ResponseBody
    public Result getCustomDirData(@RequestParam(value = "formId")String formId,
                                   @RequestParam(value = "column")String column){
        CustomForm customForm = customFormService.selectById(formId);
        List<Map<String,String>> dirDataList = customFormJsonTableService.getCustomDirData(customForm.getDataTableName(),column);
        return Result.resultInfo(ResultCode.SUCCESS,dirDataList);
    }

    @RequestMapping(value = "/getIsPublishCustomList")
    @ResponseBody
    public Result getIsPublishCustomList(){
        List<CustomForm> customFormList = customFormService.getIsPublishCustomList();
        return Result.resultInfo(ResultCode.SUCCESS,customFormList);
    }

}
