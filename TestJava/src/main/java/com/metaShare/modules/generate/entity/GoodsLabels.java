package com.metaShare.modules.generate.entity;

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