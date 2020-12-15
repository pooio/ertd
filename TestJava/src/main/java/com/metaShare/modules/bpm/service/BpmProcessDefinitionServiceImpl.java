package com.metaShare.modules.bpm.service;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.bpm.cmd.ProcessDefinitionDiagramCmd;
import com.metaShare.modules.bpm.dto.BpmProcessDefinition;
import com.metaShare.modules.bpm.entity.BpmStatus;
import com.metaShare.modules.bpm.exception.BpmException;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysMessageService;
import com.metaShare.modules.sys.service.SysUserService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


/**
 * 流程定义服务实现类，提供流程定义相关方法
 * 
 *
 */
@Service(value="bpmProcessDefinitionService")
public class BpmProcessDefinitionServiceImpl implements BpmProcessDefinitionService {

	private static Logger log = LoggerFactory.getLogger(BpmProcessDefinitionServiceImpl.class);
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected BpmStatusService bpmStatusService;
    @Autowired
	protected BpmTaskService bpmTaskService;
    
    @Autowired
	protected ProcessEngine processEngine;
    @Autowired
	protected BpmModelService bpmModelService;
	@Autowired
	SysUserService userService;
	@Autowired
	private SysMessageService sysMessageService;

	@Override
	public ProcessInstance startWorkflow(SysUser user, String businessKey, String processDefinitionKey,
			Map<String, Object> processVariables,String businessName,String title) {
		BpmnModel model = bpmModelService.getBpmModelByProcessDefinitionKey(processDefinitionKey);
		if(processVariables == null) {
			processVariables = new HashMap<String,Object>();
		}
		log.info("流程进行中");
		 //用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
		identityService.setAuthenticatedUserId(user.getLoginName());
		log.info("流程发起人："+user.getLoginName());
		processVariables.put(BpmConstants.BUSINESS_KEY, businessKey);
		
		//如果未设置流程发起人，则取当前登录用户为流程发起人
		if(StringUtils.isEmpty((String)processVariables.get(BpmConstants.APPLY_USER))){
			processVariables.put(BpmConstants.APPLY_USER, user.getLoginName());
		}
		
		String businessInfo = (String)processVariables.get(BpmConstants.BUSINESS_INFO);
		if(StringUtils.isBlank(businessInfo)){
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
			businessInfo = processDefinition.getName() + "（" + businessName +"）";
		}
		processVariables.put(BpmConstants.BUSINESS_INFO, businessInfo);
		if(StrUtils.isEmpty(title)){
			processVariables.put("title", user.getName()+ " 发起 " + model.getProcesses().get(0).getName());
		}else{
			processVariables.put("title", user.getName()+ " 发起 " +title);
		}

		processVariables.put("sponsorName", user.getName());

		Set<Entry<String, Object>> variablesSet = processVariables.entrySet();
		//如果设置了会签负责人变量,并且设置的变量为逗号分隔的字符串方式，这里转为List
		for(Entry<String, Object> entry : variablesSet){
		    if(entry.getKey().endsWith("AssigneeSignList")){
		        if(entry.getValue() instanceof String){
		            if(StringUtils.isNotEmpty((String)entry.getValue()) ){
		                entry.setValue( Arrays.asList(((String) entry.getValue()).split(",")));
		            }
		        }
		    }
		}
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, processVariables);
		String processInstanceId = processInstance.getProcessInstanceId();
		processInstance.getProcessDefinitionName();
		
		//将活跃标记设置为false
		//bpmStatusService.updateActiveFalseBybusinessKey(businessKey);
		bpmStatusService.deleteByBusinessKeyAndDefineKey(businessKey,processDefinitionKey);
		
		//在流程创建成功后，在中间表中插入一条信息
    	BpmStatus bpmStatus = new BpmStatus();
    	bpmStatus.setBusinessKey(businessKey);
    	bpmStatus.setProcessDefinitionKey(processDefinitionKey);
    	bpmStatus.setProcessInstinceId(processInstanceId);
    	bpmStatus.setProcessStatus(0L);
    	bpmStatus.setActive(true);
    	bpmStatus.setCreateTime(new Date());
    	bpmStatus.setCreateUser(user.getId());
    	bpmStatusService.insert(bpmStatus);


    	log.debug("流程启动完成，成功写入流程状态信息！");
    	
		log.debug("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{"leave", businessKey, processInstanceId, processVariables});
		return processInstance;
	}
	
	@Override
	public PageDTO<BpmProcessDefinition> listProcessDefinitions(PageDTO<BpmProcessDefinition> page,Boolean latestVersion) {
		long count = 0;
		 List<ProcessDefinition> processDefinitions;
		if(latestVersion){
			count = repositoryService.createProcessDefinitionQuery().latestVersion().count();
			processDefinitions = repositoryService
	                .createProcessDefinitionQuery().latestVersion()
	                .list();//listPage((page.getPageNumber()-1)*page.getPageSize(),page.getPageSize());
		}else{
			count = repositoryService.createProcessDefinitionQuery().count();
			processDefinitions = repositoryService
	                .createProcessDefinitionQuery()
	                .list();//listPage((page.getPageNumber()-1)*page.getPageSize(),page.getPageSize());
		}
	       
	        //将ProcessDefinition转为自定义BpmProcessDefinition
	        List<BpmProcessDefinition> list = createBpmProcessDefinitionList(processDefinitions);
		return new PageTool<BpmProcessDefinition>().getPage(list,(int)page.getPages() ,page.getCurrent());
	}

	@Override
	public List<BpmProcessDefinition> listProcessDefinitions(Boolean latestVersion) {
		 List<ProcessDefinition> processDefinitions;
		if(latestVersion){
			processDefinitions = repositoryService
	                .createProcessDefinitionQuery().latestVersion()
	                .list();
		}else{
			processDefinitions = repositoryService
	                .createProcessDefinitionQuery()
	                .list();
		}
	    	
		return createBpmProcessDefinitionList(processDefinitions);
	}
	
	
	private List<BpmProcessDefinition> createBpmProcessDefinitionList(List<ProcessDefinition> processDefinitions){
		//将ProcessDefinition转为自定义BpmProcessDefinition
        List<BpmProcessDefinition> list = new ArrayList<BpmProcessDefinition>();
    	for(ProcessDefinition definition : processDefinitions){
    		BpmProcessDefinition bpmProcessDefinition = new BpmProcessDefinition();
    		bpmProcessDefinition.setId(definition.getId());
    		bpmProcessDefinition.setKey(definition.getKey());
    		bpmProcessDefinition.setName(definition.getName());
    		bpmProcessDefinition.setDescription(definition.getDescription());
    		bpmProcessDefinition.setResourceName(definition.getResourceName());
    		bpmProcessDefinition.setDeploymentId(definition.getDeploymentId());
    		bpmProcessDefinition.setCategory(definition.getCategory());
    		bpmProcessDefinition.setDiagramResourceName(definition.getDiagramResourceName());
    		bpmProcessDefinition.setStartFormKey(definition.hasStartFormKey());
    		bpmProcessDefinition.setSuspended(definition.isSuspended());
    		bpmProcessDefinition.setTenantId(definition.getTenantId());
    		bpmProcessDefinition.setVersion(definition.getVersion());
    		bpmProcessDefinition.setGraphicalNotationDefined(definition.hasGraphicalNotation());
    		list.add(bpmProcessDefinition);
    	}
    	return list;
	}

	@Override
	public void suspendProcessDefinition(String processDefinitionId) {
		  repositoryService.suspendProcessDefinitionById(processDefinitionId,true, null);
	}

	@Override
	public void activeProcessDefinition(String processDefinitionId) {
			repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
	}

	@Override
	public InputStream getProcessDefinitionImg(String processDefinitionId) {
		 Command<InputStream> cmd = new ProcessDefinitionDiagramCmd(processDefinitionId);
		 return managementService.executeCommand(cmd);
	}

	@Override
	public InputStream getProcessDefinitionImgByKey(String processDefinitionKey) {
		ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
		return this.getProcessDefinitionImg(definition.getId());
	}
	
	@Override
	public InputStream getProcessDefinitionXml(String processDefinitionId) {
		  ProcessDefinition processDefinition = repositoryService
	                .createProcessDefinitionQuery()
	                .processDefinitionId(processDefinitionId).singleResult();
	        String resourceName = processDefinition.getResourceName();
	        InputStream resourceAsStream = repositoryService.getResourceAsStream(
	                processDefinition.getDeploymentId(), resourceName);
		return resourceAsStream;
	}
	
	@Override
	public ProcessDefinition getProcessDefinition(String processDefinitionId) {
		 ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				 .processDefinitionId(processDefinitionId).singleResult();
	        return processDefinition;
	}

	@Override
	public void deleteDeployment(String[] deployIds) {
		for(String id : deployIds){
			repositoryService.deleteDeployment(id);
		}
	}

	@Override
	public void deleteDeploymentAndProcessInstance(String[] deployIds) {
		for(String id : deployIds){
			repositoryService.deleteDeployment(id,true);
		}
	}

	@Override
	public InputStream getProcessDefinitionXmlByKey(String processDefinitionKey) {
		  ProcessDefinition processDefinition = repositoryService
	                .createProcessDefinitionQuery()
	                .processDefinitionKey(processDefinitionKey).singleResult();
	        String resourceName = processDefinition.getResourceName();
	        InputStream resourceAsStream = repositoryService.getResourceAsStream(
	                processDefinition.getDeploymentId(), resourceName);
		return resourceAsStream;
	}

	@Override
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(String taskId) throws Exception {
		// 取得流程定义  
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)  
                .getDeployedProcessDefinition(bpmTaskService.findTaskById(taskId)  
                        .getProcessDefinitionId());  
  
        if (processDefinitionEntity == null) {  
        	throw new BpmException("根据任务id:"+taskId+"未找到流程定义！");
        }  
  
        return processDefinitionEntity;  
	}
	
}
