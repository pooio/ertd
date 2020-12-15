package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableName;

/**
 * 角色与菜单对应关系
 */
@TableName("sys_role_menu")
public class SysRoleMenu {

	private Integer roleId;
	private Integer menuId;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
}
