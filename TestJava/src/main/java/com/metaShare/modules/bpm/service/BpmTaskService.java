package com.metaShare.modules.bpm.service;

import java.util.List;
import java.util.Map;

import io.swagger.models.auth.In;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.modules.bpm.dto.BpmHistoricTaskInstance;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.sys.entity.SysUser;


/**
 * 任务服务接口
 * 
 *
 */
public interface BpmTaskService {
	/**
	 * 签收任务
	 * @param taskId 任务id
	 * @param user 用户实体
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public void claimTask(String taskId, SysUser user) throws Exception;
	/**
	 * 完成任务
	 * @param taskId
	 * @param taskPass
	 * @param comment
	 * @param variables 
	 * @param user 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public void completeTask(String taskId, Boolean taskPass, String comment, SysUser user, Map<String, Object> variables) throws Exception;
	/**
	 * 获取用户所有的待办，不包含候选任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public PageDTO<BpmTask> listPersonTodoTasks(SysUser user, PageDTO<BpmTask> page,Integer approvalType,String startDate,String endDate,String processDefinitionName,String applyUserName) throws Exception;
	
	/**
	 * 获取用户所有的待办部分
	 * @param user
	 * @return 
	 * @return List<BpmTask>
	 */
	public List<BpmTask> listPersonTodoTasksNoPage(SysUser user) throws Exception;
	
	/**
	 * 根据流程实例id和用户查询待办任务
	 * @param user
	 * @param processInstanceId
	 * @return
	 * @throws Exception 
	 * @return List<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月4日 上午10:59:30
	 */
	public List<BpmTask> listPersonTodoTasksByProcessInstId(SysUser user, String processInstanceId) throws Exception;
	/**
	 * 获得用户所在组的候选任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public PageDTO<BpmTask> listGroupTodoTasks(SysUser user, PageDTO<BpmTask> page) throws Exception;
	/**
	 * 获取用户的所有的待办任务以及候选任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @param searchable 
	 */
	public PageDTO<BpmTask> listPersonAndGroupTodoTasks(SysUser user, int pageSize, int pageNumber) throws Exception;
	
	
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
	public List<BpmTask> listPersonAndGroupTodoTasksWithNoPage(SysUser user, String taskDefinitionKey) throws Exception;
	/**
	 * 获得所有的待办任务
	 * @param page
	 * @return 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public PageDTO<BpmTask> listAllTodoTasks(PageDTO<BpmTask> page) throws Exception;
	/**
	 * 根据用户获取历史任务
	 * @param user
	 * @param page
	 * @return 
	 * @return PageDTO<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @param searchable 
	 */
	public PageDTO<BpmHistoricTaskInstance> listHistoryTasks(SysUser user, PageDTO<BpmHistoricTaskInstance> page, Integer approvalType,String startDate,String endDate,String processDefinitionName) throws Exception;
	
	/**
	 * 代理任务
	 * resolved值为null，user被代理的所有任务；
	 * resolved值为true，user被代理的已完成任务；
	 * resolved值为false，user被代理的未完成任务；
	 * @param resolved
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception 
	 * @return PageDTO<BpmTask>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年3月16日 下午2:16:27
	 */
	public PageDTO<BpmTask> listDelegatedTasks(Boolean resolved, SysUser user, PageDTO<BpmTask> page) throws Exception;
	/**
	 * 根据流程实例id获取最新的task
	 * @param processInstanceId
	 * @return 
	 * @return Task
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public Task getProcessInstanceTask(String processInstanceId) throws Exception;
	/**
	 * 根据流程实例id获取所有的任务
	 * @param processInstanceId
	 * @return 
	 * @return Task
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public List<Task> getProcessInstancesTask(String processInstanceId) throws Exception;
	
	/**
	 * 根据流程实例id获取所有的历史任务信息
	 * @param processInstanceId
	 * @return 
	 * @return List<BpmHistoricTaskInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public List<BpmHistoricTaskInstance> listHistoricTaskInstance(String processInstanceId) throws Exception;
	
	/**
	 * 根据taskId获取任务实例
	 * @param taskId
	 * @return 
	 * @return TaskEntity
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:29:15
	 */
	public TaskEntity findTaskById(String taskId) throws Exception;
	
	/**
	 * 根据流程实例id和Task 的 key获得所有的同级任务实例
	 * @param processInstanceId
	 * @param key
	 * @return 
	 * @return List<Task>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:30:01
	 */
	public List<Task> findTaskListByKey(String processInstanceId, String key) throws Exception;
	
	/**
	 * 根据任务id获取任务历史实例
	 * @param taskId
	 * @return
	 * @throws Exception 
	 * @return HistoricTaskInstance
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月27日 上午11:07:10
	 */
	public HistoricTaskInstance findHistoricTaskById(String taskId) throws Exception;
	
	void completeTask4App(String token, String taskId, Boolean taskPass, String comment, Map<String, Object> variables)
			throws Exception;
	
	
	/**
	 * 撤回申请
	 * @param businessKey
	 * @return
	 */
	public Boolean revokeProcess(String businessKey);
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
	List<BpmTask> listPersonAndGroupProcessDefinitionKeyNoPage(SysUser user, List<String> processDefinitionKey)
			throws Exception;
	
	
	/**
	 * 根据流程定义查询个人的待办任务
	 * @param user
	 * @param page
	 * @param processDefinitionKey 流程定义key
	 * @return 
	 * @return PageDTO<BpmTask>
	 */
	public List<BpmTask> listPersonTodoTasksAndProcessDefinitionKey(SysUser user, String processDefinitionKey) throws Exception;
}