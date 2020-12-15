package com.metaShare.modules.generate.entity;
import java.math.BigDecimal;import com.baomidou.mybatisplus.annotations.TableField;import com.fasterxml.jackson.annotation.JsonProperty;import java.util.List;import com.metaShare.modules.core.entity.CustomBaseEntity;
public class GoodsLabels extends CustomBaseEntity {
	private String Name;
	private String CreateUser;
	private String LabelType;
	
	@JsonProperty("Name")
	public String getName() {
	    return Name;
	}
	
	public void setName(String Name)
	{
	    this.Name = Name;
	}
	
	@JsonProperty("CreateUser")
	public String getCreateUser() {
	    return CreateUser;
	}
	
	public void setCreateUser(String CreateUser)
	{
	    this.CreateUser = CreateUser;
	}
	
	@JsonProperty("LabelType")
	public String getLabelType() {
	    return LabelType;
	}
	
	public void setLabelType(String LabelType)
	{
	    this.LabelType = LabelType;
	}
	
}
