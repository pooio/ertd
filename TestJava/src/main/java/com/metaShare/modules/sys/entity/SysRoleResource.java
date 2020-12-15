package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;
@TableName("sys_role_resource")
public class SysRoleResource extends CustomBaseEntity {
	private static final long serialVersionUID = 5695876747093127049L;
	
	private String roleId;
	private String resourceId;
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
