package com.metaShare.modules.sys.entity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;
import java.util.Date;
@TableName("sys_dict_type")
public class SysDictType extends CustomBaseEntity {
	private String typeCode;
	private String typeName;
	private String typeDesc;
	private Date createTime;
	
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
