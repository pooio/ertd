/**
 * 
 */
package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableName;

/**
 * @author pc
 *
 */
@TableName("sys_role_org")
public class SysRoleOrg {
	private int id;
	private String roleId;
	private String orgId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}
