package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;

import java.util.Date;

@TableName("sys_dict")
public class SysDict extends CustomBaseEntity {
	private static final long serialVersionUID = -163750341249891988L;
	private String dictCode;
	private String dictName;
	private String dictDesc;
	private Boolean showOrNot;
	private Long sort;
	private String typeId;
	private Date createTime;
	
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
}
