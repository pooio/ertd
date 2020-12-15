package com.metaShare.modules.bpm.service;

import java.util.Map;

/**
 * 流程核心service主要是流程转向等特殊的核心操作
 * @author: zhaojie/zh_jie@163.com.com 
 * @date: 2016年1月19日 上午11:00:43
 */
public interface BpmCoreService {
	/**
	 * 结束流程，会在流程变量中添加变量processEndType<br>
	 * 如果是直接结束并且审批通过,processEndType值为：pass<br>
	 * 如果是直接结束并且审批不通过,processEndType值为：notPass<br>
	* @param taskId 当前任务ID 
     * @param pass  是否审批通过
     * @param comment  审批意见
	 * @throws Exception 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月19日 上午10:41:25
	 */
	public void endProcess(String taskId, Boolean pass, String comment) throws Exception;
	 /** 
	  * 完成任务或者任务转向
     * @param taskId 当前任务ID 
     * @param pass  是否审批通过
     * @param comment  审批意见
     * @param variables 流程变量 
     * @param activityId 
     *            流程转向执行任务节点ID<br> 
     *            此参数为空，默认为提交操作 
	 * @throws Exception 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月19日 上午10:44:28
	 */
	public void commitProcess(String taskId, Boolean pass, String comment, Map<String, Object> variables, String activityId) throws Exception;
	
	/**
	 * 流程转向操作
	 * @param taskId 当前任务ID 
	 * @param activityId  目标节点任务ID 
	 * @param variables  流程变量 
	 * @throws Exception 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月19日 上午10:53:59
	 */
	public void turnTransition(String taskId, Boolean pass, String comment, String activityId, Map<String, Object> variables) throws Exception;
	
 public  Boolean checkNotPassRoute(String taskId, String notPassExpression, boolean taskPass) throws Exception;

	public Boolean checkIsFinish(String taskId, Boolean flag) throws Exception;

}
