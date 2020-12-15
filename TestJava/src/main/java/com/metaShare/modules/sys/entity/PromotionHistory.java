package com.metaShare.modules.sys.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class PromotionHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	public PromotionHistory() {
	}

	private Integer id;
	private Integer createBy; // 创建者
	private Timestamp createDate; // 创建时间
	private Integer updateBy; // 更新者
	private Timestamp updateDate; // 更新时间
	private Integer UserId; // 员工id
	private String promotionType; // 晋档类型
	private Integer isPromotion; // 是否晋档
	private Timestamp promotionTime; // 晋档时间
	private Integer outAllowanceIdBefore; // 晋档前岗位id
	private Integer outAllowanceIdAfter; // 晋档后岗位id
	private Double seaAllowanceBefore; // 晋档前海龄补贴
	private Double seaAllowanceAfter; // 晋档后海龄补贴
	private Integer remarkId; // 备注

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getUserId() {
		return UserId;
	}

	public void setUserId(Integer userId) {
		UserId = userId;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}

	public Integer getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(Integer isPromotion) {
		this.isPromotion = isPromotion;
	}

	public Timestamp getPromotionTime() {
		return promotionTime;
	}

	public void setPromotionTime(Timestamp promotionTime) {
		this.promotionTime = promotionTime;
	}

	public Integer getOutAllowanceIdBefore() {
		return outAllowanceIdBefore;
	}

	public void setOutAllowanceIdBefore(Integer outAllowanceIdBefore) {
		this.outAllowanceIdBefore = outAllowanceIdBefore;
	}

	public Integer getOutAllowanceIdAfter() {
		return outAllowanceIdAfter;
	}

	public void setOutAllowanceIdAfter(Integer outAllowanceIdAfter) {
		this.outAllowanceIdAfter = outAllowanceIdAfter;
	}

	public Double getSeaAllowanceBefore() {
		return seaAllowanceBefore;
	}

	public void setSeaAllowanceBefore(Double seaAllowanceBefore) {
		this.seaAllowanceBefore = seaAllowanceBefore;
	}

	public Double getSeaAllowanceAfter() {
		return seaAllowanceAfter;
	}

	public void setSeaAllowanceAfter(Double seaAllowanceAfter) {
		this.seaAllowanceAfter = seaAllowanceAfter;
	}

	public Integer getRemarkId() {
		return remarkId;
	}

	public void setRemarkId(Integer remarkId) {
		this.remarkId = remarkId;
	}
}
