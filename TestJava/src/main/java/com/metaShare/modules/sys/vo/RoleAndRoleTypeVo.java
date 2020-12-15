package com.metaShare.modules.sys.vo;

import java.util.Date;

public class RoleAndRoleTypeVo {
	private static final long serialVersionUID = -163750341249891988L;
	private String id;
	private String roleName;
	private String roleCode;
	private Long roleLevel;
	private String remark;
	private Date createTime;
	private String roleTypeId;
	private String typeName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public Long getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(Long roleLevel) {
		this.roleLevel = roleLevel;
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
	public String getRoleTypeId() {
		return roleTypeId;
	}
	public void setRoleTypeId(String roleTypeId) {
		this.roleTypeId = roleTypeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
