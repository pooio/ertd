package com.metaShare.modules.sys.vo;

import java.util.Date;
import java.util.List;

public class UserProcessVo {
	private static final long serialVersionUID = -1019266474860401390L;
	private String id;
	private String loginName;
	private String name;
	private String email;
	private Integer status;
	private String phoneNumber;
	private String orgId;
	private String orgName;
	private Integer sex;
	private String userNo;
	private Date joinDate;
	private String sexName;
	private List<String> roleCodes;
	private String sysCommState;
	// 八个部门+四个单位
	private String topOrgName;
	private String topOrgId;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public List<String> getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(List<String> roleCodes) {
		this.roleCodes = roleCodes;
	}

	public String getSysCommState() {
		return sysCommState;
	}

	public void setSysCommState(String sysCommState) {
		this.sysCommState = sysCommState;
	}

	public String getTopOrgName() {
		return topOrgName;
	}

	public void setTopOrgName(String topOrgName) {
		this.topOrgName = topOrgName;
	}

	public String getTopOrgId() {
		return topOrgId;
	}

	public void setTopOrgId(String topOrgId) {
		this.topOrgId = topOrgId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
