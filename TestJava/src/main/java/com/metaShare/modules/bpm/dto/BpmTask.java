package com.metaShare.modules.bpm.dto;

import java.util.Date;
import java.util.Map;

import io.swagger.models.auth.In;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;

public class BpmTask implements Task {
	
	private String assignee;
	private String category;
	private Date createTime;
	private String description;
	private Date dueDate;
	private String executionId;
	private String formKey;
	private String id;
	private String name;
	private String owner;
	private String parentTaskId;
	private int priority;
	private String processDefinitionId;
	private String processInstanceId;
	private Map<String, Object> processVariables;
	private String taskDefinitionKey;
	private  Map<String, Object> taskLocalVariables;
	private String tenantId;
	private DelegationState delegationState;
	private  boolean isSuspended;
	
	private String businessKey;
	private String processDefinitionName;
	private String assigneeName;
	
	private String localizedName;
	private String localizedDescription;
	
	private String applyUserName;
	private String applyUser;
	private Integer approvalStatus;

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getAssigneeName() {
		return assigneeName;
	}
	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}
	public String getProcessDefinitionName() {
		return processDefinitionName;
	}
	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}
	@Override
	public String getAssignee() {
		return assignee;
	}
	@Override
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	@Override
	public String getCategory() {
		return category;
	}
	@Override
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String getDescription() {
		return description;
	}
	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public Date getDueDate() {
		return dueDate;
	}
	@Override
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	@Override
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	@Override
	public String getFormKey() {
		return formKey;
	}
	@Override
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getOwner() {
		return owner;
	}
	@Override
	public void setOwner(String owner) {
		this.owner = owner;
	}
	@Override
	public String getParentTaskId() {
		return parentTaskId;
	}
	@Override
	public void setParentTaskId(String parentTaskId) {
		this.parentTaskId = parentTaskId;
	}
	@Override
	public int getPriority() {
		return priority;
	}
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	@Override
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	@Override
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	@Override
	public Map<String, Object> getProcessVariables() {
		return processVariables;
	}
	public void setProcessVariables(Map<String, Object> processVariables) {
		this.processVariables = processVariables;
	}
	@Override
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}
	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
	@Override
	public Map<String, Object> getTaskLocalVariables() {
		return taskLocalVariables;
	}
	public void setTaskLocalVariables(Map<String, Object> taskLocalVariables) {
		this.taskLocalVariables = taskLocalVariables;
	}
	@Override
	public String getTenantId() {
		return tenantId;
	}
	@Override
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	@Override
	public DelegationState getDelegationState() {
		return delegationState;
	}
	@Override
	public void setDelegationState(DelegationState delegationState) {
		this.delegationState = delegationState;
	}
	@Override
	public boolean isSuspended() {
		return isSuspended;
	}
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}
	

	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	
	
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public String getApplyUser() {
		return applyUser;
	}
	public void setApplyUser(String applyUser) {
		this.applyUser = applyUser;
	}
	@Override
	public void delegate(String userId) {
		//不需要实现
	}
	
	public String getLocalizedDescription(){
		return localizedDescription;
	}
	@Override
	public void setLocalizedDescription(String localizedDescription) {
		this.localizedDescription =localizedDescription;
	}
	public String getLocalizedName(){
		return localizedName;
	}
	@Override
	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}
	

}
