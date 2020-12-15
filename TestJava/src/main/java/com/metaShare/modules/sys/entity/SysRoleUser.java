package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;
@TableName("sys_role_user")
public class SysRoleUser extends CustomBaseEntity {
	private static final long serialVersionUID = 8314866721374280069L;
	
	private String userId;
	private String roleId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
}
