package com.metaShare.modules.bpm.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.customize.service.CustomFormService;
import com.metaShare.modules.sys.entity.SysMessage;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.SessionHelper;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.bpm.entity.BpmStatus;
import com.metaShare.modules.bpm.service.BpmActivityService;
import com.metaShare.modules.bpm.service.BpmModelService;
import com.metaShare.modules.bpm.service.BpmProcessDefinitionService;
import com.metaShare.modules.bpm.service.BpmProcessInstanceService;
import com.metaShare.modules.bpm.service.BpmStatusService;
import com.metaShare.modules.bpm.service.BpmTaskService;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysOrganizationService;
import com.metaShare.modules.sys.service.SysUserApprovalService;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;

/**
 * 工作流相关controller
 * 
 *
 */
@Controller
@RequestMapping("bpm")
@Api(tags = "流程管理WEB")
public class BpmCommonController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(BpmCommonController.class);
	
	@Autowired
	BpmProcessDefinitionService bpmProcessDefinitionService;
	@Autowired
	BpmProcessInstanceService bpmProcessInstanceService;
	@Autowired
	BpmTaskService bpmTaskService;
	@Autowired
	BpmStatusService bpmStatusService;
	@Autowired
	BpmModelService bpmModelService;
	@Autowired
	SysUserService userService;
	@Autowired
	SysOrganizationService organizationService;
	@Autowired
	BpmActivityService bpmActivityService;
	@Autowired
	TaskService taskService;
	@Autowired
	SysUserApprovalService userApprovalService;
	@Autowired
	CustomFormService customFormService;
	/**
	 * 启动工作流
	 * 
	 * @param businessKey
	 *            业务主键
	 * @param processDefinitionKey
	 *            流程定义键名
	 * @param variables
	 *            流程变量，接受名称为variables的js对象类型。<br/>
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "start" , method={RequestMethod.POST})
	public Result start1(@RequestParam(value = "businessKey") String businessKey,
			@RequestParam(value = "processDefinitionKey") String processDefinitionKey,
			@RequestParam(value = "variables", required = false) String variablesJson) {
		//SysUser user = SessionHelper.getUser();
		SysUser user = getUserInfo();
		Result res = start(businessKey, processDefinitionKey, variablesJson,user);
		return res;
	}

	public Result start(String businessKey, String processDefinitionKey, String variablesJson,SysUser user) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Result res = null;
		try {

			// 获取参数
			Map<String, Object> variables = this.getVariablesParam(variablesJson, businessKey);

			// 启动流程
			ProcessInstance processInstance = bpmProcessDefinitionService.startWorkflow(user, businessKey,
					processDefinitionKey, variables,null,null);
			// 获取流程任务信息
			Task task = (Task) taskService.createTaskQuery().processInstanceId(processInstance.getId()).active()
					.singleResult();
			
			//构建参数
			retMap.put("taskId", task.getId());
			retMap.put("businessKey", businessKey);
			retMap.put("formKey", task.getFormKey());
			retMap.put("taskDefinitionKey", task.getTaskDefinitionKey());
			retMap.put("params", task.getProcessVariables());
			retMap.put("processDefinitionKey", processDefinitionKey);
			res = Result.resultInfo(ResultCode.SUCCESS, retMap);
		} catch (ActivitiException e) {
			if (e.getMessage().indexOf("no processes deployed with key") != -1) {
				logger.info("没有部署流程!");
				logger.warn("没有部署流程!", e);
				retMap.put("flag", "0");
				retMap.put("msg", "没有部署流程！");
				res = Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, retMap);
			} else {
				logger.info("启动流程失败：");
				logger.error("启动流程失败：", e);
				retMap.put("flag", "0");
				retMap.put("msg", "系统内部错误！");
				res = Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, retMap);
			}
		} catch (Exception e) {
			logger.info("启动流程失败：");
			logger.error("启动流程失败：", e);
			retMap.put("flag", "0");
			retMap.put("msg", "系统内部错误！");
			res = Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, retMap);
		}
		return res;
	}
	
	/**
	 * 获取流程标识符
	 * @param processDefinitionKey 标识符
	 * @param businessKey 业务主键
	 * @return
	 */
	private String getProcessDefinitionKey(String processDefinitionKey, String businessKey) {
		//参数
		List<String> s = new ArrayList<String>();
		SysOrganization organization = organizationService.
				selectById(SessionHelper.getUser().getOrgId());//登录人所在部门
		
		//获取审批流程标识符集合
		if (processDefinitionKey.equals("year")) {
			this.getApprovalTypeByOrgId(businessKey, organization, s);
		} else if (processDefinitionKey.equals("project")) {
			this.getProjectByOrgId(businessKey, organization, s);
		}
		
		//获取流程标识符
		for (String business_ : s) {
			if (!StringUtils.isEmpty(business_)) {
				processDefinitionKey = business_;
				break;
			}
		}
		
		return processDefinitionKey;
	}
	
	
	/**
	 * 查找项目审批类型
	 * @param businessKey 业务ID
	 * @param org
	 * @param s
	 * @return
	 */
	private String getProjectByOrgId(String businessKey, SysOrganization org, List<String> s) {
		//获取审批类型
		String approvalType = this.getProjectExcuteProcessType(org.getId());
		//没有审批类型存在则查找上一级部门
		if (StringUtils.isEmpty(approvalType) && !StringUtils.isEmpty(org.getParentId())) {
			SysOrganization organization = organizationService.selectById(org.getParentId());
			getProjectByOrgId(businessKey, organization, s);
		}
		
		s.add(approvalType);
		return approvalType;
	}

	/**
	 * 查找年度计划审批类型
	 * @param businessKey 业务DI
	 * @param org 组织机构
	 * @param s 
	 * @return
	 */
	private String getApprovalTypeByOrgId(String businessKey, SysOrganization org, List<String> s) {
		//获取审批类型
		String approvalType = this.getApprovalType(org.getId());
		//没有审批类型存在则查找上一级部门
		if (StringUtils.isEmpty(approvalType) && !StringUtils.isEmpty(org.getParentId())) {
			SysOrganization organization = organizationService.selectById(org.getParentId());
			getApprovalTypeByOrgId(businessKey, organization, s);
		}
		s.add(approvalType);
		return approvalType;
	}

	/**
	 * 任务到达消息展示页面
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 * @date: 2016年1月26日 上午8:55:12
	 */
	public String taskMessagePage(@RequestParam(value = "taskId") String taskId, Model model) throws Exception {
		HistoricTaskInstance taskInstance = bpmTaskService.findHistoricTaskById(taskId);
		Map<String, Object> variables = taskInstance.getProcessVariables();

		model.addAttribute("id", variables.get(BpmConstants.BUSINESS_KEY));
		model.addAttribute(BpmConstants.BUSINESS_INFO, variables.get(BpmConstants.BUSINESS_INFO));
		model.addAttribute("processInstanceId", taskInstance.getProcessInstanceId());
		model.addAttribute("formKey", taskInstance.getFormKey());
		String params = "";
		Set<String> keys = variables.keySet();
		for (String key : keys) {
			params += "&" + key + "=" + variables.get(key);
		}
		model.addAttribute("params", params);

		model.addAttribute("timestamp", new Date().getTime());
		return "bpm/task/message/main";
	}

	public String todoMainPage() throws Exception {
		return "bpm/task/todo/main";
	}

	public String allTodo() throws Exception {
		return "bpm/task/todo/all_todo";
	}

	public String groupTodo() throws Exception {
		return "bpm/task/todo/group_todo";
	}

	public String personTodo() throws Exception {
		return "bpm/task/todo/person_todo";
	}

	/**
	 * 所有待办，包括待所属组待签收的
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listPersonAndGroupTasks", method={RequestMethod.POST})
	public Result listUserTask(int pageSize, int pageNumber) throws Exception {
		PageDTO<BpmTask> task = bpmTaskService.listPersonAndGroupTodoTasks(SessionHelper.getUser(), pageSize,pageNumber);
		 return  Result.resultInfo(ResultCode.SUCCESS, task);
	}

	/**
	 * 根据任务定义key(taskDefinitionKey)，获取 所有待办，包括待所属组待签收的
	 * 
	 * @param taskDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年4月19日 下午4:07:54
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listPersonAndGroupTasksByTaskKey", method={RequestMethod.POST})
	public Result listUserTaskByTaskKey(
			@RequestParam(required = false, value = "taskDefinitionKey") String taskDefinitionKey) throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS,
				bpmTaskService.listPersonAndGroupTodoTasksWithNoPage(SessionHelper.getUser(), taskDefinitionKey));
	}

	/**
	 * 所属组待签收的
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listGroupTasks", method={RequestMethod.POST})
	public Result listGroupTasks(PageDTO<BpmTask> page) throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS, bpmTaskService.listGroupTodoTasks(SessionHelper.getUser(), page));
	}

	/**
	 * 个人待办
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listPersonTasks", method={RequestMethod.POST})
	public Result listPersonTodoTasks(PageDTO<BpmTask> page,
									  @RequestParam(value = "approvalType",required = false)Integer approvalType,
									  @RequestParam(value = "startDate",required = false)String startDate,
									  @RequestParam(value = "endDate",required = false)String endDate,
									  @RequestParam(value = "applyUserName",required = false) String applyUserName) throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS,
				bpmTaskService.listPersonTodoTasks(SessionHelper.getUser(), page,approvalType,startDate,endDate,applyUserName,null));
	}

	/**
	 * 委派任务
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年3月16日 下午1:09:16
	 */
	@ResponseBody
	@RequestMapping(value="task/delegate/listTasks", method={RequestMethod.POST})
	public Result listDelegateTasks(PageDTO<BpmTask> page) throws Exception {
		// 查询委派的任务包含已完成和未完成的
		return Result.resultInfo(ResultCode.SUCCESS, 
				bpmTaskService.listDelegatedTasks(null, SessionHelper.getUser(), page));
	}

	/**
	 * 审批历史页面
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年3月16日 下午2:39:47
	 */
	@RequestMapping(value="task/history", method={RequestMethod.POST})
	public String historyTasks() throws Exception {
		return "bpm/task/history/main";
	}

	/**
	 * 委派任务页面
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年3月16日 下午2:39:54
	 */
	@RequestMapping(value="task/delegate", method={RequestMethod.POST})
	public String delegateTasks() throws Exception {
		return "bpm/task/delegate/main";
	}

	/**
	 * 历史任务
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws Exception
	 * @return PageDTO<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/history/listTasks", method={RequestMethod.POST})
	public Result listHistoryTasks(@RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "pageNumber") int pageNumber) throws Exception {
		PageDTO<BpmHistoricTaskInstance> page = new PageDTO<BpmHistoricTaskInstance>();
		PageDTO<BpmHistoricTaskInstance> listHistoryTasks = bpmTaskService.listHistoryTasks(SessionHelper.getUser(), page,null,null,null,null);
		return Result.resultInfo(ResultCode.SUCCESS, listHistoryTasks);
	}

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 * @return Map<String,String>
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value = "claim", method={RequestMethod.POST})
	public Result claim(@RequestParam(value = "taskId") String taskId) {
		Map<String, String> retMap = new HashMap<String, String>();
		Result res = null;
		try {
			bpmTaskService.claimTask(taskId, SessionHelper.getUser());
			retMap.put("flag", "1");
			retMap.put("msg", "签收任务成功！");
			Result.resultInfo(ResultCode.SUCCESS, retMap);
		} catch (Exception e) {
			logger.error("签收任务失败失败：", e);
			retMap.put("flag", "0");
			retMap.put("msg", "签收任务失败，系统内部错误！");
			Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, retMap);
		}
		return res;
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param taskPass
	 * @param comment
	 * @param variablesJson
	 * @return
	 * @return Map<String,String>
	 * @author: zhaojie/zh_jie@163.com
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "complete", method={RequestMethod.POST})
	public Result complete(@RequestParam(value = "taskId") String taskId,
						   @RequestParam(value = "taskPass") Boolean taskPass,
						   @RequestParam(value = "comment", required = false) String comment,
						   @RequestParam(value = "variables", required = false) String variablesJson) {
		Result res = null;
		try {
			
			Map<String, Object> variables = new HashMap<String, Object>();
			ObjectMapper objectMapper = new ObjectMapper();
			variables = objectMapper.readValue(variablesJson, HashMap.class);
			SysUser user = getUserInfo();
			//2019-02-22 用户需求：用户可以设置下一节点审批人
			//获取下一节点审批人
			//bpmActivityService.nextTaskDefinition(taskId);//此方法停用
			String task = this.getTaskInfo(taskId);
			this.setNextNodeApprovalUser(variables,task);
			
			bpmTaskService.completeTask(taskId, taskPass, comment,user, variables);
			res = Result.resultInfo(ResultCode.SUCCESS, "成功完成任务！");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("完成任务失败：", e);
			res = Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, "完成任务失败，系统内部错误！");
		}
		return res;
	}
	
	/**
	 * 根据任务Id获取流程标识符
	 * @param taskId 任务ID
	 * @return
	 */
	private String getTaskInfo(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task==null){
			return "";
		}
		BpmStatus bpmStatus = bpmStatusService.
				findByProcessInstinceId(task.getProcessInstanceId());
		return bpmStatus.getProcessDefinitionKey();
	}

	//获取下一级审批人并保存
	private void setNextNodeApprovalUser(Map<String, Object> variables,String processFlag) {
		Object nextProcessId = variables.get("nextProcessId");//节点ID
		Object nextBusinessId = variables.get("nextBusinessId");//业务ID
		Object nextSpprovalUsers = variables.get("nextSpprovalUsers");//审批人ID
		String nextProcessIdString = null;
		String nextBusinessIdString = null;
		if(null != nextProcessId){
			nextProcessIdString = nextProcessId.toString();
		}
		if(null != nextBusinessId){
			nextBusinessIdString = nextBusinessId.toString();
		}
		
		//确定审批人
		if(null != nextSpprovalUsers){
			userApprovalService.saveApprovalUser(nextProcessIdString,nextBusinessIdString,nextSpprovalUsers.toString(),processFlag);
		}
		
		//删除下级审批人
		variables.remove("nextProcessId");
		variables.remove("nextBusinessId");
		variables.remove("nextSpprovalUsers");
	}

	/**
	 * 完成任务
	 * 
	 * @param taskId
	 * @param taskPass
	 * @param comment
	 * @param variablesJson
	 * @return
	 * @return Map<String,String>
	 * @author: zhaojie/zh_jie@163.com
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "completeApp", method={RequestMethod.POST})
	public Result completeApp(@RequestParam(value = "taskId") String taskId,
			@RequestParam(value = "token") String token, @RequestParam(value = "taskPass") Boolean taskPass,
			@RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "variables", required = false) String variablesJson) {
		Result res = null;
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			ObjectMapper objectMapper = new ObjectMapper();
			variables = objectMapper.readValue(variablesJson, HashMap.class);

			bpmTaskService.completeTask4App(token, taskId, taskPass, comment, variables);
			res =  Result.resultInfo(ResultCode.SUCCESS, "成功完成任务！");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("完成任务失败：", e);
			res =  Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, "完成任务失败，系统内部错误！");
		}
		return res;
	}

	/**
	 * 获取流程模型中的UserTask信息
	 * 
	 * @param processDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<UserTask>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年1月15日 下午6:23:28
	 */
	@ResponseBody
	@RequestMapping(value="model/tasks", method={RequestMethod.POST})
	public Result getModelTasksInfo(@RequestParam(value = "processDefinitionKey") String processDefinitionKey)
			throws Exception {
		BpmnModel bpmnModel = bpmModelService.getBpmModelByProcessDefinitionKey(processDefinitionKey);
		List<UserTask> userTasks = new ArrayList<UserTask>();
		if (bpmnModel != null) {
			Collection<FlowElement> elements = bpmnModel.getProcesses().get(0).getFlowElements();
			for (FlowElement element : elements) {
				if ("UserTask".equals(element.getClass().getSimpleName())
						&& StringUtils.isNotEmpty(element.getName())) {
					UserTask userTask = (UserTask) element;
					userTasks.add(userTask);
				}
			}
		}
		return Result.resultInfo(ResultCode.SUCCESS, userTasks);
	}

	/**
	 * 选择审批人页面
	 * 
	 * @param model
	 * @return
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年1月18日 上午8:16:52
	 */
	@RequestMapping(value="/selectAssignee", method={RequestMethod.POST})
	public String selectUser(Model model) {
		model.addAttribute("timestamp", new Date().getTime());
		return "bpm/selectAssignee";
	}

	/**
	 * 根据业务ID获取流程实例ID
	 * 
	 * @param taskDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年4月19日 下午4:07:54
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/businessKey", method={RequestMethod.POST})
	public Result listByBusinessId(@RequestParam(required = true, value = "businessKey") String businessKey)
			throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS, bpmStatusService.findByBusinessKey(businessKey));
	}

	/**
	 * 根据业务ID获取流程
	 * 
	 * @param taskDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listByProcess", method={RequestMethod.POST})
	public Result listByProcess(
			@RequestParam(required = true, value = "businessKey") String businessKey) throws Exception {
		List<BpmStatus> bpmStatusList = bpmStatusService.findByBusinessKey(businessKey);
		List<BpmHistoricTaskInstance> list = new ArrayList<BpmHistoricTaskInstance>();
		if (bpmStatusList.size() > 0) {
			list = bpmTaskService.listHistoricTaskInstance(bpmStatusList.get(0).getProcessInstinceId());
		}
		return Result.resultInfo(ResultCode.SUCCESS, list);
	}

	/**
	 * 根据业务ID获取流程
	 * 
	 * @param taskDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/mapByProcess", method={RequestMethod.POST})
	public Result mapByProcess(
			@RequestParam(required = true, value = "businessKey") String businessKey) throws Exception {
		Map<String, List<BpmHistoricTaskInstance>> map = new HashMap<String, List<BpmHistoricTaskInstance>>();
		List<BpmStatus> bpmStatusList = bpmStatusService.findByBusinessKey(businessKey);
		List<BpmHistoricTaskInstance> list = new ArrayList<BpmHistoricTaskInstance>();
		if (bpmStatusList.size() > 0) {
			list = bpmTaskService.listHistoricTaskInstance(bpmStatusList.get(0).getProcessInstinceId());
		}
		map.put("rows", list);
		return Result.resultInfo(ResultCode.SUCCESS, map);
	}

	/**
	 * 根据业务ID获取流程----重大项目历史版本专用
	 * 
	 * @param taskDefinitionKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/mapByProcessByHis", method={RequestMethod.POST})
	public Result mapByProcessByHis(
			@RequestParam(required = true, value = "businessKey") String businessKey) throws Exception {
		
		return Result.resultInfo(ResultCode.SUCCESS,null);
	}
	
	/**
	 * 撤销流程
	 * @param businessKey 业务ID
	 * @param processDefinitionKey 流程标识符
	 * @param type 类型
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/revokeProcess", method={RequestMethod.POST})
	public Result revokeProcess(@RequestParam(required = true, value = "businessKey") String businessKey,
			@RequestParam(required = false, value = "processDefinitionKey") String processDefinitionKey, String type)
			throws Exception {
		//获取流程标识符
		if (processDefinitionKey.equals("project")) {
			List<String> s = new ArrayList<String>();
			SysOrganization organization = organizationService.selectById(SessionHelper.getUser().getOrgId());
			this.getProjectByOrgId(businessKey, organization, s);
			for (String business_ : s) {
				if (!StringUtils.isEmpty(business_)) {
					processDefinitionKey = business_;
					break;
				}
			}
		}
		
		//删除可选用户信息
		userApprovalService.deletechoseInfo(businessKey,processDefinitionKey);
		//删除流程状态表信息
		bpmStatusService.deleteByBusinessKeyAndDefineKey(businessKey, processDefinitionKey);
		// 根据businessKey查找act_ru_execution运行时流程执行实例表的proc_inst_id
		List<String> procinstid = bpmStatusService.findactruexecution(businessKey);
		// 根据procinstid删除关联表信息;
		if (procinstid.size() != 0) {
			for (String m : procinstid) {
				bpmStatusService.deleteactruidentitylink(m);
				bpmStatusService.deleteactrutask(m);
				bpmStatusService.deleteactruvariable(m);
			}
			bpmStatusService.deleteactruexecution(businessKey);
		}
		
		//修改业务状态
		/*if (type.equals("car")) {
			ApprovalForCarService approvalForCarService = SpringHelper.getBeanByClassname(ApprovalForCarService.class);
			approvalForCarService.updateStatus(businessKey, "approvalStatus-unsubmitted");
		} */
		
		return Result.resultInfo(ResultCode.SUCCESS, true);

	}

	/**
	 * 根据部门ID获取年度计划上报审批流程标识符
	 * @param orgId 部门ID
	 * @return
	 */
	private String getApprovalType(String orgId) {
		String businessKey = "";
		if (orgId.equals("65046114")) {// 经营计划部
			businessKey = "Report_annual_jyjh";
		} else{
			businessKey = "Report_annual_ElevenDepartments";
		}
		return businessKey;
	}

	/**
	 * 根据部门ID获取项目执行流程标识符
	 * @param orgId 部门ID
	 * @return
	 */
	private String getProjectExcuteProcessType(String orgId) {
		String businessKey = "";
		if (orgId.equals("27590391c4de49449a309bc865d259f7") || orgId.equals("b939ac70953d4ce387a0b279f79955a2")) {// 生产运营-检维修【项目立项】
			businessKey = "Project_approval_scwx";
		} else if (orgId.equals("50057764") || orgId.equals("50058534") || orgId.equals("50057762") ||
				 orgId.equals("50057772") || orgId.equals("8804") || orgId.equals("400da13e0d294ba790dfce847def6440")
				 || orgId.equals("38cecf3ed0d04c4ca3ad4115acadb5de")) {//人力资源-财务资产-生产技术-企管法规-审计监察-运行保障-项目经理【项目立项】
			businessKey = "Project_approval_rlcwscqgsjyxxm";
		} else if (orgId.equals("65046114")) {// 经营计划部【项目立项】
			businessKey = "Project_approval_jyjh";
		} else if (orgId.equals("50057773") || orgId.equals("65046249")) {// 办公室-质量安全环保【项目立项】
			businessKey = "Project_approval_bgzl";
		}
		return businessKey;
	}

	/**
	 * 删除流程
	 * @param businessKey
	 * @param processDefinitionKey
	 * @return
	 */
	public Result deleteProcess(String businessKey, String processDefinitionKey){
		//判断流程是否重启
		if((boolean) this.isNeedDelete(businessKey,processDefinitionKey).getData()){
			deleteExistProcess(businessKey,processDefinitionKey);
			return Result.resultInfo(ResultCode.SUCCESS, true);
		}
		return Result.resultInfo(ResultCode.SUCCESS, false);
	}
	
	/**
	 * 判断流程是否需要删除
	 * @param businessKey
	 * @param processDefinitionKey
	 * @return
	 */
	private Result isNeedDelete(String businessKey, String processDefinitionKey) {
		Boolean flag = false;
		/*if(processDefinitionKey.contains("Month_Plan_") && mpMonthPlanScheduleService.findOne(businessKey).getReviewState().equals("verifyStatus-1")){//月报且待审批
			flag = true;
		}*/
		
		return Result.resultInfo(ResultCode.SUCCESS, flag);
	}

	/**
	 * 查处流程
	 * @param businessKey
	 * @param processDefinitionKey
	 * @return
	 */
	public Result deleteExistProcess(String businessKey, String processDefinitionKey) {
		bpmStatusService.deleteByBusinessKeyAndDefineKey(businessKey, processDefinitionKey);
		// 根据businessKey查找act_ru_execution运行时流程执行实例表的proc_inst_id
		List<String> procinstid = bpmStatusService.findactruexecution(businessKey);
		// 根据procinstid删除关联表信息;
		if (procinstid.size() != 0) {
			for (String m : procinstid) {
				bpmStatusService.deleteactruidentitylink(m);
				bpmStatusService.deleteactrutask(m);
				bpmStatusService.deleteactruvariable(m);
			}
			bpmStatusService.deleteactruexecution(businessKey);

		}
		return Result.resultInfo(ResultCode.SUCCESS, true);
	}
	
	/**
	 * 生成可用的业务主键
	 * 
	 * @return id
	 */
	private String getBusinessKey() {
		boolean flag = true;
		String id = null;
		while (flag) {
			id = UUID.randomUUID().toString().replace("-", "");
			if (bpmStatusService.checkProcessStart(id)) {
				flag = false;
			}
		}
		return id;
	}
	
	/**
	 * 获取参数
	 * 
	 * @param variablesJson
	 *            前台参数
	 * @param businessKey
	 *            业务主键
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getVariablesParam(String variablesJson, String businessKey) throws Exception {
		Map<String, Object> variables = new HashMap<String, Object>();
		ObjectMapper objectMapper = new ObjectMapper();

		// 参数转为健值对
		if (StringUtils.isNotEmpty(variablesJson)) {
			variables = objectMapper.readValue(variablesJson, HashMap.class);
		}

		// 修改参数
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getValue() instanceof String) {
				String value = ((String) entry.getValue()).replaceAll("\\{\\{id\\}\\}", businessKey);
				entry.setValue(value);
			}
		}

		return variables;
	}
	
	/**
	 * 所有待办
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listAllTodoTasks", method={RequestMethod.POST})
	public Result listAllTodoTasks(PageDTO<BpmTask> page) throws Exception {
		 PageDTO<BpmTask> task = bpmTaskService.listAllTodoTasks(page);
		 return Result.resultInfo(ResultCode.SUCCESS, task);
	}
}
