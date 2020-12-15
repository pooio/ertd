package com.metaShare.modules.bpm.dto;

import org.activiti.engine.repository.ProcessDefinition;

/**
 * 流程定义实体<br/>
 * 由于activiti提供的 ProcessDefinitionEntity <br/>
 * 中 eventSupport 属性造成jackson转json会出现错误<br/>
 * 所以写了这个类，只是用于向前台展示json数据
 * 
 *
 */
public class BpmProcessDefinition implements ProcessDefinition{
	protected String id;
	protected String name;
	protected String key;
	protected int version;
	protected String category;
	protected String deploymentId;
	protected String resourceName;
	protected String tenantId;
	protected String diagramResourceName;
	protected String description;
	protected boolean isSuspended;
	protected boolean isStartFormKey;
	protected boolean isGraphicalNotationDefined;
	
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
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	@Override
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	@Override
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	@Override
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	@Override
	public String getDiagramResourceName() {
		return diagramResourceName;
	}
	public void setDiagramResourceName(String diagramResourceName) {
		this.diagramResourceName = diagramResourceName;
	}
	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public boolean isSuspended() {
		return isSuspended;
	}
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}
	@Override
	public boolean hasStartFormKey() {
		return isStartFormKey;
	}
	public void setStartFormKey(boolean isStartFormKey) {
		this.isStartFormKey = isStartFormKey;
	}
	@Override
	public boolean hasGraphicalNotation() {
		return isGraphicalNotationDefined;
	}
	public void setGraphicalNotationDefined(boolean isGraphicalNotationDefined) {
		this.isGraphicalNotationDefined = isGraphicalNotationDefined;
	}

	
	   
}
