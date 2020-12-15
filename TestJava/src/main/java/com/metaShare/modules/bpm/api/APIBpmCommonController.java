package com.metaShare.modules.bpm.api;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.SessionHelper;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.bpm.entity.BpmStatus;
import com.metaShare.modules.bpm.entity.BpmUserTask;
import com.metaShare.modules.bpm.service.*;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.customize.service.CustomFormJsonTableService;
import com.metaShare.modules.customize.service.CustomFormService;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysMessageService;
import com.metaShare.modules.sys.service.SysOrganizationService;
import com.metaShare.modules.sys.service.SysUserApprovalService;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("api/bpm")
@Api(tags = "流程管理")
@CrossOrigin
public class APIBpmCommonController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(APIBpmCommonController.class);
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
	SysUserApprovalService userApprovalService;
	@Autowired
	BpmActivityService bpmActivityService;
	@Autowired
	TaskService taskService;
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	BpmUserTaskService bpmUserTaskService;
	@Autowired
	CustomFormService customFormService;
	@Autowired
	ProcessEngine processEngine;
	@Autowired
	CustomFormJsonTableService customFormJsonTableService;
    @Autowired
    private SysMessageService sysMessageService;


	/**
	 * 历史任务
	 * 
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws Exception
	 * @return PageDTO<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/history/listTasks", method = { RequestMethod.POST })
	@ApiOperation("历史任务")
	public Result listHistoryTasks(@RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "pageNum") int pageNum,String loginName,
								   @RequestParam(value = "approvalType",required = false)Integer approvalType,
								   @RequestParam(value = "startDate",required = false)String startDate,
								   @RequestParam(value = "endDate",required = false)String endDate,
								   @RequestParam(value = "processDefinitionName",required = false)String processDefinitionName) throws Exception {
		PageDTO<BpmHistoricTaskInstance> page = new PageDTO<BpmHistoricTaskInstance>();
		page.setCurrent(pageNum);
		page.setPages(pageSize);
		SysUser user = new SysUser();
		if (StringUtils.isEmpty(loginName) ) {
			if(null != getUserInfo()){
				loginName = getUserInfo().getLoginName();
			}else{
				return Result.resultInfo(ResultCode.LOGIN_NAME_NULL, "未获取到登录人信息");
			}
		}
		user.setLoginName(loginName);
		if(user==null){
			return Result.resultInfo(ResultCode.USER_NOT_LOGGED_IN, null);
		}
		PageDTO<BpmHistoricTaskInstance> listHistoryTasks = bpmTaskService.listHistoryTasks(user, page,approvalType,startDate,endDate,processDefinitionName);
		return Result.resultInfo(ResultCode.SUCCESS, listHistoryTasks);
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
	@RequestMapping(value= "task/todo/listPersonAndGroupTasks" , method = { RequestMethod.POST })
	@ApiOperation("所有待办,listUserTask")
	public Result listUserTask(int pageSize, int pageNumber,ServletRequest request) throws Exception {
		SysUser user = getUserInfo();
		if(user==null){
			 return  Result.resultInfo(ResultCode.USER_NOT_LOGGED_IN, null);
		}
		PageDTO<BpmTask> task = bpmTaskService.listPersonAndGroupTodoTasks(user,pageNumber,pageSize);
		return  Result.resultInfo(ResultCode.SUCCESS, task);
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
	@ResponseBody
	@RequestMapping(value = "complete", method = { RequestMethod.POST })
	@ApiOperation(" 完成任务")
	public Result complete(@RequestParam(value = "taskId") String taskId,
						   @RequestParam(value = "taskPass") Boolean taskPass,
						   @RequestParam(value = "comment", required = false) String comment,
						   @RequestParam(value = "variables", required = false) String variablesJson) {
		Result res = null;
		Map<String, Object> retMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(variablesJson)) {
			variablesJson = "{}";
		}
		try {
 			SysUser user = getUserInfo();
			Map<String, Object> variables = new HashMap<String, Object>();
			ObjectMapper objectMapper = new ObjectMapper();
			variables = objectMapper.readValue(variablesJson, HashMap.class);

			//2019-02-22 用户需求：用户可以设置下一节点审批人
			//获取下一节点审批人
			//bpmActivityService.nextTaskDefinition(taskId);//此方法停用
			String task = this.getTaskInfo(taskId);
			this.setNextNodeApprovalUser(variables,task);
			
			bpmTaskService.completeTask(taskId, taskPass, comment,user,variables);

			res = Result.resultInfo(ResultCode.SUCCESS, null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("完成任务失败：", e);
			res = Result.resultInfo(ResultCode.SYSTEM_INNER_ERROR, "0");
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
	 * 根据业务ID获取流程
	 * 
	 * @param businessKey
	 * @return
	 * @throws Exception
	 * @return List<BpmTask>
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/mapByProcess",method={RequestMethod.POST})
	@ApiOperation("根据业务ID获取流程")
	public Result mapByProcess(
			@RequestParam(required = true, value = "businessKey") String businessKey) throws Exception {
		Map<String, List<BpmHistoricTaskInstance>> map = new HashMap<String, List<BpmHistoricTaskInstance>>();
		List<BpmStatus> bpmStatusList = bpmStatusService.findByBusinessKey(businessKey);
		List<BpmHistoricTaskInstance> list = new ArrayList<BpmHistoricTaskInstance>();
		if (bpmStatusList.size() > 0) {
			list = bpmTaskService.listHistoricTaskInstance(bpmStatusList.get(0).getProcessInstinceId());
			map.put("rows", list);
		}
		return Result.resultInfo(ResultCode.SUCCESS, map);
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
	@RequestMapping(value = "/revokeProcess",method={RequestMethod.POST})
	@ApiOperation("撤销流程")
	public Result revokeProcess(@RequestParam(required = true, value = "businessKey") String businessKey,
			@RequestParam(required = false, value = "processDefinitionKey") String processDefinitionKey, String type,HttpServletRequest request)
			throws Exception {
		//获取流程标识符
//		if (processDefinitionKey.equals("project")) {
//			List<String> s = new ArrayList<String>();
//			SysOrganization organization = organizationService.selectById(SessionHelper.getUser(request).getOrgId());
//			this.getProjectByOrgId(businessKey, organization, s);
//			for (String business_ : s) {
//				if (!StringUtils.isEmpty(business_)) {
//					processDefinitionKey = business_;
//					break;
//				}
//			}
//		}
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

		return Result.resultInfo(ResultCode.SUCCESS, true);
	}

	/**
	 * 查找项目审批类型
	 * @param businessKey 业务ID
	 * @param org
	 * @param s
	 * @return
	 */
//	private String getProjectByOrgId(String businessKey, SysOrganization org, List<String> s) {
//		//获取审批类型
//		String approvalType = this.getProjectExcuteProcessType(org.getId());
//		//没有审批类型存在则查找上一级部门
//		if (StringUtils.isEmpty(approvalType) && !StringUtils.isEmpty(org.getParentId())) {
//			SysOrganization organization = organizationService.selectById(org.getParentId());
//			getProjectByOrgId(businessKey, organization, s);
//		}
//		
//		s.add(approvalType);
//		return approvalType;
//	}
//	/**
//	 * 根据部门ID获取项目执行流程标识符
//	 * @param orgId 部门ID
//	 * @return
//	 */
//	private String getProjectExcuteProcessType(String orgId) {
//		String businessKey = "";
//		if (orgId.equals("27590391c4de49449a309bc865d259f7") || orgId.equals("b939ac70953d4ce387a0b279f79955a2")) {// 生产运营-检维修【项目立项】
//			businessKey = "Project_approval_scwx";
//		} else if (orgId.equals("50057764") || orgId.equals("50058534") || orgId.equals("50057762") ||
//				 orgId.equals("50057772") || orgId.equals("8804") || orgId.equals("400da13e0d294ba790dfce847def6440")
//				 || orgId.equals("38cecf3ed0d04c4ca3ad4115acadb5de")) {//人力资源-财务资产-生产技术-企管法规-审计监察-运行保障-项目经理【项目立项】
//			businessKey = "Project_approval_rlcwscqgsjyxxm";
//		} else if (orgId.equals("65046114")) {// 经营计划部【项目立项】
//			businessKey = "Project_approval_jyjh";
//		} else if (orgId.equals("50057773") || orgId.equals("65046249")) {// 办公室-质量安全环保【项目立项】
//			businessKey = "Project_approval_bgzl";
//		}
//		return businessKey;
//	}
	
	
	/**
	 * 启动工作流
	 * 
	 * @param businessKey
	 *            业务主键
	 * @param processDefinitionKey
	 *            流程定义键名
	 * @param variablesJson
	 *            流程变量，接受名称为variables的js对象类型。<br/>
	 * @param
	 *
	 * @return
	 */
	//TODO 表名
	@ResponseBody
	@RequestMapping(value = "start" , method={RequestMethod.POST})
	public Result start(@RequestParam(value = "businessKey") String businessKey,
						@RequestParam(value = "businessName")String businessName,
						@RequestParam(value = "processDefinitionKey") String processDefinitionKey,
						@RequestParam(value = "variables", required = false) String variablesJson,
						@RequestParam(value = "tableName",required = false)String tableName,
						@RequestParam(value = "formIdentifity",required = false)String formIdentifity,
						@RequestParam(value = "title",required = false)String title

	) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Result res = null;
		try {
			SysUser user = getUserInfo();
			//判断对应业务是否已经发起流程
			BpmUserTask existTask = bpmUserTaskService.getByBusinessKeyAndProcessKey(businessKey,processDefinitionKey);
			if(existTask != null){
				return Result.resultInfo(ResultCode.FAILURE,"该业务已发起流程，请勿重复提交");
			}
			if (null == user) {
				return Result.resultInfo(ResultCode.LOGIN_NAME_NULL,"未获取到登录人信息");
			}
			// 获取参数
			Map<String, Object> variables = this.getVariablesParam(variablesJson, businessKey);

			// 启动流程
			ProcessInstance processInstance = bpmProcessDefinitionService.startWorkflow(user, businessKey,
					processDefinitionKey, variables,businessName,title);

			// 获取流程任务信息
			Task task = (Task) taskService.createTaskQuery().processInstanceId(processInstance.getId()).active()
					.singleResult();
			
			//构建参数
			retMap.put("taskId", task.getId());
			retMap.put("businessKey", businessKey);
			retMap.put("formKey", task.getFormKey());
			retMap.put("taskDefinitionKey", task.getTaskDefinitionKey());
			retMap.put("params", task.getProcessVariables());
			res = Result.resultInfo(ResultCode.SUCCESS, retMap);

			//保存任务发起信息
			ProcessInstance process = bpmProcessInstanceService.getProcessInstance(processInstance.getProcessInstanceId());
			BpmUserTask bpmUserTask = new BpmUserTask();
			bpmUserTask.setCreateUserId(user.getId());
			bpmUserTask.setProcessId(processInstance.getProcessInstanceId());
			bpmUserTask.setCreateTime(sdf.format(new Date()));
			bpmUserTask.setBusinessName(businessName);
			bpmUserTask.setBusinessKey(businessKey);
			bpmUserTask.setDefinitionKey(processDefinitionKey);
			bpmUserTask.setDefinitionName(process.getProcessDefinitionName());
			bpmUserTask.setApprovalStatus(0);
			bpmUserTask.setProcessName(user.getName() + " 发起 " + (StringUtils.isEmpty(title) ? (process.getProcessDefinitionName()) : title));
			bpmUserTask.setTableName(tableName);
			bpmUserTaskService.insert(bpmUserTask);



            SysUser nextUser = userService.findByLoginName(task.getAssignee());
            if(nextUser!= null){
				SysMessage message = new SysMessage();
				message.setReceiver(nextUser.getId());
				message.setStatus(0L);
				message.setCreateTime(new Date());
				message.setSubject(bpmUserTask.getProcessName());
				message.setContent("您好，您有一个新的审批流程 -->" + businessName);
				sysMessageService.saveAndFlush(message);
			}






			if(StringUtils.isNotEmpty(tableName)){
				//说明是自定义表单提交审批，保存流程实例ID到对应数据
				customFormJsonTableService.updateProcessIdOnTable(tableName,businessKey,processInstance.getProcessInstanceId());
			}
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
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			res = Result.resultInfo(ResultCode.SESSION_EXPIRED,"session过期");
			e.printStackTrace();
		}catch (Exception e) {
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
		Wrapper<SysOrganization> wrapper = Condition.create()
				.eq("deleted", false)
				.eq("user", SessionHelper.getUser().getOrgId());
		List<String> s = new ArrayList<String>();
		List<SysOrganization> organization = organizationService.
				selectList(wrapper);//登录人所在部门
		
//		//获取审批流程标识符集合
//		if (processDefinitionKey.equals("year")) {
//			this.getApprovalTypeByOrgId(businessKey, organization.get(0), s);
//		} else if (processDefinitionKey.equals("project")) {
//			this.getProjectByOrgId(businessKey, organization.get(0), s);
//		}
		
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
	 * 删除流程
	 * @param businessKey
	 * @param processDefinitionKey
	 * @return
	 */
	public Result deleteProcess(String businessKey, String processDefinitionKey){
		//判断流程是否重启
		if(this.isNeedDelete(businessKey,processDefinitionKey)){
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
	private Boolean isNeedDelete(String businessKey, String processDefinitionKey) {
		Boolean flag = false;
		/*if(processDefinitionKey.contains("Month_Plan_") && mpMonthPlanScheduleService.findOne(businessKey).getReviewState().equals("verifyStatus-1")){//月报且待审批
			flag = true;
		}*/
		return flag;
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
	@RequestMapping(value="task/todo/listAllTodoTasks",method={RequestMethod.POST})
	@ApiOperation("所有待办")
	public Result listAllTodoTasks(@RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "pageNumber") int pageNumber) throws Exception {
		PageDTO<BpmTask> page = new PageDTO<BpmTask>();
		page.setCurrent(pageNumber);
		page.setPages(pageSize);
		PageDTO<BpmTask> task = bpmTaskService.listAllTodoTasks(page);
		return  Result.resultInfo(ResultCode.SUCCESS, task);
	}
	
	/**
	 * 根据流程标识符获取第一个节点的ID
	 * 
	 * @param processDefinitionKey
	 *            流程定义key
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/findFirstNodeInfo",method={RequestMethod.POST})
	@ApiOperation("根据流程标识符获取第一个节点的ID")
	public Result findFirstNodeInfo(String processDefinitionKey) {
		// 节点ID
		String firstNodeId = "";

		// 获取流程定义ID
		String processDefinitionId = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).latestVersion().singleResult().getId();

		// 获取节点
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);

		// 获取节点ID
		List<ActivityImpl> ActivityImplList = processDefinitionEntity.getActivities();
		for (ActivityImpl activity : ActivityImplList) {
			if (activity.getProperty("type").equals("startEvent")) {
				firstNodeId = findFirstNodeId(activity);
				break;
			}
		}

		// 返回结果
		return Result.resultInfo(ResultCode.SUCCESS, firstNodeId);
	}

	/**
	 * 获取节点ID
	 * 
	 * @param activity
	 * @return
	 */
	private String findFirstNodeId(ActivityImpl activity) {
		StringBuffer sbf = new StringBuffer();
		List<PvmTransition> OutgoingList = activity.getOutgoingTransitions();
		for (PvmTransition out : OutgoingList) {
			PvmActivity pvmActivity = out.getDestination(); // 获取线路的终点节点
			sbf.append(pvmActivity.getId()).append(",");
		}
		if (sbf.length() > 0) {
			return sbf.toString().substring(0, sbf.length() - 1);
		}
		return "";
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
	@RequestMapping(value="task/todo/listPersonTasks",method={RequestMethod.POST} )
	@ApiOperation("个人待办")
	public Result listPersonTasks(@RequestParam(value = "pageSize") int pageSize,
								  @RequestParam(value = "pageNum") int pageNum,String loginName,
								  @RequestParam(value = "approvalType",required = false)Integer approvalType,
								  @RequestParam(value = "startDate",required = false)String startDate,
								  @RequestParam(value = "endDate",required = false)String endDate,
								  @RequestParam(value = "type")int type,
								  @RequestParam(value = "processDefinitionName",required = false)String processDefinitionName,
								  @RequestParam(value = "applyUserName",required = false)String applyUserName) throws Exception {
		//type为0是查看全部待办，为1时查看个人
		PageDTO<BpmTask> page = new PageDTO();
		page.setCurrent(pageNum);
		page.setPages(pageSize);
		if (StringUtils.isEmpty(loginName) ) {
			if(null != getUserInfo()){
				loginName = getUserInfo().getLoginName();
			}else{
				return Result.resultInfo(ResultCode.LOGIN_NAME_NULL, "未获取到登录人信息");
			}
		}
		SysUser user = new SysUser();
		if(type == 1){
			user.setLoginName(loginName);
		}
		return Result.resultInfo(ResultCode.SUCCESS, bpmTaskService.listPersonTodoTasks(user, page,approvalType,startDate,endDate,processDefinitionName,applyUserName));
	}
	
	/**
	 * 根据流程定义查询个人的待办任务
	 * 
	 * @return
	 * @throws Exception
	 * @return String
	 * @author: zhaojie/zh_jie@163.com
	 */
	@ResponseBody
	@RequestMapping(value="task/todo/listPersonTasksAndProcessDefinitionKey",method={RequestMethod.POST} )
	@ApiOperation("根据流程定义查询个人的待办任务")
	public Result listPersonTodoTasks(@RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "pageNumber") int pageNumber,
			@RequestParam(value="processDefinitionKey", required=true) String processDefinitionKey) throws Exception {
		SysUser user = getUserInfo();
		List<BpmTask> list = bpmTaskService.listPersonTodoTasksAndProcessDefinitionKey(user,processDefinitionKey);
		return Result.resultInfo(ResultCode.SUCCESS, new PageTool<BpmTask>().getPage(list, pageSize, pageNumber));
	}

}
