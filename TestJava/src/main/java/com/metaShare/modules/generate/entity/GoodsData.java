package com.metaShare.modules.generate.entity;
import java.math.BigDecimal;import com.baomidou.mybatisplus.annotations.TableField;import com.fasterxml.jackson.annotation.JsonProperty;import java.util.List;import com.metaShare.modules.core.entity.CustomBaseEntity;
public class GoodsData extends CustomBaseEntity {
	private String Name;
	private String GoodsId;
	private String GoodsName;
	private int LikeQuantity;
	private int CollectionQuantity;
	private int SaleQuantity;
	private int ViewQuantity;
	
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
	
	@JsonProperty("LikeQuantity")
	public int getLikeQuantity() {
	    return LikeQuantity;
	}
	
	public void setLikeQuantity(int LikeQuantity)
	{
	    this.LikeQuantity = LikeQuantity;
	}
	
	@JsonProperty("CollectionQuantity")
	public int getCollectionQuantity() {
	    return CollectionQuantity;
	}
	
	public void setCollectionQuantity(int CollectionQuantity)
	{
	    this.CollectionQuantity = CollectionQuantity;
	}
	
	@JsonProperty("SaleQuantity")
	public int getSaleQuantity() {
	    return SaleQuantity;
	}
	
	public void setSaleQuantity(int SaleQuantity)
	{
	    this.SaleQuantity = SaleQuantity;
	}
	
	@JsonProperty("ViewQuantity")
	public int getViewQuantity() {
	    return ViewQuantity;
	}
	
	public void setViewQuantity(int ViewQuantity)
	{
	    this.ViewQuantity = ViewQuantity;
	}
	
}
