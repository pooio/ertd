package com.metaShare.modules.bpm.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metaShare.modules.core.entity.CustomBaseEntity;

public class BpmDelegate extends CustomBaseEntity{
	private static final long serialVersionUID = 12334L;
	
	private Date createTime;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	private String createUser;
	
	private String updateUser;
	
	private String userId;
	
	/**
	 * 委托人
	 */
	private String delegate;
	
	/**
	 * 委托类型
	 * delegateType=0:委托，把任务直接叫给委托人来处理，自己不再处理
	 * delegateType=1:写作，把任务先交于委托人处理，处理完成后自己还要处理
	 */
	private Integer delegateType;
	
	/**
	 * 委托开始时间
	 */
	private Date startDate;
	
	/**
	 * 委托结束时间
	 */
	private Date endDate;
	
	/**
	 * 流程定义键值
	 */
	private String processKey;
	
	/**
	 * 委托状态，false：未启用，true：启用
	 */
	private Boolean delegateStatus;

	private String remark;
	
	
	public Integer getDelegateType() {
		return delegateType;
	}

	public void setDelegateType(Integer delegateType) {
		this.delegateType = delegateType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public Boolean getDelegateStatus() {
		return delegateStatus;
	}

	public void setDelegateStatus(Boolean delegateStatus) {
		this.delegateStatus = delegateStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDelegate() {
		return delegate;
	}

	public void setDelegate(String delegate) {
		this.delegate = delegate;
	}
}
