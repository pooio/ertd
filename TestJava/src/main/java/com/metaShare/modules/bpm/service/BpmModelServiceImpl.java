package com.metaShare.modules.bpm.service;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service(value = "bpmModelService")
public class BpmModelServiceImpl implements BpmModelService {

	 @Autowired
    protected RepositoryService repositoryService;
	 
	@Override
	public BpmnModel getBpmModelByProcessDefinitionKey(String processDefinitionKey) {
		 ProcessDefinition processDefinition = repositoryService
	                .createProcessDefinitionQuery()
	                .processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
		 if(processDefinition==null){
			 return null;
		 }
		return repositoryService.getBpmnModel(processDefinition.getId());
	}
}
