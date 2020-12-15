package com.metaShare.modules.customize.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.metaShare.common.tool.pageTool.Page;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.common.utils.TransitionUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.customize.entity.*;
import com.metaShare.modules.customize.service.*;
import com.metaShare.modules.sys.entity.SysUser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 自定义表单类
 */
@Controller
@RequestMapping(value = "/api/form")
public class CustomFormController extends BaseController {
    @Autowired
    private CustomFormService customFormService;
    @Autowired
    private CustomFormJsonTableService customFormJsonTableService;
    @Autowired
    private CustomDataService customDataService;
    @Autowired
    private CustomVersionFormService customVersionFormService;
    @Autowired
    private CustomRelationalService customRelationalService;
    /**
     * 保存业务
     * @param businessName
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveBussiness")
    @ApiOperation(value="保存业务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessName", value = "业务名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "description", value = "表单描述", required = true, dataType = "String"),
            @ApiImplicitParam(name = "icon", value = "图标", required = true, dataType = "String")
    })
    public Result saveBussiness(@RequestParam(value = "businessName")String businessName,
                                @RequestParam(value = "description",required = false)String description,
                                @RequestParam(value = "icon",required = false)String icon,
                                @RequestParam(value = "wordFileId",required = false)Integer wordFileId,
                                @RequestParam(value = "color",required = false)String color,
                                @RequestParam(value = "parentId",required = false)String parentId){
        SysUser sysUser = getUserInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomForm customForm = new CustomForm();
        customForm.setBusinessName(businessName);
        String spell = TransitionUtils.getFirstSpell(businessName);
        boolean flag = true;
        //判断标识是否已存在
        while(flag){
            int count = customFormService.getBySpell(spell);
            char str=(char) ('a'+Math.random()*('z'-'a'+1));
            if(count >= 1){
                spell = spell + str;
            }else{
                flag =false;
            }
        }
        customForm.setFormIdentification(spell);
        customForm.setPostStatus(0);
        customForm.setDescription(description);
        customForm.setIcon(icon);
        customForm.setIsCreateTable(0);
        customForm.setIsUseNumber(0);
        customForm.setApprovalStatus(2);
        customForm.setCreateUserId(sysUser.getId());
        customForm.setColor(color);
        customForm.setCreateTime(sdf.format(new Date()));
        customForm.setWordFileId(wordFileId);
        customFormService.insert(customForm);

        //保存父子表关系
        if(StringUtils.isNotEmpty(parentId)){
            CustomRelational customRelational = new CustomRelational();
            customRelational.setParentId(parentId);
            customRelational.setChildrenId(customForm.getId());
            customRelational.setCreateTime(sdf.format(new Date()));
            customRelationalService.insert(customRelational);
        }


        String operator = getUserInfo().getName();
        String info = "新增了" +businessName+ "业务";
        saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);

        return  Result.resultInfo(ResultCode.SUCCESS,customForm);

    }

    @ResponseBody
    @RequestMapping(value = "/updateBussiness")
    @ApiOperation(value="编辑业务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessName", value = "业务名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
            @ApiImplicitParam(name = "description", value = "表单描述", required = true, dataType = "String"),
            @ApiImplicitParam(name = "icon", value = "图标", required = true, dataType = "String")
    })
    public Result updateBussiness(@RequestParam(value = "businessName")String businessName,
                                  @RequestParam(value = "id")String id,
                                  @RequestParam(value = "description",required = false)String description,
                                  @RequestParam(value = "icon",required = false)String icon,
                                  @RequestParam(value = "wordFileId",required = false)Integer wordFileId,
                                  @RequestParam(value = "color",required = false)String color,
                                  @RequestParam(value = "parentId",required = false)String parentId){
        CustomForm customForm = customFormService.selectById(id);
        if(customForm.getPostStatus() == 1){
            return  Result.resultInfo(ResultCode.FAILURE,"该表单已发布，不能编辑");
        }
        customForm.setBusinessName(businessName);
        customForm.setDescription(description);
        customForm.setIcon(icon);
        customForm.setColor(color);
        customForm.setWordFileId(wordFileId);
        customFormService.updateAllColumnById(customForm);

        CustomRelational customRelational = customRelationalService.selectByChildrenId(id);
        if(StringUtils.isNotEmpty(parentId)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(customRelational == null){
                //直接保存
                CustomRelational newCustomRelational = new CustomRelational();
                newCustomRelational.setParentId(parentId);
                newCustomRelational.setChildrenId(customForm.getId());
                newCustomRelational.setCreateTime(sdf.format(new Date()));
                customRelationalService.insert(newCustomRelational);
            }else{
                //判断是否修改了父关系表
                if(!customRelational.getParentId().equals(parentId)){
                    customRelational.setParentId(parentId);
                    customRelationalService.updateById(customRelational);
                }
            }
        }else{
            if(customRelational != null){
                customRelationalService.deleteById(customRelational.getId());
            }
        }


        String operator = getUserInfo().getName();
        String info = "编辑了" +businessName+ "表单";
        saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);


        return  Result.resultInfo(ResultCode.SUCCESS,"编辑成功");

    }


    @ResponseBody
    @RequestMapping(value = "/getFormData")
    @ApiOperation(value="获取表单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessKey", value = "业务ID", required = true, dataType = "String")
    })
    public Result getFormData(@RequestParam(value = "businessKey")String businessKey){
        CustomData customData = customDataService.getDataJson(businessKey);
        if(StringUtils.isNotEmpty(customData.getCustomVersionId())){
            //防止之前数据该值为空报错
            CustomVersionForm customVersionForm = customVersionFormService.selectById(customData.getCustomVersionId());
            return  Result.resultInfo(ResultCode.SUCCESS,customVersionForm);
        }else{
            CustomForm customForm = customFormService.selectById(customData.getCustomFormId());
            return  Result.resultInfo(ResultCode.SUCCESS,customForm);
        }
    }

    /**
     * 保存自定义表单
     * TODO 跟流程关联
     * @param json
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveCustomForm")
    @ApiOperation(value="保存业务与自定义表单关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "json", value = "自定义表单JSON串", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result saveCustomForm(@RequestParam(value = "json") String json,
                                 @RequestParam(value = "id")String id) {
        CustomForm customForm = new CustomForm();
        customForm.setId(id);
        customForm.setFormJson(json);
        customFormService.updateById(customForm);
        CustomForm customForm1 = customFormService.selectById(id);
        if (customForm1 != null) {
            String operator = getUserInfo().getName();
            String info = "设计了" + customForm1.getBusinessName() + "自定义表单";
            saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);
        }
        return Result.resultInfo(ResultCode.SUCCESS,"保存成功");
    }

    /**
     * 绑定流程标识
     * TODO 跟流程关联
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/toBindProcessDefinition")
    @ApiOperation(value="绑定流程标识")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result toBindProcessDefinition(@RequestParam(value = "processDefinitionKey",required = false) String processDefinitionKey,
                                 @RequestParam(value = "id")String id){

        customFormService.updateDefinitionKey(processDefinitionKey,id);
        CustomForm customForm1= customFormService.selectById(id);
        if(customForm1!=null){
            String operator = getUserInfo().getName();
            String info = "";
            if(StringUtils.isEmpty(processDefinitionKey)){
                info = customForm1.getBusinessName() + "解除了流程绑定";
            }else{
                Map<String,String> modelData = customFormService.getModelName(processDefinitionKey);
                info = "业务" +customForm1.getBusinessName()+ "，绑定了流程"+modelData.get("NAME_");
            }
            saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);

        }
        return Result.resultInfo(ResultCode.SUCCESS,"保存成功");
    }

    /**
     * 将自定义表单发布，并创建对应的数据库表
     * @param id
     * @return
     */
    @RequestMapping(value = "/ToPublishCustomForm")
    @ResponseBody
    @ApiOperation(value="发布自定义表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result publishCustomForm(@RequestParam(value = "id")String id){
        //判断该表单是否已建立过数据库表
        CustomForm existCustomForm = customFormService.selectById(id);
        if(existCustomForm.getIsCreateTable() == 0){
            existCustomForm.setDataTableName("dynamic_" + existCustomForm.getFormIdentification());
            String json = existCustomForm.getFormJson();
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray listArray =  jsonObject.getJSONArray("fields");
            Map<String,String> tableFields = new HashMap<>();
            List<String> columnName = new ArrayList<>();
            for(int i = 0; i < listArray.size(); i++){
                Map<String,Object> dataMap = (Map<String, Object>) listArray.get(i);
                String type = String.valueOf(dataMap.get("__vModel__"));
                if(StringUtils.isEmpty(type)){
                    //说明是行容器
                    Map<String,String> map = (Map<String, String>) dataMap.get("__config__");
                    type = map.get("componentName");
                }
                //String keys = String.valueOf(dataMap.get("__config__"));
                //JSONObject keyJSON = JSONObject.parseObject(keys);
                //String key = keyJSON.getString("tagIcon");
                //tableFields.put(key, type);
                columnName.add(type);
            }
            String tableName = "dynamic_" + existCustomForm.getFormIdentification();
            String[] names = columnName.toArray(new String[columnName.size()]);
            customFormJsonTableService.createCustomTable(tableName,names);
        }else{
            //数据库表已创建，判断是否存在新增字段
            String json = existCustomForm.getFormJson();
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray listArray =  jsonObject.getJSONArray("fields");
            for(int i = 0; i < listArray.size(); i++){
                Map<String,Object> dataMap = (Map<String, Object>) listArray.get(i);
                String type = String.valueOf(dataMap.get("__vModel__"));
                if(StringUtils.isEmpty(type)){
                    Map<String,String> map = (Map<String, String>) dataMap.get("__config__");
                    type = map.get("componentName");
                }
                int count = customFormJsonTableService.judgeFieldExist(existCustomForm.getDataTableName(),type);
                if(count != 0){
                    continue;
                }
                //将新增加的字段新增到数据库表中
                String keys = String.valueOf(dataMap.get("__config__"));
                JSONObject keyJSON = JSONObject.parseObject(keys);
                String key = getInconKey(keyJSON.getString("tagIcon"));
                customFormJsonTableService.insertFieldToTable(existCustomForm.getDataTableName(),type,key);
            }
        }
        //自义定数据库表
        existCustomForm.setPostStatus(1);
        existCustomForm.setIsCreateTable(1);
        customFormService.updateById(existCustomForm);


        String operator = getUserInfo().getName();
        String info = "发布了" +existCustomForm.getBusinessName()+ "表单";
        saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);

        return Result.resultInfo(ResultCode.SUCCESS,"发布成功");
    }

    private String getInconKey(String tagIcon) {
        return "VARCHAR(500)";
    }
    /**
     * 取消发布表单
     * @param id
     * @return
     */
    @RequestMapping(value = "/ToUnPublishCustomForm")
    @ResponseBody
    @ApiOperation(value="取消发布自定义表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result ToUnPublishCustomForm(@RequestParam(value = "id")String id){
        //自义定数据库表
        CustomForm existCustomForm = customFormService.selectById(id);
        existCustomForm.setPostStatus(0);
        customFormService.updateById(existCustomForm);


        String operator = getUserInfo().getName();
        String info = "取消发布了" +existCustomForm.getBusinessName()+ "表单";
        saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);


        return Result.resultInfo(ResultCode.SUCCESS,"取消发布成功");
    }


    /**
     * 删除自定义表单
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteCustomForm")
    @ApiOperation(value="删除自定义表单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result deleteCustomForm(@RequestParam(value = "id")String id){
        CustomForm customForm = customFormService.selectById(id);
        if(customForm.getIsUseNumber() > 0){
            return Result.resultInfo(ResultCode.FAILURE,"该表单正在被使用，不能删除");
        }
        customFormService.updateToDelete(id);
        //删除对应数据库表
        if(StringUtils.isNotEmpty(customForm.getDataTableName())){
            customFormJsonTableService.deleteTable(customForm.getDataTableName());
        }
        //删除与父表的关系
        customRelationalService.deleteFromChildrenId(id);

        String operator = getUserInfo().getName();
        String info = "删除了" +customForm.getBusinessName()+ "表单";
        saveOperatorInfo(info, StatusEnum.LogInfoType10.getIntValue(), operator);


        return Result.resultInfo(ResultCode.SUCCESS,"删除成功");
    }


    /**
     * 获取业务表单列表
     * @param pageNum
     * @param pageSize
     * @param businessName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getCustomFormList")
    @ApiOperation(value="获取业务表单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页展示个数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "businessName", value = "业务名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "Integer")
    })
    public Result getListOnTableName(@RequestParam(value = "pageNum")int pageNum,
                                     @RequestParam(value = "pageSize")int pageSize,
                                     @RequestParam(value = "businessName",required = false)String businessName,
                                     @RequestParam(value = "type",required = false)Integer type){
        //需要区分是个人查看还是系统管理员管理，个人查看只可看已发布的表单
        List<CustomForm> customFormList = customFormService.getList(businessName,type);
        PageDTO<CustomForm> pageDTO = new PageTool<CustomForm>().getPage(customFormList,pageSize,pageNum);
        return Result.resultInfo(ResultCode.SUCCESS,customFormList.size(),pageDTO.getData());
    }


    /**
     * 判断表单标识是否重复
     * @param formIdentification
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkFormIdentification")
    @ApiOperation(value="判断表单标识是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formIdentification", value = "表单标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result checkFormIdentification(@RequestParam(value = "formIdentification")String formIdentification,
                                          @RequestParam(value = "id",required = false)String id){
        Result result = null;
        CustomForm customForm = customFormService.selectByIdentification(formIdentification);
        if(customForm == null){
            result  = Result.resultInfo(ResultCode.SUCCESS,true);
        }else{
            if(customForm.getId().equals(id)){
                result  = Result.resultInfo(ResultCode.SUCCESS,true);
            }else{
                result  = Result.resultInfo(ResultCode.SUCCESS,false);
            }
        }
        return result;
    }

    /**
     * 判断表单名称是否重复
     * @param formName
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkFormName")
    @ApiOperation(value="判断表单名称是否重复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formName", value = "表单名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result checkFormName(@RequestParam(value = "formName")String formName,
                                          @RequestParam(value = "id",required = false)String id){
        Result result = null;
        CustomForm customForm = customFormService.selectByFormName(formName);
        if(customForm == null){
            result  = Result.resultInfo(ResultCode.SUCCESS,true);
        }else{
            if(StringUtils.isEmpty(id) && !"".equals(id)){
                result  = Result.resultInfo(ResultCode.FAILURE,false);
            }else{
                if(customForm.getId().equals(id)){
                    result  = Result.resultInfo(ResultCode.SUCCESS,true);
                }else{
                    result  = Result.resultInfo(ResultCode.FAILURE,false);
                }
            }
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/getFormById")
    @ApiOperation(value="根据ID获取表单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result getFormById(@RequestParam(value = "id",required = false)String id){
        CustomForm customForm = customFormService.getFormById(id);
        return Result.resultInfo(ResultCode.SUCCESS,customForm);
    }

    @ResponseBody
    @RequestMapping(value = "/getFormByIdentification")
    @ApiOperation(value="根据ID获取表单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
    })
    public Result getFormByIdentification(@RequestParam(value = "id",required = false)String id){
        CustomForm customForm = customFormService.selectByIdentification(id);
        if(customForm == null){
            return Result.resultInfo(ResultCode.FAILURE,"表单不存在");
        }
        if(customForm.getPostStatus() == 0){
            return Result.resultInfo(ResultCode.FAILURE,"表单未发布");
        }
        List<CustomForm> childrenCustomTable = customRelationalService.getChildrenCustomTable(customForm.getId());
        if(childrenCustomTable.size() > 0){
            customForm.setIsOwnChildren("1");
        }else{
            customForm.setIsOwnChildren("0");
        }
        return Result.resultInfo(ResultCode.SUCCESS,customForm);
    }

    @RequestMapping(value = "/getCustomListName")
    @ResponseBody
    @ApiOperation(value="获取已发布列表")
    public Result getCustomListName(@RequestParam(value = "customName",required = false)String customName){
        List<CustomForm> customFormList = customFormService.getCustomListName(customName);
        return Result.resultInfo(ResultCode.SUCCESS,customFormList);
    }

    @RequestMapping(value = "/getChildrenCustomTable")
    @ResponseBody
    @ApiOperation(value = "获取子表列表")
    public Result getChildrenCustomTable(@RequestParam(value = "formId")String formId){
        List<CustomForm> customFormList = customRelationalService.getChildrenCustomTable(formId);
        return Result.resultInfo(ResultCode.SUCCESS,customFormList);
    }


}
