package com.metaShare.modules.generate.entity;
import java.math.BigDecimal;import com.baomidou.mybatisplus.annotations.TableField;import com.fasterxml.jackson.annotation.JsonProperty;import java.util.List;import com.metaShare.modules.core.entity.CustomBaseEntity;
public class GoodsLabel extends CustomBaseEntity {
	private String Name;
	private String GoodsId;
	private String GoodsName;
	private String LabelsId;
	private String LabelsName;
	
	@JsonProperty("Name")
	public String getName() {
	    return Name;
	}
	
	public void setName(String Name)
	{
	    this.Name = Name;
	}
	
	@JsonProperty("GoodsId")
	public String getGoodsId() {
	    return GoodsId;
	}
	
	public void setGoodsId(String GoodsId)
	{
	    this.GoodsId = GoodsId;
	}
	
	@JsonProperty("GoodsName")
	public String getGoodsName() {
	    return GoodsName;
	}
	
	public void setGoodsName(String GoodsName)
	{
	    this.GoodsName = GoodsName;
	}
	
	@JsonProperty("LabelsId")
	public String getLabelsId() {
	    return LabelsId;
	}
	
	public void setLabelsId(String LabelsId)
	{
	    this.LabelsId = LabelsId;
	}
	
	@JsonProperty("LabelsName")
	public String getLabelsName() {
	    return LabelsName;
	}
	
	public void setLabelsName(String LabelsName)
	{
	    this.LabelsName = LabelsName;
	}
	
}
