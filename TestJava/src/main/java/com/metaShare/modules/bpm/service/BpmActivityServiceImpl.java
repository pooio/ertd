package com.metaShare.modules.bpm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;


@Service(value="bpmActivityService")
public class BpmActivityServiceImpl implements BpmActivityService {

	@Autowired
	RuntimeService runtimeService;
	@Autowired
	HistoryService historyService;
	@Autowired
	BpmProcessDefinitionService bpmProcessDefinitionService;
	@Autowired
	BpmTaskService bpmTaskService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected RepositoryService repositoryService;
	
	@Override
	public List<HistoricActivityInstance> listHistoricActivityInstance(String processInstanceId) {
		return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list();
	}

	@Override
	public List<String> listActivityIds(String processInstanceId) {
		 String executionId = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult().getId();
		return runtimeService.getActiveActivityIds(executionId);
	}
	
	@Override
	public ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception {
		 // 取得流程定义  
        ProcessDefinitionEntity processDefinition = bpmProcessDefinitionService.findProcessDefinitionEntityByTaskId(taskId);  
  
        // 获取当前活动节点ID  
        if (StringUtils.isBlank(activityId)) {  
            activityId = bpmTaskService.findTaskById(taskId).getTaskDefinitionKey();  
        }  
  
        // 根据流程定义，获取该流程实例的结束节点  
        if (activityId.toUpperCase().equals("END")) {  
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {  
                List<PvmTransition> pvmTransitionList = activityImpl  
                        .getOutgoingTransitions();  
                if (pvmTransitionList.isEmpty()) {  
                    return activityImpl;  
                }  
            }  
        }  
  
        // 根据节点ID，获取对应的活动节点  
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)  
                .findActivity(activityId);  
  
        return activityImpl;  
	}

	@Override
	public TaskDefinition nextTaskDefinition(String procInstId) {
		TaskDefinition task = null;
		String id = null;
		
		//获取流程实例Id信息   
        String processInstanceId = taskService.createTaskQuery().taskId(procInstId).singleResult().getProcessInstanceId();
		
        //获取流程发布Id信息   
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();  

        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
                .getDeployedProcessDefinition(definitionId);  

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();  

        //当前流程节点Id信息   
        String activitiId = execution.getActivityId();
        
        //获取流程所有节点信息   
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        
      //遍历所有节点信息   
        for(ActivityImpl activityImpl : activitiList){      
            id = activityImpl.getId();     
            if (activitiId.equals(id)) {
                //获取下一个节点信息   
                task = nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId); 
                break;
            }
        }  
        return task;
	}
	
	/**  
     * 下一个任务节点信息,  
     *  
     * 如果下一个节点为用户任务则直接返回,  
     *  
     * 如果下一个节点为排他网关, 获取排他网关Id信息, 根据排他网关Id信息和execution获取流程实例排他网关Id为key的变量值,  
     * 根据变量值分别执行排他网关后线路中的el表达式, 并找到el表达式通过的线路后的用户任务
     * @param ActivityImpl activityImpl     流程节点信息  
     * @param String activityId             当前流程节点Id信息  
     * @param String elString               排他网关顺序流线段判断条件
     * @param String processInstanceId      流程实例Id信息  
     * @return  
     */    
    private TaskDefinition nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString, String processInstanceId){   

        PvmActivity ac = null;
        Object conditionText = null;

        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior())
                    .getTaskDefinition();
            return taskDefinition;
        } else {
            // 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                ac = tr.getDestination(); // 获取线路的终点节点
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();

                    // 如果网关路线判断条件为空信息
                    if (StringUtils.isEmpty(elString)) {
                        // 获取流程启动时设置的网关判断条件信息
                        elString = getGatewayCondition(ac.getId(), processInstanceId);
                    }

                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        return nextTaskDefinition((ActivityImpl) outTransitionsTemp.get(0).getDestination(), activityId,
                                elString, processInstanceId);
                    } else if (outTransitionsTemp.size() > 1) { // 如果排他网关有多条线路信息
                        for (PvmTransition tr1 : outTransitionsTemp) {
                        	conditionText = tr1.getProperty("conditionText"); // 获取排他网关线路判断条件信息
                        	// 判断el表达式是否成立
                            if (isCondition(ac.getId(), StringUtils.trim(conditionText.toString()), elString)) {
                                return nextTaskDefinition((ActivityImpl) tr1.getDestination(), activityId, elString,
                                        processInstanceId);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    return ((UserTaskActivityBehavior) ((ActivityImpl) ac).getActivityBehavior()).getTaskDefinition();
                } else {
                }
            }
            return null;
        }
    }  

    /** 
     * 查询流程启动时设置排他网关判断条件信息  
     * @param String gatewayId          排他网关Id信息, 流程启动时设置网关路线判断条件key为网关Id信息  
     * @param String processInstanceId  流程实例Id信息  
     * @return 
     */  
    private String getGatewayCondition(String gatewayId, String processInstanceId) {  
        Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
        Object object= runtimeService.getVariable(execution.getId(), gatewayId);
        return object==null? "":object.toString();  
    }  

    /** 
     * 根据key和value判断el表达式是否通过信息  
     * @param String key    el表达式key信息  
     * @param String el     el表达式信息  
     * @param String value  el表达式传入值信息  
     * @return 
     */  
    private boolean isCondition(String key, String el, String value) { 
    	key = "deptId";
    	value = "true";
        ExpressionFactory factory = new ExpressionFactoryImpl();    
        SimpleContext context = new SimpleContext();
        context.setVariable(key, factory.createValueExpression(value, String.class));    
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);    
        return (Boolean) e.getValue(context);  
    } 
    
    /**
   	 * 获取当前活动节点的信息
   	 * 
   	 * @param businessKey
   	 *            业务ID
   	 * @param processDefinitionKey
   	 *            流程标识符
   	 *@param processInstanceId
   	 *            流程实例ID
   	 * @return
   	 * @throws Exception 
   	 */
       @Override
   	public Map<String,Object> findCurrNodeInfo (
   			String processDefinitionKey,
   			String processInstanceId) throws Exception {
   		Map<String,Object> retMap = new HashMap<String,Object>();
   		//当前节点
   		List<Task> task = bpmTaskService.getProcessInstancesTask(processInstanceId);
   		// 流程模型
   		List<Model> models = repositoryService.createModelQuery()
   				.modelKey(processDefinitionKey).list();
   		//流程历史记录
   		List<BpmHistoricTaskInstance> bpmHistoricTaskInstance = bpmTaskService.listHistoricTaskInstance(processInstanceId);
   		//流程实例
   		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                   .processInstanceId(processInstanceId).singleResult();
   		
   		retMap.put("tasks", task);
   		retMap.put("models", models);
   		retMap.put("bpmHistoricTaskInstance", bpmHistoricTaskInstance);
   		retMap.put("processInstance", pi);
   		return retMap;
   	}
}
