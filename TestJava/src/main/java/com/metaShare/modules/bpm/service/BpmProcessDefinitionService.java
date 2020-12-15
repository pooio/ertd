package com.metaShare.modules.bpm.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.modules.bpm.dto.BpmProcessDefinition;
import com.metaShare.modules.sys.entity.SysUser;

/**
 * 流程定义服务接口
 * 
 *
 */
public interface BpmProcessDefinitionService {
	/**
	 * 启动流程
	 * @param user 用户
	 * @param businessKey 业务主键
	 * @param processDefinitionKey 流程定义key
	 * @param processVariables 流程变量
	 * @return 
	 * @return ProcessInstance
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public ProcessInstance startWorkflow(SysUser user, String businessKey, String processDefinitionKey, Map<String, Object> processVariables,String businessName,String title);
	/**
	 * 获取所有的流程定义
	 * @param page
	 * @param latestVersion 是否查询最后的版本
	 * @return 
	 * @return PageDTO<BpmProcessDefinition>
	 * @author: zhaojie/zh_jie@163.com 
	 */
    public PageDTO<BpmProcessDefinition> listProcessDefinitions(PageDTO<BpmProcessDefinition> page, Boolean latestVersion);
    
    /**
	 * 获取所有的流程定义 不分页
	 * @param latestVersion 是否查询最后的版本
	 * @return 
	 * @return List<BpmProcessDefinition>
	 * @author: zhaojie/zh_jie@163.com 
	 */
    public List<BpmProcessDefinition> listProcessDefinitions(Boolean latestVersion);
    /**
     * 挂起流程定义
     * @param processDefinitionId 
     * @return void
     * @author: zhaojie/zh_jie@163.com 
     */
    public void suspendProcessDefinition(String processDefinitionId);
    /**
     * 启动流程定义
     * @param processDefinitionId 
     * @return void
     * @author: zhaojie/zh_jie@163.com 
     */
    public void activeProcessDefinition(String processDefinitionId);
    /**
     * 根据流程定义id获取流程定义图片
     * @param processDefinitionId
     * @return 
     * @return InputStream
     * @author: zhaojie/zh_jie@163.com 
     */
    public InputStream getProcessDefinitionImg(String processDefinitionId);
    /**
     * 根据流程定义key获取流程定义图片
     * @param processDefinitionKey
     * @return 
     * @return InputStream
     * @author: zhaojie/zh_jie@163.com 
     */
    public InputStream getProcessDefinitionImgByKey(String processDefinitionKey);
    /**
     * 根据流程定义获得流程定义xml
     * @param processDefinitionId
     * @return 
     * @return InputStream
     * @author: zhaojie/zh_jie@163.com 
     */
    public InputStream getProcessDefinitionXml(String processDefinitionId);
    /**
     * 根据流程定义key获得流程定义xml
     * @param processDefinitionKey
     * @return 
     * @return InputStream
     * @author: zhaojie/zh_jie@163.com 
     */
    public InputStream getProcessDefinitionXmlByKey(String processDefinitionKey);
    /**
     * 根据流程定义id获得流程定义
     * @param processDefinitionId
     * @return 
     * @return ProcessDefinition
     * @author: zhaojie/zh_jie@163.com 
     */
	public ProcessDefinition getProcessDefinition(String processDefinitionId);
	/**
	 * 删除流程部署信息
	 * @param deployIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public void deleteDeployment(String[] deployIds);
	/**
	 * 删除流程部署以及流程实例信息
	 * @param deployIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public void deleteDeploymentAndProcessInstance(String[] deployIds);
	/**
	 * 根据任务ID获取流程定义
	 * @param taskId
	 * @return
	 * @throws Exception 
	 * @return ProcessDefinitionEntity
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception;
}
