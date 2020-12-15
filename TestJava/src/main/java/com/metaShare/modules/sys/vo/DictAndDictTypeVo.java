package com.metaShare.modules.sys.vo;

import java.util.Date;

public class DictAndDictTypeVo {
	private static final long serialVersionUID = -163750341249891988L;
	private String dictCode;
	private String dictName;
	private String dictDesc;
	private Boolean showOrNot;
	private Long sort;
	private String typeId;
	private Date createTime;
	private String id;
	private Boolean deleted;
	private String typeName;
	public String getDictCode() {
		return dictCode;
	}
	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public String getDictDesc() {
		return dictDesc;
	}
	public void setDictDesc(String dictDesc) {
		this.dictDesc = dictDesc;
	}
	public Boolean getShowOrNot() {
		return showOrNot;
	}
	public void setShowOrNot(Boolean showOrNot) {
		this.showOrNot = showOrNot;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
