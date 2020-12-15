package com.metaShare.modules.bpm.service;

import org.activiti.bpmn.model.BpmnModel;

import java.util.Map;


/**
 * 流程模型服务接口
 * @author: zhaojie/zh_jie@163.com.com 
 * @version: 2016年1月15日 下午5:44:50
 */
public interface BpmModelService {
	
	/**
	 * 根据流程定义key获得流程模型对象
	 * @param processDefinitionKey
	 * @return 
	 * @return BpmnModel
	 * @author: zhaojie/zh_jie@163.com 
	 */
	public BpmnModel getBpmModelByProcessDefinitionKey(String processDefinitionKey);

}
