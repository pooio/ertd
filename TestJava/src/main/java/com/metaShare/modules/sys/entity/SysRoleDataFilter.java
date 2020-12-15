package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;
@TableName("sys_role_data_filter")
public class SysRoleDataFilter extends CustomBaseEntity {
	private static final long serialVersionUID = -4303802283076825649L;
	
	private String roleId;
	private String dataFilterId;
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getDataFilterId() {
		return dataFilterId;
	}
	public void setDataFilterId(String dataFilterId) {
		this.dataFilterId = dataFilterId;
	}
}
