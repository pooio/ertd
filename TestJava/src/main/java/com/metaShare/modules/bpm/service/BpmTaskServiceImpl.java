package com.metaShare.modules.bpm.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.metaShare.modules.bpm.entity.BpmUserTask;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.service.SysMessageService;
import io.swagger.models.auth.In;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.NativeHistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.*;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.utils.SessionHelper;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.bpm.entity.BpmStatus;
import com.metaShare.modules.bpm.exception.BpmException;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserApprovalService;
import com.metaShare.modules.sys.service.SysUserService;


@Service(value="bpmTaskService")
public class BpmTaskServiceImpl implements BpmTaskService {

	@Autowired
	protected RuntimeService runtimeService; 
	@Autowired 
	protected HistoryService historyService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	ProcessEngine ProcessEngine;
	@Autowired
	SysUserService userService;
	@Autowired
    protected RepositoryService repositoryService;
	
	@Autowired
	protected BpmCoreService bpmCoreService;
	
	@Autowired
	private BpmStatusService bpmStatusService;
	@Autowired
	private SysUserApprovalService userApprovalService;
	@Autowired
	private BpmUserTaskService bpmUserTaskService;
	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private BpmModelService bpmModelService;
	@Autowired
	private SysMessageService sysMessageService;

	@Override
	public void claimTask(String taskId,SysUser user) {
		 taskService.claim(taskId, user.getLoginName());
	}

	@Override
	public void completeTask(String taskId,Boolean taskPass,String comment,SysUser user, Map<String, Object> variables) throws Exception {
		if(variables==null){
			variables = new HashMap<String, Object>();
		}
		Set<Entry<String, Object>> variablesSet = variables.entrySet();
		if(StringUtils.isEmpty(comment)){
			comment = taskPass ? "审批通过":"审批不通过";
		}
		//如果设置了会签负责人变量,并且设置的变量为逗号分隔的字符串方式，这里转为List
        for(Entry<String, Object> entry : variablesSet){
            if(entry.getKey().endsWith("AssigneeSignList")){
                if(entry.getValue() instanceof String){
                    if(StringUtils.isNotEmpty((String)entry.getValue()) ){
                        entry.setValue( Arrays.asList(((String) entry.getValue()).split(",")));
                    }
                }
            }
        }
		
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(task==null){
			throw new BpmException("此任务已审批，无需再审批");
		}
		BpmStatus bpmStatus = bpmStatusService.findByProcessInstinceId(task.getProcessInstanceId());
		String assignee = task.getAssignee();
		String owner = task.getOwner();
		
		Map<String, Object> valiables = runtimeService.getVariables(task.getExecutionId());
		
		//业务主键ID
		String id = (String) valiables.get(BpmConstants.BUSINESS_KEY);
		
		//2019-2-26用户新的需求
		String processId_ = bpmStatus.getProcessInstinceId();
		//userApprovalService.updateApproval(id,user.getId());
//		variables.put(BpmConstants.PROCESS_IS_NEXT_NODE,userApprovalService.isNextNode(id,user.getId(),processId_));
//		variables.put(BpmConstants.PROCESS_IS_CHOOSE_USER,userApprovalService.isSureApproval(id,processId_));
		
		//流程标识
    	String processKey = bpmStatus.getProcessDefinitionKey();
    	int type = 0;
      //修改业务状态
		BpmUserTask bpmUserTask = bpmUserTaskService.selectByTaskId(task.getProcessInstanceId());
		String tableName = bpmUserTask.getTableName();
		if(StringUtils.isNotEmpty(tableName)){
			if("kb_content".equals(tableName)){
				type = 1;
			}
			bpmUserTaskService.updateApprovalStatusSelf(bpmUserTask.getId(),taskPass?1:3,processId_);
			//bpmUserTaskService.updateApprovalStatusOnTable(tableName,bpmUserTask.getBusinessKey(),type,taskPass?1:3);
		}
    	
		if(assignee.toLowerCase().equals(user.getLoginName().toLowerCase()) ||
				(StringUtils.isNotBlank(owner) && owner.equals(user.getLoginName()))){
			//将用户信息保存到线程变量里，工作流引擎有时会从此变量中取值
			Authentication.setAuthenticatedUserId(user.getLoginName());
			//如果审批不通过，且流程模板后续没有审批不通过的条件分支
			//则直接结束流程并且将流程置为不通过（在流程变量中添加名为processEndType=notPass的变量）

			if ( !taskPass && !bpmCoreService.checkNotPassRoute(taskId, "!"+task.getTaskDefinitionKey(),taskPass) 
					&&DelegationState.PENDING != task.getDelegationState()) {
				bpmCoreService.endProcess(taskId, taskPass, comment);
			} else {
				String processInstanceId = task.getProcessInstanceId();
				taskService.addComment(taskId, processInstanceId, taskPass?BpmConstants.PASS:BpmConstants.NOT_PASS, comment);
				//添加流程变量来标识该任务是审批通过还是不通过，标识为任务的id
				//例如 id为hrTask的任务在完成后会在流程变量添加一个名为：hrTask的变量，值为true 或者false
				variables.put(task.getTaskDefinitionKey(), taskPass) ;
//				//年度计划维护
//		    	if(valiables.get(BpmConstants.BUSINESS_INFO).toString().contains("固定资产")){//维护
//		    		YearPlanHisService  as = SpringHelper.getBeanByClassname(YearPlanHisService.class);
//		    		YearPlanHis yearPlanHis = as.findOne(id);
//		    		variables.put(BpmConstants.PROCESS_MONEY,yearPlanHis.getTotalMoney());
//		    	}
				
				//如果是协作任务（协作任务在activiti里的委托任务，委派给的人审批完了之后，原审批人还要重新审批，这里叫做协作更贴切）
				if(DelegationState.PENDING==task.getDelegationState()){
					//协作任务完成
					taskService.resolveTask(taskId, variables);
				} else {
					//普通任务 完成
					taskService.complete(taskId, variables);
				}
			}
			UserTask userTask =null;
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId_).singleResult();
			if(processInstance != null && taskPass){
				SysUser applyUser = userService.findByLoginName(String.valueOf(valiables.get("applyUser")));
				SysUser thisUser = userService.findByLoginName(task.getAssignee());
				SysMessage applyMessage = new SysMessage();
				applyMessage.setReceiver(applyUser.getId());
				applyMessage.setStatus(0L);
				applyMessage.setCreateTime(new Date());
				applyMessage.setSubject(String.valueOf(valiables.get("businessInfo")));
				applyMessage.setContent("您好，您的流程" + valiables.get("businessInfo") + "已完成" + task.getName() + "环节");
				sysMessageService.saveAndFlush(applyMessage);

				String activityId=processInstance.getActivityId();
				//说明流程未结束，给申请人和下一个节点审批人发送消息
				FlowNode flowNode =getFlowNode(processInstance.getProcessDefinitionId(),activityId);
				if(flowNode != null){
                    Task nextTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
					SysUser nextUser = userService.findByLoginName(nextTask.getAssignee());
					SysMessage message = new SysMessage();
					message.setReceiver(nextUser.getId());
					message.setStatus(0L);
					message.setCreateTime(new Date());
					message.setSubject(String.valueOf(valiables.get("businessInfo")));
					message.setContent("您好，您有一个新的审批流程 -->" + valiables.get("businessInfo"));
					sysMessageService.saveAndFlush(message);
				}
			}
		} else {
			throw new BpmException("你并不是此节点的负责人，无法完成操作！");
		}	
	}

	public FlowNode getFlowNode(String processDefinitionId,String activityId){
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
		return flowNode;
	}


	@Override
	public void completeTask4App(String token,String taskId,Boolean taskPass,String comment, Map<String, Object> variables) throws Exception {
		
		
	}
	

	/**
	 * 查询个人的待办任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:12:30
	 */
	@Override
	public PageDTO<BpmTask> listPersonTodoTasks(SysUser user, PageDTO<BpmTask> page, Integer approvalType,String startDate,String endDate,String processDefinitionName,String applyUserName) throws Exception {
		//TaskQuery taskQuery = taskService.createTaskQuery();
		/*if(StringUtils.isNotEmpty(user.getLoginName())){
			taskQuery.taskAssignee(user.getLoginName());
		}*/
		/*List<Task> tasks = taskQuery.active().orderByTaskCreateTime().desc().listPage((int) ((page.getCurrent()-1)*page.getPages()), (int)page.getPages());*/
		String sql = "SELECT art.* FROM " +
				" act_ru_task AS art " +
				" LEFT JOIN act_ru_variable AS arv ON art.PROC_INST_ID_ = arv.PROC_INST_ID_ "+
				" LEFT JOIN bpm_user_task AS but on art.PROC_INST_ID_ = but.process_id "+
				" LEFT JOIN sys_user as user on but.create_user_id = user.id" +
				" where arv.NAME_ = 'title' ";
		if(approvalType != null){
			sql += "and but.approval_status = " + approvalType;
		}
		if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(simpleDateFormat.parse(endDate));
			calendar.add(Calendar.DATE,1);
			endDate = simpleDateFormat.format(calendar.getTime());
			sql += " and but.create_time >= '" + startDate + "' and but.create_time < '" + endDate + "' ";
		}
		if(StringUtils.isNotEmpty(user.getLoginName())){
			sql += " and art.ASSIGNEE_ = #{loginName} ";
		}
		if(StringUtils.isNotEmpty(processDefinitionName)){
			sql += " and arv.TEXT_ like CONCAT('%',#{processDefinitionName},'%') ";
		}
		if(StringUtils.isNotEmpty(applyUserName)){
			sql += " and user.name like CONCAT('%',#{applyUserName},'%') ";
		}
		sql += " order by CREATE_TIME_ desc ";
		NativeTaskQuery nativeTaskQuery = processEngine.getTaskService().createNativeTaskQuery().sql(sql);
		if(StringUtils.isNotEmpty(user.getLoginName())){
			nativeTaskQuery.parameter("loginName", user.getLoginName());
		}
		if(StringUtils.isNotEmpty(processDefinitionName)){
			nativeTaskQuery.parameter("processDefinitionName", processDefinitionName);
		}
		if(StringUtils.isNotEmpty(applyUserName)){
			nativeTaskQuery.parameter("applyUserName", applyUserName);
		}
		List<Task> tasks = nativeTaskQuery.listPage((int) ((page.getCurrent() - 1) * page.getPages()), (int) page.getPages());
		int total = bpmUserTaskService.getToTasksTotal(user.getLoginName(),processDefinitionName,approvalType,startDate,endDate,applyUserName);
		List<BpmTask> list = createBpmTaskList(tasks,approvalType,startDate,endDate);
		PageDTO<BpmTask> pageDTO = new PageTool<BpmTask>().getPage(list, (int) page.getPages(), 1);
		pageDTO.setTotal(total);
		return pageDTO;
	}

	/**
	 * 根据流程定义查询个人的待办任务
	 * @param user
	 * @param processDefinitionKey 流程定义key
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:12:30
	 */
	@Override
	public List<BpmTask> listPersonTodoTasksAndProcessDefinitionKey(SysUser user,String processDefinitionKey) throws Exception {
		List<Task> tasks = taskService
				 .createTaskQuery()
				 .taskAssignee(user.getLoginName())
				 .active()
				 .processDefinitionKey(processDefinitionKey)
				 .orderByTaskCreateTime()
				 .desc()
				 .list();
		 return createBpmTaskList(tasks);
	}

	/**
	 * 获取用户所有的待办部分
	 * @param user
	 * @return 
	 * @return List<MeetingAgenda>
	 */
	@Override
	public List<BpmTask> listPersonTodoTasksNoPage(SysUser user) throws Exception {
		 List<Task> tasks = taskService.createTaskQuery().taskAssignee(user.getLoginName()).active().list();
		 List<BpmTask> bmplist = createBpmTaskList(tasks);	
		 return bmplist;
	}
	
	
	@Override
	public List<BpmTask> listPersonTodoTasksByProcessInstId(SysUser user,String processInstanceId) throws Exception {
		 List<Task> tasks = taskService.createTaskQuery().taskAssignee(user.getLoginName()).processInstanceId(processInstanceId).active().list();
		 List<BpmTask> list = createBpmTaskList(tasks);
		 return list;
	}
	
	/**
	 * 查询user拥有的角色的所有候选任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:12:47
	 */
	@Override
	public PageDTO<BpmTask> listGroupTodoTasks(SysUser user, PageDTO<BpmTask> page) throws Exception {
		 List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(user.getLoginName()).active()
				 //.includeProcessVariables()
	 			.list();
		 return new PageTool<BpmTask>().getPage(createBpmTaskList(tasks), (int)page.getPages(), page.getCurrent());
	}
	
	/**
	 * 获取user的所有待办，包括已签收任务和候选任务
	 * @param user
	 * @param pageSize
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:14:06
	 */
	@Override
	public PageDTO<BpmTask> listPersonAndGroupTodoTasks(SysUser user, int pageSize, int pageNumber) throws Exception {
		 List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(user.getLoginName()).taskCandidateUser(user.getLoginName()).active()
		         .orderByTaskCreateTime().desc()
				 //.includeProcessVariables()
				 			.list();//listPage((page.getPageNumber()-1)*page.getPageSize(),page.getPageSize());
		 return new PageTool<BpmTask>().getPage(createBpmTaskList(tasks), pageNumber,pageSize);
	}
	
	
	/**
	 * 获取用户的指定任务的所有的待办任务以及候选任务,不分页
	 * @param user
	 * @param taskDefinitionKey 为空则忽略此条件
	 * @return
	 * @throws Exception 
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月19日 下午4:01:19
	 */
	@Override
	public List<BpmTask> listPersonAndGroupTodoTasksWithNoPage(SysUser user, String taskDefinitionKey) throws Exception {
		  TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(user.getLoginName()).taskCandidateUser(user.getLoginName()).active().orderByTaskCreateTime().desc();
		  if(StringUtils.isNotEmpty(taskDefinitionKey)){
			  query.taskDefinitionKey(taskDefinitionKey);
		  }
		  List<Task> tasks = query.list();
		return createBpmTaskList(tasks);
	}
	/**
	 * 获取用户的指定任务的所有的待办任务以及候选任务,不分页
	 * @param user
	 * @param processDefinitionKey 为空则忽略此条件
	 * @return
	 * @throws Exception 
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月19日 下午4:01:19
	 */
	@Override
	public List<BpmTask> listPersonAndGroupProcessDefinitionKeyNoPage(SysUser user, List<String> processDefinitionKey) throws Exception {
		TaskQuery query = null;
		List<Task> tasks =  new ArrayList<>();
		try {
			query = taskService.createTaskQuery().taskCandidateOrAssigned(user.getLoginName()).taskCandidateUser(user.getLoginName()).active().orderByTaskCreateTime().desc();
			if(processDefinitionKey.size()!=0){
			   query.processDefinitionKeyIn(processDefinitionKey);
			}
			tasks = query.list();
		} catch (Exception e) {
			
		}
		return createBpmTaskList(tasks);
	}
	
	@Override
	public PageDTO<BpmTask> listAllTodoTasks(PageDTO<BpmTask> page) throws Exception {
		List<Task> tasks = taskService.createTaskQuery()
				//.includeProcessVariables()
				.orderByTaskCreateTime()
				.desc()
				.list();
		 return new PageTool<BpmTask>().getPage(createBpmTaskList(tasks), (int)page.getPages(), page.getCurrent());
	}
	

	
	@Override
	public PageDTO<BpmHistoricTaskInstance> listHistoryTasks(SysUser user, PageDTO<BpmHistoricTaskInstance> page,Integer approvalType,String startDate,String endDate,String processDefinitionName) throws Exception {
		/*List<HistoricTaskInstance> historyTaskINstances = historyService.createHistoricTaskInstanceQuery()
				 		.taskAssignee(user.getLoginName()).finished()
				 		.orderByTaskCreateTime().desc()
				 		//.includeProcessVariables()
				 		//.list();
						.listPage((int) ((page.getCurrent()-1)*page.getPages()),(int)page.getPages());*/
		String sql = "select task.* from act_hi_taskinst as task left join bpm_user_task as but on task.PROC_INST_ID_ = but.process_id" +
				" LEFT JOIN act_ru_variable AS arv ON task.PROC_INST_ID_ = arv.PROC_INST_ID_  and arv.NAME_ = 'title'  "+
				" where task.ASSIGNEE_ = '" + user.getLoginName()+"' and but.approval_status != 0";
		if(approvalType != null){
			sql += " and but.approval_status = " + approvalType;
		}
		if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(simpleDateFormat.parse(endDate));
			calendar.add(Calendar.DATE,1);
			endDate = simpleDateFormat.format(calendar.getTime());
			sql += " and but.create_time >= '" + startDate + "' and but.create_time < '" + endDate + "' ";
		}
		if(StringUtils.isNotEmpty(processDefinitionName)){
			sql += " and arv.TEXT_ like CONCAT('%','"+ processDefinitionName+"','%') ";
		}
		sql += " order by task.end_time_ desc";
		NativeHistoricTaskInstanceQuery historicTaskInstanceQuery = processEngine.getHistoryService().createNativeHistoricTaskInstanceQuery().sql(sql);
		List<HistoricTaskInstance> historicTaskInstances = historicTaskInstanceQuery.listPage((int) ((page.getCurrent() - 1) * page.getPages()), (int) page.getPages());
		List<BpmHistoricTaskInstance> bpmHistoricTaskList = createBpmHistoricTaskList(historicTaskInstances, approvalType, startDate, endDate);
		int total = bpmUserTaskService.getHistorickTotal(user.getLoginName(),approvalType,startDate,endDate,processDefinitionName);
		PageDTO<BpmHistoricTaskInstance> pageDTO = new PageTool<BpmHistoricTaskInstance>().getPage(bpmHistoricTaskList, (int)page.getPages(), 1);
		pageDTO.setTotal(total);
		return pageDTO;
	}

	@Override
	public PageDTO<BpmTask> listDelegatedTasks(Boolean resolved,SysUser user, PageDTO<BpmTask> page) throws Exception {
		DelegationState state  = DelegationState.RESOLVED;
		if(resolved!=null){
			if (resolved) {
				state = DelegationState.RESOLVED;
			} else {
				state = DelegationState.PENDING;
			}
		}
		
		 List<Task> tasks = taskService.createTaskQuery()
				 .taskOwner(user.getLoginName())
				 .taskDelegationState(state)
 				.list();
		return new PageTool<BpmTask>().getPage(createBpmTaskList(tasks), (int)page.getPages(), page.getCurrent());
	}
	
	@Override
	public Task getProcessInstanceTask(String processInstanceId) throws Exception {
		 List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime().desc().listPage(0, 1);
		return tasks.get(0);
	}

	@Override
	public List<Task> getProcessInstancesTask(String processInstanceId) throws Exception {
		return taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime().desc().list();
	}	
	
	@Override
	public List<BpmHistoricTaskInstance> listHistoricTaskInstance(String processInstanceId) throws Exception {
		return createBpmHistoricTaskList(historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list());
	}

	@Override
	public TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(  
                taskId).singleResult();  
        if (task == null) {  
            throw new BpmException("根据任务id:"+taskId+"未找到任务实例！");
        }  
        return task;  
	}

	@Override
	public List<Task> findTaskListByKey(String processInstanceId, String key) throws Exception {
		  return taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(key).list();  
	}


	private List<BpmTask> createBpmTaskList(List<Task> tasks) throws Exception {
		Set<String> ids = new HashSet<String>();
		for(int i=0;i<tasks.size();i++){
			ids.add(tasks.get(i).getProcessInstanceId());
		}
		List<BpmTask> list = new ArrayList<BpmTask>();
		if(ids.size()==0){
			return list;
		}
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceIds(ids).includeProcessVariables().list();

		out:for(int i=0;i<tasks.size();i++){
			Task task = tasks.get(i);
			BpmTask bpmTask = new BpmTask();
			bpmTask.setAssignee(task.getAssignee());
			bpmTask.setCategory(task.getCategory());
			bpmTask.setCreateTime(task.getCreateTime());
			bpmTask.setDelegationState(task.getDelegationState());
			bpmTask.setDescription(task.getDescription());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setExecutionId(task.getExecutionId());
			bpmTask.setFormKey(task.getFormKey());
			bpmTask.setId(task.getId());
			bpmTask.setName(task.getName());
			bpmTask.setOwner(task.getOwner());
			bpmTask.setParentTaskId(task.getParentTaskId());
			bpmTask.setPriority(task.getPriority());
			bpmTask.setProcessDefinitionId(task.getProcessDefinitionId());
			bpmTask.setProcessInstanceId(task.getProcessInstanceId());
			bpmTask.setProcessVariables(task.getProcessVariables());
			bpmTask.setSuspended(task.isSuspended());
			bpmTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
			bpmTask.setTaskLocalVariables(task.getTaskLocalVariables());
			bpmTask.setTenantId(task.getTenantId());

			//根据用户的登录名设置用户姓名，因为工作流中配置的是用户的loginName,要转换为用户真实名称。
			//由于设置了用户缓存，虽多次访问，并不会造成性能的影响
			if(StringUtils.isNotEmpty(task.getAssignee())){
				SysUser user = userService.findByLoginName(task.getAssignee());
				bpmTask.setAssigneeName(user!=null?user.getName():task.getAssignee());
			}

			//设置业务主键
			try {


				for(int j=0;j<processInstances.size();j++){
					if(processInstances.get(j).getProcessInstanceId().equals(task.getProcessInstanceId())){
						bpmTask.setProcessDefinitionName(processInstances.get(j).getProcessDefinitionName());
						bpmTask.setProcessVariables(processInstances.get(j).getProcessVariables());
						bpmTask.setBusinessKey(processInstances.get(j).getBusinessKey());

						//设置申请人信息
						bpmTask.setApplyUser((String)processInstances.get(j).getProcessVariables().get(BpmConstants.APPLY_USER));
						SysUser user = userService.findByLoginName(bpmTask.getApplyUser());
						//2019-02-11 因逻辑删除用户，审批流程申请人为无，应该为申请人名称
						if(user==null){
							bpmTask.setApplyUserName("<font color='red'>"+bpmTask.getApplyUser()+"-账号异常</font>");
						}else{
							bpmTask.setApplyUserName(user!=null?user.getName():"");
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			list.add(bpmTask);
		}
		return list;
	}
	
	/**
	 * 将Task内容存入BpmTask中，并添加一些Task中没有的内容
	 * @param tasks
	 * @return 
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:43:11
	 */
	private List<BpmTask> createBpmTaskList(List<Task> tasks,Integer approvalType,String startDate,String endDate) throws Exception {
		Set<String> ids = new HashSet<String>();
		for(int i=0;i<tasks.size();i++){
			ids.add(tasks.get(i).getProcessInstanceId());
		}
		List<BpmTask> list = new ArrayList<BpmTask>();
		if(ids.size()==0){
			return list;
		}
		/*List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceIds(ids).includeProcessVariables().list();*/
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(StringUtils.isNotEmpty(endDate)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(endDate));
			calendar.add(Calendar.DATE,1);
			endDate = sdf.format(calendar.getTime());
		}
		out:for(int i=0;i<tasks.size();i++){
			Task task = tasks.get(i);
			BpmUserTask bpmUserTask = bpmUserTaskService.selectByTaskId(task.getProcessInstanceId());
			HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(bpmUserTask.getProcessId()).includeProcessVariables().singleResult();
			if(approvalType != null){
				if(!approvalType.equals(bpmUserTask.getApprovalStatus())){
					continue ;
				}
			}
			if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)){
				if(bpmUserTask.getCreateTime().compareTo(startDate) < 0 || bpmUserTask.getCreateTime().compareTo(endDate) > 0){
					continue ;
				}
			}
			Date startTime = simpleDateFormat.parse(bpmUserTask.getCreateTime());
			BpmTask bpmTask = new BpmTask();
			bpmTask.setAssignee(task.getAssignee());
			bpmTask.setCategory(task.getCategory());
			bpmTask.setCreateTime(startTime);
			bpmTask.setDelegationState(task.getDelegationState());
			bpmTask.setDescription(task.getDescription());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setExecutionId(task.getExecutionId());
			bpmTask.setFormKey(task.getFormKey());
			bpmTask.setId(task.getId());
			bpmTask.setName(task.getName());
			bpmTask.setOwner(task.getOwner());
			bpmTask.setParentTaskId(task.getParentTaskId());
			bpmTask.setPriority(task.getPriority());
			bpmTask.setProcessDefinitionId(task.getProcessDefinitionId());
			bpmTask.setProcessInstanceId(task.getProcessInstanceId());
			bpmTask.setProcessVariables(task.getProcessVariables());
			bpmTask.setSuspended(task.isSuspended());
			bpmTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
			bpmTask.setTaskLocalVariables(task.getTaskLocalVariables());
			bpmTask.setTenantId(task.getTenantId());
			bpmTask.setApprovalStatus(bpmUserTask.getApprovalStatus());

			//根据用户的登录名设置用户姓名，因为工作流中配置的是用户的loginName,要转换为用户真实名称。
			//由于设置了用户缓存，虽多次访问，并不会造成性能的影响
			if(StringUtils.isNotEmpty(task.getAssignee())){
				SysUser user = userService.findByLoginName(task.getAssignee());
				bpmTask.setAssigneeName(user!=null?user.getName():task.getAssignee());
			}
			bpmTask.setProcessDefinitionName(String.valueOf(historicProcessInstance.getProcessVariables().get("title")));
			//bpmTask.setProcessVariables(processInstances.get(j).getProcessVariables());
			bpmTask.setBusinessKey(bpmUserTask.getBusinessKey());
			SysUser user = userService.selectById(bpmUserTask.getCreateUserId());
			//2019-02-11 因逻辑删除用户，审批流程申请人为无，应该为申请人名称
			if(user==null){
				bpmTask.setApplyUserName("<font color='red'>"+bpmTask.getApplyUser()+"-账号异常</font>");
			}else{
				bpmTask.setApplyUserName(user!=null?user.getName():"");
			}
			//设置业务主键
			/*try {


				for(int j=0;j<processInstances.size();j++){
					if(processInstances.get(j).getProcessInstanceId().equals(task.getProcessInstanceId())){
						bpmTask.setProcessDefinitionName(processInstances.get(j).getProcessDefinitionName());
						bpmTask.setProcessVariables(processInstances.get(j).getProcessVariables());
						bpmTask.setBusinessKey(processInstances.get(j).getBusinessKey());

						//设置申请人信息
						bpmTask.setApplyUser((String)processInstances.get(j).getProcessVariables().get(BpmConstants.APPLY_USER));
						SysUser user = userService.findByLoginName(bpmTask.getApplyUser());
						//2019-02-11 因逻辑删除用户，审批流程申请人为无，应该为申请人名称
						if(user==null){
							bpmTask.setApplyUserName("<font color='red'>"+bpmTask.getApplyUser()+"-账号异常</font>");
						}else{
							bpmTask.setApplyUserName(user!=null?user.getName():"");
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			list.add(bpmTask);
		}
		return list;
	}


	/**
	 * 将HistoricTaskInstance内容存放到BpmHistoricTaskInstance中并添加一些HistoricTaskInstance没有的内容
	 * @param tasks
	 * @return
	 * @return List<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2015年12月16日 下午8:43:58
	 */
	private List<BpmHistoricTaskInstance> createBpmHistoricTaskList(List<HistoricTaskInstance> tasks) throws ParseException {
		List<BpmHistoricTaskInstance> list = new ArrayList<BpmHistoricTaskInstance>();
		if(tasks==null || tasks.size()==0){
			return list;
		}

		Set<String> ids = new HashSet<String>();
		for(int i=0;i<tasks.size();i++){
			String procInstId = tasks.get(i).getProcessInstanceId();
			if(!ids.contains(procInstId)){
				ids.add(procInstId);
			}
		}
		List<HistoricProcessInstance> historicProcessInstances = new ArrayList<HistoricProcessInstance>();

		//获取所有的Comment
		List<Comment> listComment =  new ArrayList<Comment>();

		historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
				.processInstanceIds(ids).includeProcessVariables().list();

		Set<String> processDefineIds = new HashSet<String>();
		for(HistoricProcessInstance hisInstance : historicProcessInstances){
			//如果流程结束再查询comment只能通过这种方式，因为taskService.getComment(commentId) 只会查询到类型是comment，自定义类型的查不到
			List<Comment> commentsPass = taskService.getProcessInstanceComments(hisInstance.getId(),"pass");
			List<Comment> commentsNoPass = taskService.getProcessInstanceComments(hisInstance.getId(),"notPass");
			listComment.addAll(commentsNoPass);
			listComment.addAll(commentsPass);
			//流程定义id
			processDefineIds.add(hisInstance.getProcessDefinitionId());
		}

		//流程定义信息
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefineIds).list();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		BpmUserTask bpmUserTask = bpmUserTaskService.selectByTaskId(tasks.get(0).getProcessInstanceId());
		for(int i=0;i<tasks.size();i++){
			HistoricTaskInstance task = tasks.get(i);
			BpmHistoricTaskInstance bpmTask = new BpmHistoricTaskInstance();
			bpmTask.setAssignee(task.getAssignee());
			bpmTask.setCategory(task.getCategory());
			bpmTask.setCreateTime(simpleDateFormat.parse(bpmUserTask.getCreateTime()));
			bpmTask.setDescription(task.getDescription());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setExecutionId(task.getExecutionId());
			bpmTask.setFormKey(task.getFormKey());
			bpmTask.setId(task.getId());
			bpmTask.setName(task.getName());
			bpmTask.setOwner(task.getOwner());
			bpmTask.setParentTaskId(task.getParentTaskId());
			bpmTask.setPriority(task.getPriority());
			bpmTask.setProcessDefinitionId(task.getProcessDefinitionId());
			bpmTask.setProcessInstanceId(task.getProcessInstanceId());
			bpmTask.setProcessVariables(task.getProcessVariables());
			bpmTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
			bpmTask.setTaskLocalVariables(task.getTaskLocalVariables());
			bpmTask.setTenantId(task.getTenantId());
			bpmTask.setTime(task.getTime());
			bpmTask.setDeleteReason(task.getDeleteReason());
			bpmTask.setStartTime(task.getStartTime());
			bpmTask.setEndTime(task.getEndTime());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setDurationInMillis(task.getDurationInMillis());
			bpmTask.setWorkTimeInMillis(task.getWorkTimeInMillis());
			bpmTask.setClaimTime(task.getClaimTime());

			//根据用户的登录名设置用户姓名，因为工作流中配置的是用户的loginName,要转换为用户真实名称。
			//由于设置了用户缓存，虽多次访问，并不会造成性能的影响
			if(StringUtils.isNotEmpty(task.getAssignee())){
				SysUser user = null;
				try {
					user = userService.findByLoginName(task.getAssignee());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String assigneeName = user!=null?user.getName():task.getAssignee();
				//对于委托完成的，做特殊处理显示原审批人
				if(StringUtils.isNotBlank(task.getOwner())
						&& !task.getOwner().equals(task.getAssignee())){
					SysUser owner = null;
					try {
						owner = userService.findByLoginName(task.getOwner());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					assigneeName = assigneeName+"(原:"+(owner!=null?owner.getName():task.getOwner())+")";
				}
				bpmTask.setAssigneeName(assigneeName);
			}

			//设置业务主键及批注信息
			for(int j=0;j<historicProcessInstances.size();j++){
				HistoricProcessInstance hProcessInstance = historicProcessInstances.get(j);
				if(hProcessInstance.getId().equals(task.getProcessInstanceId())){
					bpmTask.setBusinessKey(hProcessInstance.getBusinessKey());
					bpmTask.setProcessVariables(hProcessInstance.getProcessVariables());
					List<Comment> comments = new ArrayList<Comment>();
					String commentMessage = "";
					for(Comment comment :listComment){
						CommentEntity commentEntity = (CommentEntity)comment;
						if(comment.getTaskId().equals(bpmTask.getId())){
							if(task.getAssignee().equals(commentEntity.getUserId())){
								commentMessage = (commentEntity.getType().equals("pass")?"同意:":"不同意:")
										+commentEntity.getMessage()+"<br/>"+commentMessage;
							}else{
								SysUser user = null;
								try {
									user = userService.findByLoginName(commentEntity.getUserId());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								commentMessage+="协作:"+commentEntity.getMessage();
								bpmTask.setAssigneeName(bpmTask.getAssigneeName()+"(协作:"+(user !=null ?user.getName():"未知")+")");
							}
							comments.add(comment);
						}
					}
					bpmTask.setCommentMessage(commentMessage);
					bpmTask.setComments(comments);

					//设置流程定义名称
					for(ProcessDefinition processDefinition : processDefinitions){
						if(hProcessInstance.getProcessDefinitionId().equals(processDefinition.getId())){
							bpmTask.setProcessDefinitionName(processDefinition.getName());
							break;
						}
					}

					//设置申请人信息
					bpmTask.setApplyUser((String)bpmTask.getProcessVariables().get(BpmConstants.APPLY_USER));
					SysUser user = null;
					try {
						user = userService.findByLoginName(bpmTask.getApplyUser());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bpmTask.setApplyUserName(user!=null?user.getName():"");

					if(StringUtils.isEmpty(bpmTask.getAssigneeName())){
						//对于动态指定了候选用户的情况
						String candidateUsers = (String) hProcessInstance.getProcessVariables().get(task.getTaskDefinitionKey()+"CandidateUsers");
						if(StringUtils.isNotEmpty(candidateUsers)){
							String[] loginNames = candidateUsers.split(",");
							String[] names = new String[loginNames.length];
							for(int index=0 ; index<loginNames.length ; index++){
								//用户查询是有缓存的，这里会走切面缓存
								SysUser theUser = null;
								try {
									theUser = userService.findByLoginName(loginNames[index]);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								names[index] = theUser.getName();
							}
							bpmTask.setAssigneeName(StringUtils.join(names, ","));
						}
					}

					break;
				}
			}
			list.add(bpmTask);
		}
		return list;
	}
	/**
	 * 将HistoricTaskInstance内容存放到BpmHistoricTaskInstance中并添加一些HistoricTaskInstance没有的内容
	 * @param tasks
	 * @return 
	 * @return List<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月16日 下午8:43:58
	 */
	private List<BpmHistoricTaskInstance> createBpmHistoricTaskList(List<HistoricTaskInstance> tasks,Integer approvalType,String startDate,String endDate) throws ParseException {
		List<BpmHistoricTaskInstance> list = new ArrayList<BpmHistoricTaskInstance>();
		if(tasks==null || tasks.size()==0){
			return list;
		}
		Set<String> ids = new HashSet<String>();
		for(int i=0;i<tasks.size();i++){
			String procInstId = tasks.get(i).getProcessInstanceId();
			if(!ids.contains(procInstId)){
				ids.add(procInstId);
			}
		}
		List<HistoricProcessInstance> historicProcessInstances = new ArrayList<HistoricProcessInstance>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取所有的Comment
		List<Comment> listComment =  new ArrayList<Comment>();
		
		historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
				.processInstanceIds(ids).includeProcessVariables().list();
		
		Set<String> processDefineIds = new HashSet<String>();
		for(HistoricProcessInstance hisInstance : historicProcessInstances){
			//如果流程结束再查询comment只能通过这种方式，因为taskService.getComment(commentId) 只会查询到类型是comment，自定义类型的查不到
			List<Comment> commentsPass = taskService.getProcessInstanceComments(hisInstance.getId(),"pass");
			List<Comment> commentsNoPass = taskService.getProcessInstanceComments(hisInstance.getId(),"notPass");
			listComment.addAll(commentsNoPass);
			listComment.addAll(commentsPass);
			//流程定义id
			processDefineIds.add(hisInstance.getProcessDefinitionId());
		}
		
		//流程定义信息
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionIds(processDefineIds).list();
		if(StringUtils.isNotEmpty(endDate)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(endDate));
			calendar.add(Calendar.DATE,1);
			endDate = sdf.format(calendar.getTime());
		}
		for(int i=0;i<tasks.size();i++){
			HistoricTaskInstance task = tasks.get(i);

			BpmUserTask bpmUserTask = bpmUserTaskService.selectByTaskId(task.getProcessInstanceId());
			/*if(approvalType != null){
				if(!approvalType.equals(bpmUserTask.getApprovalStatus())){
					continue ;
				}
			}
			if(StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)){
				if(bpmUserTask.getCreateTime().compareTo(startDate) < 0 || bpmUserTask.getCreateTime().compareTo(endDate) > 0){
					continue ;
				}
			}*/
			Date startTime = simpleDateFormat.parse(bpmUserTask.getCreateTime());
			BpmHistoricTaskInstance bpmTask = new BpmHistoricTaskInstance();
			bpmTask.setAssignee(task.getAssignee());
			bpmTask.setCategory(task.getCategory());
			bpmTask.setCreateTime(startTime);
			bpmTask.setDescription(task.getDescription());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setExecutionId(task.getExecutionId());
			bpmTask.setFormKey(task.getFormKey());
			bpmTask.setId(task.getId());
			bpmTask.setName(task.getName());
			bpmTask.setOwner(task.getOwner());
			bpmTask.setParentTaskId(task.getParentTaskId());
			bpmTask.setPriority(task.getPriority());
			bpmTask.setProcessDefinitionId(task.getProcessDefinitionId());
			bpmTask.setProcessInstanceId(task.getProcessInstanceId());
			bpmTask.setProcessVariables(task.getProcessVariables());
			bpmTask.setTaskDefinitionKey(task.getTaskDefinitionKey());
			bpmTask.setTaskLocalVariables(task.getTaskLocalVariables());
			bpmTask.setTenantId(task.getTenantId());
			bpmTask.setTime(task.getTime());
			bpmTask.setDeleteReason(task.getDeleteReason());
			bpmTask.setStartTime(startTime);
			bpmTask.setEndTime(task.getEndTime());
			bpmTask.setDueDate(task.getDueDate());
			bpmTask.setDurationInMillis(task.getDurationInMillis());
			bpmTask.setWorkTimeInMillis(task.getWorkTimeInMillis());
			bpmTask.setClaimTime(task.getClaimTime());
			
			//根据用户的登录名设置用户姓名，因为工作流中配置的是用户的loginName,要转换为用户真实名称。
			//由于设置了用户缓存，虽多次访问，并不会造成性能的影响
			if(StringUtils.isNotEmpty(task.getAssignee())){
				SysUser user = null;
				try {
					user = userService.findByLoginName(task.getAssignee());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String assigneeName = user!=null?user.getName():task.getAssignee();
				//对于委托完成的，做特殊处理显示原审批人
				if(StringUtils.isNotBlank(task.getOwner())
						&& !task.getOwner().equals(task.getAssignee())){
					SysUser owner = null;
					try {
						owner = userService.findByLoginName(task.getOwner());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					assigneeName = assigneeName+"(原:"+(owner!=null?owner.getName():task.getOwner())+")";
				}
				bpmTask.setAssigneeName(assigneeName);
			}
			
			//设置业务主键及批注信息
			for(int j=0;j<historicProcessInstances.size();j++){
				HistoricProcessInstance hProcessInstance = historicProcessInstances.get(j);
				if(hProcessInstance.getId().equals(task.getProcessInstanceId())){
					bpmTask.setBusinessKey(hProcessInstance.getBusinessKey());
					bpmTask.setProcessVariables(hProcessInstance.getProcessVariables());
					List<Comment> comments = new ArrayList<Comment>();
					String commentMessage = "";
					for(Comment comment :listComment){
						CommentEntity commentEntity = (CommentEntity)comment;
						if(comment.getTaskId().equals(bpmTask.getId())){
							if(task.getAssignee().equals(commentEntity.getUserId())){
								commentMessage = (commentEntity.getType().equals("pass")?"同意:":"不同意:")
										+commentEntity.getMessage()+"<br/>"+commentMessage;
							}else{
								SysUser user = null;
								try {
									user = userService.findByLoginName(commentEntity.getUserId());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								commentMessage+="协作:"+commentEntity.getMessage();
								bpmTask.setAssigneeName(bpmTask.getAssigneeName()+"(协作:"+(user !=null ?user.getName():"未知")+")");
							}
							comments.add(comment);
						}
					}
					bpmTask.setCommentMessage(commentMessage);
					bpmTask.setComments(comments);
					
					//设置流程定义名称
					/*for(ProcessDefinition processDefinition : processDefinitions){
						if(hProcessInstance.getProcessDefinitionId().equals(processDefinition.getId())){
							bpmTask.setProcessDefinitionName(processDefinition.getName());
							break;
						}
					}*/
					bpmTask.setProcessDefinitionName(bpmUserTask.getProcessName());

					//设置申请人信息
					bpmTask.setApplyUser((String)bpmTask.getProcessVariables().get(BpmConstants.APPLY_USER));
					SysUser user = null;
					try {
						user = userService.findByLoginName(bpmTask.getApplyUser());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bpmTask.setApplyUserName(user!=null?user.getName():"");
					
					if(StringUtils.isEmpty(bpmTask.getAssigneeName())){
					    //对于动态指定了候选用户的情况
					    String candidateUsers = (String) hProcessInstance.getProcessVariables().get(task.getTaskDefinitionKey()+"CandidateUsers");
					    if(StringUtils.isNotEmpty(candidateUsers)){
					        String[] loginNames = candidateUsers.split(",");
	                        String[] names = new String[loginNames.length];
	                        for(int index=0 ; index<loginNames.length ; index++){
	                            //用户查询是有缓存的，这里会走切面缓存
	                            SysUser theUser = null;
								try {
									theUser = userService.findByLoginName(loginNames[index]);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                            names[index] = theUser.getName();
	                        }
	                        bpmTask.setAssigneeName(StringUtils.join(names, ","));
					    }
					}
					
					break;
				}
			}
			list.add(bpmTask);
		}
		return list;
	}

	@Override
	public HistoricTaskInstance findHistoricTaskById(String taskId) throws Exception {
		return historyService.createHistoricTaskInstanceQuery().taskId(taskId).includeProcessVariables().singleResult();
	}

	@Override
	public Boolean revokeProcess(String businessKey){
		return null;
//		Task task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).singleResult();
//		if(task==null) {
//			System.out.println("流程未启动或已执行完成，无法撤回");
//		}
//		
//		User loginUser = SessionHelper.getUser();
//		List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
//				.processInstanceBusinessKey(businessKey)
//				.orderByTaskCreateTime()
//				.asc()
//				.list();
//		String myTaskId = null;
//		HistoricTaskInstance myTask = null;
//		for(HistoricTaskInstance hti : htiList) {
////			if(loginUser.getLoginName().equals(hti.getAssignee())) {
////				myTaskId = hti.getId();
////				myTask = hti;
////				break;
////			}
//			myTaskId = hti.getId();
//			myTask = hti;
//		}
//		
//		
//		if(null==myTaskId) {
//			System.out.println("该任务非当前用户提交，无法撤回");
//		}
//		
//		String processDefinitionId = myTask.getProcessDefinitionId();
//		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
//		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//		
//		//变量
////		Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
//		String myActivityId = null;
//		List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
//				.executionId(myTask.getExecutionId()).finished().list();
//		for(HistoricActivityInstance hai : haiList) {
//			//if(myTaskId.equals(hai.getTaskId())) {
//				myActivityId = hai.getActivityId();
//				//break;
//			//}
//		}
//		FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);
//		
//		
//		Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
//		String activityId = execution.getActivityId();
//		FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
//		
//		//记录原活动方向
//		List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
//		oriSequenceFlows.addAll(flowNode.getOutgoingFlows());
//		
//		//清理活动方向
//		flowNode.getOutgoingFlows().clear();
//		//建立新方向
//		List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
//		SequenceFlow newSequenceFlow = new SequenceFlow();
//		newSequenceFlow.setId("newSequenceFlowId");
//		newSequenceFlow.setSourceRef(flowNode.toString());
//		newSequenceFlow.setTargetRef(myFlowNode.toString());
//		newSequenceFlowList.add(newSequenceFlow);
//		flowNode.setOutgoingFlows(newSequenceFlowList);
//		
//		Authentication.setAuthenticatedUserId(loginUser.getLoginName());
//		taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");
//		
//		//Map<String,Object> currentVariables = new HashMap<String,Object>();
//		//currentVariables.put("applier", loginUser.getUsername());
//		//完成任务
//		//taskService.complete(task.getId(),currentVariables);
//		//恢复原方向
//		return false;
	}
}
