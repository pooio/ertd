package com.metaShare.modules.bpm.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmProcessInstance;
import com.metaShare.modules.bpm.service.BpmActivityService;
import com.metaShare.modules.bpm.service.BpmProcessInstanceService;
import com.metaShare.modules.bpm.service.BpmTaskService;
import com.metaShare.modules.bpm.service.TraceService;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 流程实例controller
 * 
 *
 */
@Controller
@RequestMapping("api/bpm")
@Api(tags="流程实例")
public class APIBpmProcessInstanceController  extends BaseController {
	private static Logger log = LoggerFactory.getLogger(APIBpmProcessInstanceController.class);
	
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
    @Autowired
	ProcessEngine processEngine;
    
	// 权限字符串前缀
	protected String identity = "bpm:api:APIBpmProcessInstance";
	protected String checkBeforeSaveMsg = "";
	protected String checkBeforeUpdateMsg = "";
	protected String checkBeforeDeleteMsg = "";
	
	/**
	 * 激活挂起删除
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("instance")
    public String instance() throws Exception {
    	return "/bpm/instance/main";
    }

	/**
	 * 获取流程实例数据
	 * @param pageSize
	 * @param pageNum
	 * @param businessKey
	 * @param processDefinitionName
	 * @param loginName
	 * @param searchable
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
    @RequestMapping(value="instance/list",method={RequestMethod.POST})
	@ApiOperation("获取流程实例数据")
    public Result list(@RequestParam(value="pageSize")int pageSize,
					   @RequestParam(value="pageNum")int pageNum,
					   @RequestParam(value="businessKey",required=false) String businessKey,
					   @RequestParam(value="processDefinitionName",required=false) String processDefinitionName,
					   @RequestParam(value="loginName",required=false) String loginName,
					   @RequestParam(value = "processName",required = false)String processName,
					   @RequestParam(value = "type",required = false)Integer type) throws Exception {
		
		/*PageDTO<BpmProcessInstance> page = new PageDTO<BpmProcessInstance>();
		page.setCurrent(pageNum);
		page.setPages(pageSize);
	 	page = bpmProcessInstanceService.listProcessInstances(page,businessKey,processDefinitionName,loginName);
*/
		PageDTO<BpmProcessInstance> bpmProcessInstancePageDTO = bpmProcessInstanceService.listAllProcessInstances(pageNum, pageSize, processDefinitionName, loginName,processName,type);
		return Result.resultInfo(ResultCode.SUCCESS, bpmProcessInstancePageDTO);
    }


    
    /**
     * 暂停流程实例
     * @param processInstanceId
     */
    @ResponseBody
    @RequestMapping("instance/suspend")
    public Result suspend(@RequestParam(value="processInstanceId") String processInstanceId) throws Exception {
    	bpmProcessInstanceService.suspendProcessInstance(processInstanceId);
    	return Result.resultInfo(ResultCode.SUCCESS, null);
    }
    
    /**
     * 激活流程实例
     * @param processInstanceId
     */
    @ResponseBody
    @RequestMapping("instance/active")
    public Result active(@RequestParam(value="processInstanceId") String processInstanceId) throws Exception {
    	bpmProcessInstanceService.activeProcessInstance(processInstanceId);
    	return Result.resultInfo(ResultCode.SUCCESS,null);
    }
    /**
     * 删除流程实例
     * @param processInstanceId
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("instance/delete")
    public Result deleteInstance(@RequestParam(value="processInstanceId") String processInstanceId,ServletRequest request) throws Exception {
    	Result result = null;
    	try {
			SysUser user = getUserInfo();
			if(user==null){
				return Result.resultInfo(ResultCode.USER_NOT_LOGGED_IN, null);
			}
			bpmProcessInstanceService.deleteProcessInstance(processInstanceId, "删除人："+user.getLoginName());
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			result = Result.resultInfo(ResultCode.SESSION_EXPIRED,"session过期");
			e.printStackTrace();
		}catch (Exception e) {
			result = Result.resultInfo(ResultCode.FAILURE,null);
			e.printStackTrace();
		}
    	return result;
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
    public Result lisCurrentActivityIds(@RequestParam(value="processInstanceId")String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	List<String> currentActivities = new ArrayList<String>(); 
    	 HistoricProcessInstance historicProcessInstance = bpmProcessInstanceService.getHistoricProcessInstance(processInstanceId);
    	 if (historicProcessInstance.getEndTime() == null) {
    		 currentActivities =  bpmActivityService.listActivityIds(processInstanceId);
         } else {
        	 currentActivities = Collections.singletonList(historicProcessInstance.getEndActivityId());
         }
    	 return Result.resultInfo(ResultCode.SUCCESS, currentActivities);
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
    	if (StringUtils.isEmpty(processInstanceId)) {
    		return Result.resultInfo(ResultCode.PARAMS_IS_NULL, "参数为空");
		}
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
    	return Result.resultInfo(ResultCode.SUCCESS, 
    			traceService.traceProcessInstance(processInstanceId));
    }
    /**
     * 获取流程实例历史记录生成的图片
     * @param processInstanceId
     * @param responseimg
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
