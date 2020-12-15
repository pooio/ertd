package com.metaShare.modules.sys.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;
@TableName("sys_permission")
public class SysPermission extends CustomBaseEntity{
	private static final long serialVersionUID = 8691685361720012997L;
	private String permissionStr;
	private String permissionDesc;
	private Date createTime;
	
	public String getPermissionStr() {
		return permissionStr;
	}
	public void setPermissionStr(String permissionStr) {
		this.permissionStr = permissionStr;
	}
	public String getPermissionDesc() {
		return permissionDesc;
	}
	public void setPermissionDesc(String permissionDesc) {
		this.permissionDesc = permissionDesc;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
