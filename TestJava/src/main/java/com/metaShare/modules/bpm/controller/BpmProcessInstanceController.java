package com.metaShare.modules.bpm.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.SessionHelper;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmProcessInstance;
import com.metaShare.modules.bpm.service.BpmActivityService;
import com.metaShare.modules.bpm.service.BpmProcessInstanceService;
import com.metaShare.modules.bpm.service.BpmTaskService;
import com.metaShare.modules.bpm.service.TraceService;
import com.metaShare.modules.sys.entity.SysUser;



/**
 * 流程实例controller
 * 
 *
 */
@Controller
@RequestMapping("bpm")
public class BpmProcessInstanceController  extends BaseController {
	private static Logger log = LoggerFactory.getLogger(BpmProcessInstanceController.class);
	
	@Autowired
	BpmProcessInstanceService bpmProcessInstanceService;
	@Autowired
    BpmActivityService bpmActivityService;
    @Autowired
    TraceService traceService;
    @Autowired
    BpmTaskService bpmTaskService;
    @Autowired
    ManagementService managementService;
	
	@RequestMapping("instance")
    public String instance() throws Exception {
    	return "/bpm/instance/main";
    }

	
	@ResponseBody
    @RequestMapping("instance/list")
    public PageDTO<BpmProcessInstance> list(@RequestParam(value="pageSize")int pageSize,
    		@RequestParam(value="pageNumber")int pageNumber,
    		@RequestParam(value="businessKey",required=false) String businessKey,
    		@RequestParam(value="processDefinitionName",required=false) String processDefinitionName,
    		@RequestParam(value="loginName",required=false) String loginName) throws Exception {
		PageDTO<BpmProcessInstance> page = new PageDTO<BpmProcessInstance>();
	 	page = bpmProcessInstanceService.listProcessInstances(page,businessKey,processDefinitionName,loginName);
        return page;
    }


    
    /**
     * 暂停流程实例
     * @param processInstanceId
     */
    @ResponseBody
    @RequestMapping("instance/suspend")
    public Map<String, String> suspend(@RequestParam(value="processInstanceId") String processInstanceId) throws Exception {
    	bpmProcessInstanceService.suspendProcessInstance(processInstanceId);
    	Map<String, String> map = new HashMap<String,String>();
    	map.put("msg", "流程实例挂起（暂停）成功！");
    	return map;
    }
    
    /**
     * 激活流程实例
     * @param processInstanceId
     */
    @ResponseBody
    @RequestMapping("instance/active")
    public Map<String, String> active(@RequestParam(value="processInstanceId") String processInstanceId) throws Exception {
    	bpmProcessInstanceService.activeProcessInstance(processInstanceId);
    	Map<String, String> map = new HashMap<String,String>();
    	map.put("msg", "流程实例激活(启动)成功！");
    	return map;
    }
    
    @ResponseBody
    @RequestMapping("instance/delete")
    public Map<String, String> deleteInstance(@RequestParam(value="processInstanceId") String processInstanceId) throws Exception {
    	SysUser user = SessionHelper.getUser();
    	bpmProcessInstanceService.deleteProcessInstance(processInstanceId, "删除人："+user.getLoginName());
    	Map<String, String> map = new HashMap<String,String>();
    	map.put("msg", "流程实例删除成功！");
    	return map;
    }
    
    /**
     * 查看历史
     */
    @RequestMapping("instance/viewHistory")
    public String viewHistory(@RequestParam(value="processInstanceId")String processInstanceId,Model model) {
    	model.addAttribute("timestamp", new Date().getTime());
        return "bpm/instance/history";
    }
    
    
    /**
     * 获取当前活跃的节点
     * @param processInstanceId
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("instance/lisCurrentActivityIds")
    public List<String> lisCurrentActivityIds(@RequestParam(value="processInstanceId")String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	List<String> currentActivities = new ArrayList<String>(); 
    	 HistoricProcessInstance historicProcessInstance = bpmProcessInstanceService.getHistoricProcessInstance(processInstanceId);
    	 if (historicProcessInstance.getEndTime() == null) {
    		 currentActivities =  bpmActivityService.listActivityIds(processInstanceId);
         } else {
        	 currentActivities = Collections.singletonList(historicProcessInstance.getEndActivityId());
         }
    	 return currentActivities;
    }
    
    /**
     * 获得历史任务信息
     * @param processInstanceId
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("instance/listHistoricTasks")
    public Result listHistoricTasks(@RequestParam(value = "processInstanceId")String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	List<BpmHistoricTaskInstance> list = bpmTaskService.listHistoricTaskInstance(processInstanceId);
    	return Result.resultInfo(ResultCode.SUCCESS, list);
    }
    
    /**
     * 获取节点信息
     * @param processInstanceId
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("instance/listNodeDtos")
    public Result listNodeDtos(@RequestParam(value = "processInstanceId")String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	return Result.resultInfo(ResultCode.SUCCESS, traceService.traceProcessInstance(processInstanceId));
    }
    /**
     * 获取流程实例历史记录生成的图片
     * @param processInstanceId
     * @param response
     * @throws Exception
     */
    @RequestMapping("instance/history/img")
    public void graphHistoryProcessInstanceImg(@RequestParam(value = "processInstanceId")String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	InputStream is = bpmProcessInstanceService.getHistoryProcessInstanceImg(processInstanceId);
        response.setContentType("image/png");
        IOUtils.copy(is, response.getOutputStream());
    }
	
}
