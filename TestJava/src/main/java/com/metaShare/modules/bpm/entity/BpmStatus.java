package com.metaShare.modules.bpm.entity;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.metaShare.modules.core.entity.CustomBaseEntity;

public class BpmStatus extends CustomBaseEntity {
	private static final long serialVersionUID = 5745482023988292513L;
	private String businessKey;
	private String processDefinitionKey;
	private String processInstinceId;
	private Long processStatus;
	private boolean isActive;
	private Date createTime;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	private String createUser;
	
	private String updateUser;
	@TableField(exist=false)
	private String status;
	
	public String getStatus() {
		if(StringUtils.isEmpty(status)){
			if(processStatus==0){
				status = "待审批";
			}else if(processStatus==1){
				status = "审批通过";
			}else if(processStatus==2){
				status = "审批不通过";
			}else {
				status = "未提交";
			}
		}
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getProcessInstinceId() {
		return processInstinceId;
	}
	public void setProcessInstinceId(String processInstinceId) {
		this.processInstinceId = processInstinceId;
	}
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
		this.processStatus = processStatus;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
}
