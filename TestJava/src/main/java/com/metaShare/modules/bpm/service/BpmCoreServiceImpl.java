package com.metaShare.modules.bpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metaShare.modules.bpm.BpmConstants;


@Service(value = "bpmCoreService")
public class BpmCoreServiceImpl implements BpmCoreService {
	@Autowired
	BpmActivityService bpmActivityService;
	@Autowired
	BpmTaskService bpmTaskService;
	@Autowired
	protected TaskService taskService;

	@Override
	public void endProcess(String taskId,Boolean pass, String comment) throws Exception {
		 ActivityImpl endActivity = bpmActivityService.findActivitiImpl(taskId, "end");  
		 Map<String, Object> variables =  new HashMap<String, Object>();
		 if ( pass ) {
			 variables.put(BpmConstants.PROCESS_END_TYPE, BpmConstants.PASS);
		 } else {
			 variables.put(BpmConstants.PROCESS_END_TYPE, BpmConstants.NOT_PASS);
		 }
         commitProcess(taskId, pass,comment,variables, endActivity.getId());
	}

	@Override
	public void commitProcess(String taskId,Boolean pass, String comment, Map<String, Object> variables, String activityId) throws Exception {
		   if (variables == null) {  
               variables = new HashMap<String, Object>();  
           }  
           // 跳转节点为空，默认提交操作  
           if (StringUtils.isBlank(activityId)) {
        	    variables.remove(BpmConstants.PROCESS_END_TYPE);
        	    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
	       		String processInstanceId = task.getProcessInstanceId();
	       		taskService.addComment(taskId, processInstanceId, pass?BpmConstants.PASS:BpmConstants.NOT_PASS, comment);
	       		variables.put(task.getTaskDefinitionKey(), pass) ;
	       		taskService.complete(taskId, variables);
           } else {// 流程转向操作  
               turnTransition(taskId,pass,comment,activityId, variables);  
           }  
	}

	@Override
	public void turnTransition(String taskId, Boolean pass, String comment,String activityId, Map<String, Object> variables) throws Exception {
		// 当前节点  
        ActivityImpl currActivity = bpmActivityService.findActivitiImpl(taskId, null);  
        // 清空当前流向  
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);  
  
        // 创建新流向  
        TransitionImpl newTransition = currActivity.createOutgoingTransition();  
        // 目标节点  
        ActivityImpl pointActivity = bpmActivityService.findActivitiImpl(taskId, activityId);  
        // 设置新流向的目标节点  
        newTransition.setDestination(pointActivity);  
  
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
		taskService.addComment(taskId, processInstanceId, pass?BpmConstants.PASS:BpmConstants.NOT_PASS, comment);
		//添加流程变量来标识该任务是审批通过还是不通过，标识为任务的id
		//例如 id为hrTask的任务在完成后会在流程变量添加一个名为：hrTask的变量，值为true 或者false
		variables.put(task.getTaskDefinitionKey(), pass) ;
		
        // 执行转向任务  
        taskService.complete(taskId,variables);
       
        // 删除目标节点新流入  
        pointActivity.getIncomingTransitions().remove(newTransition);  
  
        // 还原以前流向  
        restoreTransition(currActivity, oriPvmTransitionList);  
	}

	/**
	 * 清空指定活动节点流向 
	 * @param activityImpl
	 * @return 
	 * @return List<PvmTransition>
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月19日 上午11:02:06
	 */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) { 
        // 存储当前节点所有流向临时变量  
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
        // 获取当前节点所有流向，存储到临时变量，然后清空  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        for (PvmTransition pvmTransition : pvmTransitionList) {  
            oriPvmTransitionList.add(pvmTransition);  
        }  
        pvmTransitionList.clear();  
  
        return oriPvmTransitionList;  
    }  
  
   /**
     * 还原指定活动节点流向 
     *  
     * @param activityImpl 活动节点 
     * @param oriPvmTransitionList 原有节点流向集合 
     * @return void
     * @author: zhaojie/zh_jie@163.com 
     * @date: 2016年1月19日 上午11:01:38
     */
    private void restoreTransition(ActivityImpl activityImpl,  
            List<PvmTransition> oriPvmTransitionList) {  
        // 清空现有流向  
        List<PvmTransition> pvmTransitionList = activityImpl  
                .getOutgoingTransitions();  
        pvmTransitionList.clear();  
        // 还原以前流向  
        for (PvmTransition pvmTransition : oriPvmTransitionList) {  
            pvmTransitionList.add(pvmTransition);  
        }  
    }  
    
    
    @Override
    public  Boolean checkNotPassRoute(String taskId,String notPassCondition,boolean taskPass) throws Exception{
    	ActivityImpl activityImpl = bpmActivityService.findActivitiImpl(taskId, null);
    	//false代表后续连线不包含审批不通过的条件
    	Boolean flag = false;
    	List<PvmTransition> transitions =  activityImpl.getOutgoingTransitions();
    	for(PvmTransition transition : transitions) {
    		 TransitionImpl transitionImpl = (TransitionImpl) transition;  
             ActivityImpl destination = transitionImpl.getDestination();  
             String type = (String) destination.getProperty("type");  
             if ("endEvent".equals(type)) {// 结束节点
            	 
             } else if ("userTask".equals(type)) {// 用户任务  
            	 
             } else if ("exclusiveGateway".equals(type)) {// 分支路线
            	 List<PvmTransition> destinationTrans =  destination.getOutgoingTransitions();
            	 for(PvmTransition dTransition : destinationTrans){
            		 TransitionImpl dTransitionImpl = (TransitionImpl) dTransition;
            		 String conditionText =  (String) dTransitionImpl.getProperties().get("conditionText");
            		 flag = taskPass;
            	/*	 if(conditionText.contains(notPassCondition)){
            			 break;
            		 }*/
            	 }
             }
             
             if(flag){
            	 break;
             }
    	}
    	
    	return flag;
    }
    
    /**
     * 判断流程是否结束
     * @param taskId
     * @param notPassCondition
     * @return
     * @throws Exception
     */
    public  Boolean checkIsFinish(String taskId,Boolean flag) throws Exception{
    	Boolean resBoolean = null;
    	ActivityImpl activityImpl = bpmActivityService.findActivitiImpl(taskId, null);
    	List<PvmTransition> transitions =  activityImpl.getOutgoingTransitions();
    	for(PvmTransition transition : transitions) {
    		 TransitionImpl transitionImpl = (TransitionImpl) transition;  
             ActivityImpl destination = transitionImpl.getDestination();  
             String type = (String) destination.getProperty("type");  
             if ("endEvent".equals(type)) {// 结束节点
            	 resBoolean = true;
             } else if ("userTask".equals(type)) {// 用户任务  
            	 resBoolean = false;
             } else if ("exclusiveGateway".equals(type)) {// 分支路线
            	 List<PvmTransition> destinationTrans =  destination.getOutgoingTransitions();
            	 for(PvmTransition dTransition : destinationTrans){
            		 TransitionImpl dTransitionImpl = (TransitionImpl) dTransition;
            		 String conditionText =  (String) dTransitionImpl.getProperties().get("conditionText");
            		 resBoolean = false;
            	/*	 if(conditionText.contains(notPassCondition)){
            			 break;
            		 }*/
            	 }
             }
             
             if(resBoolean){
            	 break;
             }
    	}
    	
    	return resBoolean;
    }

}
