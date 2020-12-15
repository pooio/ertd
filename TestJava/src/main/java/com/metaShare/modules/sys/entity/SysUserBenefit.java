package com.metaShare.modules.sys.entity;

import java.io.Serializable;

/**
 * 系统用户
 */
public class SysUserBenefit implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 月份
	private String month;
	// 境外SAP
	private String outCode;
	// 姓名
	private String name; 
	// 组织部门
	private String orgName;
	// 人事所在单位
	private String orgHr; 
	// 发薪单位
	private String orgSalary; 
	// 月效益奖海上
	private Double bonusBenefitSea;
	// 月效益奖陆地
	private Double bonusBenefitLand;
	// 效益奖补差
	private Double benefitTotal;
	// 预发效益奖
	private Double benefit;
	// 月奖金
	private Double bonusMonth;
	// 月效益奖
	private Double bonusBenefit;

	
	
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getOutCode() {
		return outCode;
	}

	public void setOutCode(String outCode) {
		this.outCode = outCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrgHr() {
		return orgHr;
	}

	public void setOrgHr(String orgHr) {
		this.orgHr = orgHr;
	}

	public String getOrgSalary() {
		return orgSalary;
	}

	public void setOrgSalary(String orgSalary) {
		this.orgSalary = orgSalary;
	}

	public Double getBonusBenefitSea() {
		return bonusBenefitSea;
	}

	public void setBonusBenefitSea(Double bonusBenefitSea) {
		this.bonusBenefitSea = bonusBenefitSea;
	}

	public Double getBonusBenefitLand() {
		return bonusBenefitLand;
	}

	public void setBonusBenefitLand(Double bonusBenefitLand) {
		this.bonusBenefitLand = bonusBenefitLand;
	}

	public Double getBenefitTotal() {
		return benefitTotal;
	}

	public void setBenefitTotal(Double benefitTotal) {
		this.benefitTotal = benefitTotal;
	}

	public Double getBenefit() {
		return benefit;
	}

	public void setBenefit(Double benefit) {
		this.benefit = benefit;
	}

	public Double getBonusMonth() {
		return bonusMonth;
	}

	public void setBonusMonth(Double bonusMonth) {
		this.bonusMonth = bonusMonth;
	}

	public Double getBonusBenefit() {
		return bonusBenefit;
	}

	public void setBonusBenefit(Double bonusBenefit) {
		this.bonusBenefit = bonusBenefit;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
