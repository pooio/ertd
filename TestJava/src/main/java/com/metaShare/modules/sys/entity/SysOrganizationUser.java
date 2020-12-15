package com.metaShare.modules.sys.entity;

import java.math.BigDecimal;
import java.math.BigInteger;


public class SysOrganizationUser{
	private static final long serialVersionUID = 7195765094071320415L;
	
	
	public SysOrganizationUser(String id, String orgName, String parentId, String treeLevel, BigDecimal sort ,String type) {
		this.id = id;
		this.orgName = orgName;
		this.parentId = parentId;
		this.treeLevel = treeLevel;
		this.sort = sort;
		this.type = type;
	}
	
	protected String id;
	private String orgName;
	private String parentId;
	private String treeLevel;
	private BigDecimal sort;
	private String type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getTreeLevel() {
		return treeLevel;
	}
	public void setTreeLevel(String treeLevel) {
		this.treeLevel = treeLevel;
	}
	public BigDecimal getSort() {
		return sort;
	}
	public void setSort(BigDecimal sort) {
		this.sort = sort;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
		
}
