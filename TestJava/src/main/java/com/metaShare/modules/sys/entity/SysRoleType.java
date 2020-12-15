package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;

import java.util.List;
@TableName("sys_roletype")
public class SysRoleType extends CustomBaseEntity{
	private static final long serialVersionUID = 7661144581282444554L;

	private String typeName;
	private Integer sort;
	private Integer treeLevel;
	private String remark;
	private String parentId;

	@TableField(exist = false)
	private List<SysRoleType> children;

	public List<SysRoleType> getChildren() {
		return children;
	}

	public void setChildren(List<SysRoleType> children) {
		this.children = children;
	}

	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public Integer getTreeLevel() {
		return treeLevel;
	}
	public void setTreeLevel(Integer treeLevel) {
		this.treeLevel = treeLevel;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
