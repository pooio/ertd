package com.metaShare.modules.bpm.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;

/**
 * Activity服务接口
 * 
 *
 */
public interface BpmActivityService {
	/**
	 * 根据流程实例id获取流程活动节点实例集合
	 * @param processInstanceId
	 * @return 
	 * @return List<HistoricActivityInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public List<HistoricActivityInstance> listHistoricActivityInstance(String processInstanceId);
	/**
	 * 根据流程实例id获得活动节点Id集合
	 * @param processInstanceId
	 * @return 
	 * @return List<String>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public List<String> listActivityIds(String processInstanceId);
	  /** 
     * 根据任务ID和节点ID获取活动节点 <br> 
     *  
     * @param taskId 
     *            任务ID 
     * @param activityId 
     *            活动节点ID <br> 
     *            如果为null或""，则默认查询当前活动节点 <br> 
     *            如果为"end"，则查询结束节点 <br> 
	 * @throws Exception 
	 * @return ActivityImpl
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public ActivityImpl findActivitiImpl(String taskId, String activityId) throws Exception;
	
	/**
	 * 实例ID获取下一节点
	 * @param procInstId
	 * @return
	 */
	public TaskDefinition nextTaskDefinition(String procInstId);
	
	/**
	 * 获取当前几点的信息
	 * @param processDefinitionKey
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> findCurrNodeInfo(
            String processDefinitionKey,
            String processInstanceId) throws Exception;
	
}