package com.metaShare.modules.bpm.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metaShare.common.utils.EntityChangeDto;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.vo.UserProcessVo;

public class BpmDelegateDTO  {
	private static final long serialVersionUID = 1L;

	private String id;
	private UserProcessVo user;
	private UserProcessVo delegate;
	private Integer delegateType;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endDate;
	private String processKey;
	private Boolean delegateStatus;
	private String remark;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public UserProcessVo getUser() {
		return user;
	}
	public void setUser(SysUser user) {
		try {
			this.user = EntityChangeDto.objectToOtherObject(user, new UserProcessVo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public UserProcessVo getDelegate() {
		return delegate;
	}
	public void setDelegate(SysUser delegate) {
		try {
			this.delegate = EntityChangeDto.objectToOtherObject(delegate, new UserProcessVo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
}
