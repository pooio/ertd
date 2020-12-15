package com.metaShare.modules.generate.entity;
import java.math.BigDecimal;import com.baomidou.mybatisplus.annotations.TableField;import com.fasterxml.jackson.annotation.JsonProperty;import java.util.List;import com.metaShare.modules.core.entity.CustomBaseEntity;
public class Goods extends CustomBaseEntity {
	private String Name;
	private String GoodsType;
	@TableField(exist = false)
	private com.metaShare.modules.generate.entity.GoodsData Datas;
	private String CreateDate;
	private String Status;
	private BigDecimal Price;
	
	@JsonProperty("Name")
	public String getName() {
	    return Name;
	}
	
	public void setName(String Name)
	{
	    this.Name = Name;
	}
	
	@JsonProperty("GoodsType")
	public String getGoodsType() {
	    return GoodsType;
	}
	
	public void setGoodsType(String GoodsType)
	{
	    this.GoodsType = GoodsType;
	}
	
	@JsonProperty("Datas")
	public com.metaShare.modules.generate.entity.GoodsData getDatas() {
	    return Datas;
	}
	
	public void setDatas(com.metaShare.modules.generate.entity.GoodsData Datas)
	{
	    this.Datas = Datas;
	}
	
	@JsonProperty("CreateDate")
	public String getCreateDate() {
	    return CreateDate;
	}
	
	public void setCreateDate(String CreateDate)
	{
	    this.CreateDate = CreateDate;
	}
	
	@JsonProperty("Status")
	public String getStatus() {
	    return Status;
	}
	
	public void setStatus(String Status)
	{
	    this.Status = Status;
	}
	
	@JsonProperty("Price")
	public BigDecimal getPrice() {
	    return Price;
	}
	
	public void setPrice(BigDecimal Price)
	{
	    this.Price = Price;
	}
	
}
