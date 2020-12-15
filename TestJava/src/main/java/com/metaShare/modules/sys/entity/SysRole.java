package com.metaShare.modules.sys.entity;


import java.util.Date;
import com.metaShare.modules.core.entity.CustomBaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("sys_role")
public class SysRole extends CustomBaseEntity{
	private static final long serialVersionUID = 6453746400950087672L;

	private String roleName;
	private String roleCode;
	private String remark;
	private int inUse =1 ;
	private int roleType;
	//暂无
	private Date createTime;
	private String typeId;

	public int getInUse() {
		return inUse;
	}

	public void setInUse(int inUse) {
		this.inUse = inUse;
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

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}
	 
}
