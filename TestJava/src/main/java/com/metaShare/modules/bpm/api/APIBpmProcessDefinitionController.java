package com.metaShare.modules.bpm.api;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.BpmProcessDefinition;
import com.metaShare.modules.bpm.service.BpmProcessDefinitionService;

/**
 * 流程定义controller
 * 
 *
 */
@Controller
@RequestMapping("api/bpm")
public class APIBpmProcessDefinitionController  extends BaseController{
	
	private static Logger log = LoggerFactory.getLogger(APIBpmProcessDefinitionController.class);
	
	@Autowired
	BpmProcessDefinitionService bpmProcessDefinitionService;
	
	// 权限字符串前缀
	protected String identity = "bpm:api:APIBpmProcessDefinition";
	protected String checkBeforeSaveMsg = "";
	protected String checkBeforeUpdateMsg = "";
	protected String checkBeforeDeleteMsg = "";
 	
 	 /**
     * 获得流程定义列表
     * @param modelName
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("definition/list")
    public Result list(@RequestParam(value = "pageNumber") int pageNumber,
							@RequestParam(value = "pageSize") int pageSize,
							@RequestParam(value="lastVersion",required=false) Boolean lastVersion) throws Exception {
    	if(lastVersion==null){
    		lastVersion = false;
    	}
    	PageDTO<BpmProcessDefinition> page = new PageDTO<BpmProcessDefinition>();
    	page.setCurrent(pageNumber);
		page.setPages(pageSize);
    	page = bpmProcessDefinitionService.listProcessDefinitions(page,lastVersion);
    	return Result.resultInfo(ResultCode.SUCCESS, page);
    }
    
    /**
     * 获取最新版本的所有流程定义信息
     * @return
     * @throws Exception 
     * @return List<BpmProcessDefinition>
     * @author: zhaojie/zh_jie@163.com 
     * @version: 2016年3月11日 下午2:37:37
     */
    @ResponseBody
    @RequestMapping("definition/latest/listAll")
    public Result latestVersion() throws Exception {
    	return Result.resultInfo(ResultCode.SUCCESS, bpmProcessDefinitionService.listProcessDefinitions(true));
    }
    
    /**
     * 获得流程定义图片
     * @param processDefinitionId
     */
    @RequestMapping("definition/img")
    public void processDefinitionImg(@RequestParam(value = "processDefinitionId",required=false) String processDefinitionId,
    		@RequestParam(value = "processDefinitionKey",required=false) String processDefinitionKey,
    		HttpServletResponse response) throws Exception {
    	InputStream is = null;
    	if(StringUtils.isNotBlank(processDefinitionId)){
    		is = bpmProcessDefinitionService.getProcessDefinitionImg(processDefinitionId);
    	}
    	if(StringUtils.isNotBlank(processDefinitionKey)){
    		is = bpmProcessDefinitionService.getProcessDefinitionImgByKey(processDefinitionKey);
    	}
   	 	response.setContentType("image/png");
   	 	if(is!=null){
   	 		IOUtils.copy(is, response.getOutputStream());
   	 	}
    }
    
   
    
    /**
     * 获得流程定义xml
     * @param processDefinitionId
     */
    @RequestMapping("definition/xml")
    public void processDefinitionXml(@RequestParam(value = "processDefinitionId") String processDefinitionId,
    		HttpServletResponse response) throws Exception {
    	InputStream is = bpmProcessDefinitionService.getProcessDefinitionXml(processDefinitionId);
    	response.setContentType("text/xml;charset=UTF-8");
        IOUtils.copy(is, response.getOutputStream());
    }
    
    
    
    /**
     * 暂停流程定义
     * @param processDefinitionId
     */
    @ResponseBody
    @RequestMapping("definition/suspend")
    public Result suspend(@RequestParam(value = "processDefinitionId") String processDefinitionId) throws Exception {
    	bpmProcessDefinitionService.suspendProcessDefinition(processDefinitionId);
    	return Result.resultInfo(ResultCode.SUCCESS, "流程定义挂起（暂停）成功！");
    }
    
    /**
     * 激活流程定义
     * @param processDefinitionId
     */
    @ResponseBody
    @RequestMapping("definition/active")
    public Result active(@RequestParam(value = "processDefinitionId") String processDefinitionId) throws Exception {
    	bpmProcessDefinitionService.activeProcessDefinition(processDefinitionId);
    	return Result.resultInfo(ResultCode.SUCCESS, "流程定义激活(启动)成功！");
    }
    
    @ResponseBody
    @RequestMapping("definition/deployment/delete")
    public Result deleteDeployment(@RequestParam(value = "id") String[] ids) {
//    	for(String id : ids){
    		bpmProcessDefinitionService.deleteDeployment(ids);
//    	}
		return Result.resultInfo(ResultCode.SUCCESS, "删除流程部署成功");
    }
    
    @ResponseBody
    @RequestMapping("definition/deployment/delete/cascade")
    public Result deleteDeploymentAndInstance(@RequestParam(value = "id") String[] ids) {
    	for(String id : ids){
    		bpmProcessDefinitionService.deleteDeploymentAndProcessInstance(ids);
    	}
		return Result.resultInfo(ResultCode.SUCCESS, "删除流程部署及流程定义成功");
    }
    
}
