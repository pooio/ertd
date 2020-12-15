package com.metaShare.modules.bpm.service;

import java.io.InputStream;
import java.util.List;

import com.metaShare.common.tool.pageTool.PageTool;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.modules.bpm.dto.BpmProcessInstance;



/**
 * 流程实例服务接口
 * 
 *
 */
public interface BpmProcessInstanceService {
	/**
	 * 根据流程实例id获取流程历史信息
	 * @param processInstanceId
	 * @return 
	 * @return HistoricProcessInstance
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public HistoricProcessInstance getHistoricProcessInstance(String processInstanceId);
	/**
	 * 根据流程实例id获取流程实例信息
	 * @param processInstanceId
	 * @return 
	 * @return ProcessInstance
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月26日 上午8:50:35
	 */
	public ProcessInstance getProcessInstance(String processInstanceId);
	/**
	 * 获得所有的流程实例
	 * @param page
	 * @param businessKey
	 * @param processDefinitionName
	 * @return 
	 * @return PageDTO<BpmProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月13日 上午8:32:18
	 */
	public PageDTO<BpmProcessInstance> listProcessInstances(PageDTO<BpmProcessInstance> page
            , String businessKey, String processDefinitionName, String loginName);
	/**
	 * 获得所有的正在运行的流程实例
	 * @param page
	 * @return 
	 * @return PageDTO<BpmProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:40:47
	 */
	public PageDTO<BpmProcessInstance> findRunningProcessInstaces(PageDTO<BpmProcessInstance> page);
	/**
	 * 获得所有的已完成的流程实例
	 * @param page
	 * @return 
	 * @return PageDTO<HistoricProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:41:41
	 */
	public PageDTO<HistoricProcessInstance> findFinishedProcessInstaces(PageDTO<HistoricProcessInstance> page);
	/**
	 * 根据流程定义key获得正在运行的流程实例
	 * @param page
	 * @param processDefinitionKey
	 * @return 
	 * @return PageDTO<BpmProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:41:54
	 */
	public PageDTO<BpmProcessInstance> findRunningProcessInstaces(PageDTO<BpmProcessInstance> page, String processDefinitionKey);
	/**
	 * 根据流程定义ke获得已完成的流程实例
	 * @param page
	 * @param processDefinitionKey
	 * @return 
	 * @return PageDTO<HistoricProcessInstance>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:42:16
	 */
	public PageDTO<HistoricProcessInstance> findFinishedProcessInstaces(PageDTO<HistoricProcessInstance> page, String processDefinitionKey);
	/**
	 * 根据流程实例id，删除正在运行的流程实例
	 * @param processInstanceId
	 * @param deleteReason 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:42:39
	 */
	public void deleteProcessInstance(String processInstanceId, String deleteReason);
	/**
	 * 暂停流程实例
	 * @param processInstanceId 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:43:04
	 */
	public void suspendProcessInstance(String processInstanceId);
	/**
	 * 激活流程实例
	 * @param processInstanceId 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:43:16
	 */
	public void activeProcessInstance(String processInstanceId);
	/**
	 * 删除已经结束的流程实例
	 * @param processInstanceId 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:43:24
	 */
	public void deleteProcessInstanceHistory(String processInstanceId);
	/**
	 * 获得流程实例历史信息的图片
	 * @param processInstanceId
	 * @return 
	 * @return InputStream
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:44:27
	 */
	public InputStream getHistoryProcessInstanceImg(String processInstanceId);
	/**
	 * 根据任务id获取流程实例
	 * @param taskId
	 * @return 
	 * @return ProcessInstance
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月19日 上午9:44:56
	 */
	public ProcessInstance findProcessInstanceByTaskId(String taskId) throws Exception;


	PageDTO<BpmProcessInstance> listAllProcessInstances(int pageNum, int pageSize, String processDefinitionName, String loginName,String processName,Integer type);
}
