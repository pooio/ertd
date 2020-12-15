package com.metaShare.modules.bpm.service;

import java.io.InputStream;
import java.util.*;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.bpm.cmd.HistoryProcessInstanceDiagramCmd;
import com.metaShare.modules.bpm.dto.BpmProcessInstance;
import com.metaShare.modules.bpm.exception.BpmException;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;



@Service(value="bpmProcessInstanceService")
public class BpmProcessInstanceServiceImpl implements BpmProcessInstanceService {
	@Autowired
	protected RuntimeService runtimeService; 
	@Autowired 
	protected HistoryService historyService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected ManagementService managementService;
	@Autowired
	protected RepositoryService repositoryService;
	@Autowired
	protected ProcessEngine processEngine;
	@Autowired
    ProcessEngineConfiguration processEngineConfiguration;
	@Autowired
	protected BpmTaskService bpmTaskService;
	@Autowired
	protected BpmStatusService bpmStatusService;
	@Autowired
	protected SysUserService userService;
	@Autowired
	private BpmUserTaskService bpmUserTaskService;
	@Autowired
	BpmProcessInstanceService bpmProcessInstanceService;

	@Override
	public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId) {
		 HistoricProcessInstance historicProcessInstance = historyService
	                .createHistoricProcessInstanceQuery()
	                .processInstanceId(processInstanceId).singleResult();
		return historicProcessInstance;
	}
	
	@Override
	public ProcessInstance getProcessInstance(String processInstanceId) {
		 ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				 .processInstanceId(processInstanceId).singleResult();
		return processInstance;
	}
	
	@Override
	public PageDTO<BpmProcessInstance> listProcessInstances(PageDTO<BpmProcessInstance> page,
			String businessKey,String processDefinitionName,String loginName) {
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
		if(StringUtils.isNotEmpty(businessKey)){
			query.processInstanceBusinessKey(businessKey);
		}
		if(StringUtils.isNotEmpty(processDefinitionName)){
			query.processDefinitionName(processDefinitionName);
			//query.processInstanceNameLikeIgnoreCase(processDefinitionName);
		}
		if(StringUtils.isNotEmpty(loginName)){
			query.variableValueLike(BpmConstants.APPLY_USER, loginName);
		}
		List<ProcessInstance> processInstances = query.includeProcessVariables().orderByProcessInstanceId().desc()
	                .list();//listPage((page.getPageNumber()-1)*page.getPageSize(),page.getPageSize());
        return new PageTool<BpmProcessInstance>().getPage(createBpmProcessInstance(processInstances), (int)page.getPages(), page.getCurrent());
	}

	@Override
	public void deleteProcessInstance(String processInstanceId, String deleteReason) {
		 runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
		 bpmStatusService.deleteByProcessInstanceId(processInstanceId);
	}

	@Override
	public void suspendProcessInstance(String processInstanceId) {
		 runtimeService.suspendProcessInstanceById(processInstanceId);
	}

	@Override
	public void activeProcessInstance(String processInstanceId) {
		 runtimeService.activateProcessInstanceById(processInstanceId);
	}

	@Override
	public void deleteProcessInstanceHistory(String processInstanceId) {
       	historyService.deleteHistoricProcessInstance(processInstanceId);
	}

	@Override
	public PageDTO<BpmProcessInstance> findRunningProcessInstaces(PageDTO<BpmProcessInstance> page) {
		  ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
				  .active().orderByProcessInstanceId().desc();
	       List<ProcessInstance> list = query.list();
	       return new PageTool<BpmProcessInstance>().getPage(createBpmProcessInstance(list), (int)page.getPages(), page.getCurrent());

	}

	@Override
	public PageDTO<HistoricProcessInstance> findFinishedProcessInstaces(PageDTO<HistoricProcessInstance> page) {
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().finished().orderByProcessInstanceId().desc();
		List<HistoricProcessInstance> list = query.list();
	    return new PageTool<HistoricProcessInstance>().getPage(list, (int)page.getPages(), page.getCurrent());

	}

	@Override
	public PageDTO<BpmProcessInstance> findRunningProcessInstaces(PageDTO<BpmProcessInstance> page, String processDefinitionKey) {
		  ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey)
				  .active().orderByProcessInstanceId().desc();
	       List<ProcessInstance> list = query.list();
		   return new PageTool<BpmProcessInstance>().getPage(createBpmProcessInstance(list), (int)page.getPages(), page.getCurrent());
	}

	@Override
	public PageDTO<HistoricProcessInstance> findFinishedProcessInstaces(PageDTO<HistoricProcessInstance> page,
			String processDefinitionKey) {
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey)
				.finished().orderByProcessInstanceId().desc();
		List<HistoricProcessInstance> list = query.list();
	    return new PageTool<HistoricProcessInstance>().getPage(list, (int)page.getPages(), page.getCurrent());
	}

	/**
	 * 将ProcessInstance封装为 BpmProcessInstance
	 * @param processInstances
	 * @return 
	 * @return List<BpmProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月19日 上午10:37:28
	 */
	private List<BpmProcessInstance> createBpmProcessInstance(List<ProcessInstance> processInstances){
		List<BpmProcessInstance> list = new ArrayList<BpmProcessInstance>();
		List<String> ids = Lists.newArrayList();
		
		for(ProcessInstance processInstance : processInstances){
			ids.add(processInstance.getId());
		}
		List<Task> tasks =  Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(ids)){
			tasks = taskService.createTaskQuery().processInstanceIdIn(ids).active().list();
		}
		
		for(ProcessInstance processInstance : processInstances){
			
			BpmProcessInstance bpmProcessInstance = new BpmProcessInstance();
			for(Task task : tasks){
				if(processInstance.getId().equals(task.getProcessInstanceId()) 
						&& task.getTaskDefinitionKey().equals(processInstance.getActivityId())){
					bpmProcessInstance.setFormKey(task.getFormKey());
					bpmProcessInstance.setTaskId(task.getId());
					break;
				}
			}
			bpmProcessInstance.setActivityId(processInstance.getActivityId());
			bpmProcessInstance.setBusinessKey(processInstance.getBusinessKey());
			bpmProcessInstance.setEnded(processInstance.isEnded());
			bpmProcessInstance.setId(processInstance.getId());
			bpmProcessInstance.setName(processInstance.getName());
			bpmProcessInstance.setParentId(processInstance.getParentId());
			bpmProcessInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
			bpmProcessInstance.setProcessInstanceId(processInstance.getProcessInstanceId());
			bpmProcessInstance.setProcessVariables(processInstance.getProcessVariables());
			bpmProcessInstance.setSuspended(processInstance.isSuspended());
			bpmProcessInstance.setTenantId(processInstance.getTenantId());
			bpmProcessInstance.setDeploymentId(processInstance.getDeploymentId());
			bpmProcessInstance.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
			bpmProcessInstance.setProcessDefinitionName(processInstance.getProcessDefinitionName());
			bpmProcessInstance.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
			bpmProcessInstance.setLocalizedDescription(processInstance.getLocalizedName());
			bpmProcessInstance.setLocalizedName(processInstance.getLocalizedName());
			String applyUser = (String) bpmProcessInstance.getProcessVariables().get(BpmConstants.APPLY_USER);
			System.out.println("======++++++++++++---------------申请人：" +  applyUser);
			if(StringUtils.isNotEmpty(applyUser)){
				SysUser user = null;
				try {
					user = userService.findByLoginName(applyUser);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bpmProcessInstance.setApplyUser(applyUser);
				if(user==null){
                    bpmProcessInstance.setApplyUserName("未知");
                } else {
                    bpmProcessInstance.setApplyUserName(user.getName());
                }
			}
			list.add(bpmProcessInstance);
		}
		return list;
		
	}

	@Override
	public InputStream getHistoryProcessInstanceImg(String processInstanceId) {
		Command<InputStream> cmd = new HistoryProcessInstanceDiagramCmd( processInstanceId);
        InputStream is = managementService.executeCommand(cmd);
        //activiti提供的原生方法
        /*ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        String executionId = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult().getId();
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        String activityFontName = processEngine.getProcessEngineConfiguration().getActivityFontName();
        String labelFontName = processEngine.getProcessEngineConfiguration().getLabelFontName();
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        InputStream is = diagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds, Collections.<String>emptyList(),
        													"宋体", "宋体", null, 1.0);*/

		return is;
	}

	@Override
	public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception {
		 // 找到流程实例  
        ProcessInstance processInstance = runtimeService  
                .createProcessInstanceQuery().processInstanceId(bpmTaskService.findTaskById(taskId).getProcessInstanceId())  
                .singleResult();  
        if (processInstance == null) {  
        	throw new BpmException("根据任务id:"+taskId+"未找到流程实例！");
        }  
        return processInstance;  
	}

	@Override
	public  PageDTO<BpmProcessInstance> listAllProcessInstances(int pageNum, int pageSize, String processDefinitionName, String loginName,String processName,Integer type) {
		/*HistoricTaskInstanceQuery historicTaskInstanceQuery = processEngine.getHistoryService().createHistoricTaskInstanceQuery();
		if(StringUtils.isNotEmpty(loginName)){
			historicTaskInstanceQuery.taskAssigneeLikeIgnoreCase(loginName);
		}
		if(StringUtils.isNotEmpty(processName)){
			historicTaskInstanceQuery.processDefinitionNameLike(processName);

		}*/
		//List<HistoricTaskInstance> list = historicTaskInstanceQuery.listPage((pageNum-1) * pageSize,  pageSize);

        /*if(StringUtils.isNotEmpty(loginName)){
            historicProcessInstanceQuery.startedBy(loginName);
        }
        if(StringUtils.isNotEmpty(processName)){
            historicProcessInstanceQuery.processInstanceNameLike(processName);
        }*/
		/*HistoricProcessInstanceQuery historicProcessInstanceQuery = processEngine.getHistoryService().createHistoricProcessInstanceQuery().includeProcessVariables();
		if(StringUtils.isNotEmpty(processName)){
			historicProcessInstanceQuery.variableValueLike("title", processName);
		}
		List<HistoricProcessInstance> list = historicProcessInstanceQuery.list();*/
		List<BpmProcessInstance> bpmList = new ArrayList<BpmProcessInstance  >();
		NativeHistoricProcessInstanceQuery nativeHistoricProcessInstanceQuery = processEngine.getHistoryService().createNativeHistoricProcessInstanceQuery();
		String sql = "select ahp.*,ahv.TEXT_ as name,ah.TEXT_ as sponName from act_hi_procinst as ahp left join act_hi_varinst as ahv on ahp.ID_ = ahv.PROC_INST_ID_  LEFT JOIN act_hi_varinst AS ah ON ahp.ID_ = ah.PROC_INST_ID_ where ahv.NAME_= #{title} and ah.NAME_ = #{sponsorName} ";
		if(type == null || type == 0){
			sql += " and ahp.END_TIME_ is null ";
		}else{
			sql += " and ahp.END_TIME_ is not null ";
		}
		if(StringUtils.isNotEmpty(processName)){
			sql += " and ahv.TEXT_ like CONCAT('%',#{processName},'%') ";
		}
		if(StringUtils.isNotEmpty(loginName)){
			sql += " and ah.TEXT_  like CONCAT('%',#{loginName},'%') ";
		}
		nativeHistoricProcessInstanceQuery.sql(sql);
		nativeHistoricProcessInstanceQuery.parameter("title", "title");
		nativeHistoricProcessInstanceQuery.parameter("sponsorName", "sponsorName");
		if(StringUtils.isNotEmpty(processName)){
			nativeHistoricProcessInstanceQuery.parameter("processName", processName);
		}
		if(StringUtils.isNotEmpty(loginName)){
			nativeHistoricProcessInstanceQuery.parameter("loginName", loginName);
		}

		boolean isEnded = true;
		int total = bpmUserTaskService.getHistoryProcessInstanceTotal(processName,loginName,type);
		List<HistoricProcessInstance> list = nativeHistoricProcessInstanceQuery.listPage((pageNum-1)*pageSize,pageSize);
		for(HistoricProcessInstance historicTaskInstance : list){
			Map<String,String> taskInfo =  bpmUserTaskService.getTaskInfoOnProcessId(historicTaskInstance.getId());
			/*if(StringUtils.isNotEmpty(loginName) && !taskInfo.get("loginName").contains(loginName)){
				break;
			}
			if(StringUtils.isNotEmpty(processName) && !taskInfo.get("processName").contains(processName)){
				break;
			}*/
			ProcessInstance processInstance = bpmProcessInstanceService.getProcessInstance(historicTaskInstance.getId());

			BpmProcessInstance bpmProcessInstance = new BpmProcessInstance();

			bpmProcessInstance.setActivityId(historicTaskInstance.getStartActivityId());
			bpmProcessInstance.setBusinessKey(taskInfo.get("businessKey"));
			if(type == null || type == 0){
				isEnded = false;
				Task task = taskService.createTaskQuery().processInstanceId(taskInfo.get("processId")).singleResult();
				bpmProcessInstance.setName(task.getName());
				SysUser sysUser = userService.getByAccount(task.getAssignee());
				if(sysUser != null){
					bpmProcessInstance.setLocalizedName(sysUser.getName());
				}else{
					bpmProcessInstance.setLocalizedName(task.getAssignee() + "-账号异常");
				}
			}else{
				bpmProcessInstance.setName("流程已结束");
				bpmProcessInstance.setLocalizedName("无");
			}
			bpmProcessInstance.setEnded(isEnded);
			bpmProcessInstance.setId(historicTaskInstance.getId());
			bpmProcessInstance.setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
			bpmProcessInstance.setProcessInstanceId(historicTaskInstance.getId());
			bpmProcessInstance.setSuspended(processInstance != null && processInstance.isSuspended());
			bpmProcessInstance.setTenantId(historicTaskInstance.getTenantId());
			bpmProcessInstance.setProcessDefinitionKey(taskInfo.get("definitionKey"));
			bpmProcessInstance.setProcessDefinitionName(taskInfo.get("processName"));
			bpmProcessInstance.setApplyUserName(taskInfo.get("applyUserName"));
			bpmProcessInstance.setApplyUser(taskInfo.get("loginName"));
			Map<String,Object> varivables = new HashMap<>();
			varivables.put("startTime", historicTaskInstance.getStartTime());
			varivables.put("endTime", historicTaskInstance.getEndTime());
					bpmProcessInstance.setProcessVariables(varivables);
			//bpmProcessInstance.setTaskId(task.getId());
			//bpmProcessInstance.setParentId(processInstance.getParentId());
			//bpmProcessInstance.setProcessVariables(processInstance.getProcessVariables());
			//bpmProcessInstance.setDeploymentId(processInstance.getDeploymentId());
			//bpmProcessInstance.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());

			//bpmProcessInstance.setLocalizedName(processInstance.getLocalizedName());
			/*String applyUser = historicTaskInstance.getAssignee();
			System.out.println("======++++++++++++---------------申请人：" +  applyUser);
			if(StringUtils.isNotEmpty(applyUser)) {
				SysUser user = null;
				try {
					user = userService.findByLoginName(applyUser);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				bpmProcessInstance.setApplyUser(applyUser);
				if (user == null) {
					bpmProcessInstance.setApplyUserName("未知");
				} else {
					bpmProcessInstance.setApplyUserName(user.getName());
				}
			}*/
			bpmList.add(bpmProcessInstance);
		}
		PageDTO<BpmProcessInstance> pageDTO = new PageTool<BpmProcessInstance>().getPage(bpmList, pageSize, 1);
		pageDTO.setTotal(total);
		return pageDTO;
	}
}
