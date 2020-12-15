package com.metaShare.modules.sys.entity;

import java.io.Serializable;

/**
 * 系统用户配置表
 */
public class SysUserConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	public SysUserConfig() {
	}
	
	private Integer id;//系统用户配置表标识
	private Integer userId;//系统用户
	private String outJobType; //境外岗序
	private String outJobName; //境外工作岗位
	private Integer outAllowanceId;//境外工资标准ID
	private String inJobType; //境内岗序
	private Integer inAllowanceId;//境内工资标准ID
	private Integer benefitId;//效益岗级标准ID
	private double benefitRate;//岗位效益工资系数
	private Integer deepseaAllowanceId;//深海津贴标准ID
	private Integer agentAllowanceId;//代理奖金标准ID
	private Integer techAllowanceId;//技术津贴标准ID
	private Integer techAllowanceInId;//代理奖金标准ID
	private Integer skillAllowanceId;//技能津贴标准ID
	private Integer teamAllowanceId;//职业队经理津贴标准ID
	private Integer localWorkingAllowanceId;//现场津贴标准
	private double inPercent;//境内工资系数
	private double seaageAllowance;//海龄补贴
	private double workWildAllowance;//野外工作年限补贴
	private double housingReformAllowance;//房改补贴
	private double bonusYear;//年奖金标准
	private double bonusYearRatio;//年奖金系数
	private double bonusMonth;//月奖金标准
	private double bonusMonthRatio;//月奖金系数
	private double langPct;//外语未达标扣除百分比
	private double isuEndowmentBase;//养老保险基数
	private double isuEndowmentDedu;//养老保险扣缴额
	private String isuEndowmentPlace; //养老保险所在地
	private double isuMedicalBase;//医疗保险基数
	private double isuMedicalDedu;//医疗保险扣缴额
	private String isuMedicalPlace; //医疗保险所在地
	private double isuUnemploymentBase;//失业保险基数
	private double isuUnemploymentDedu;//失业保险扣缴额
	private String isuUnemploymentPlace; //失业保险所在地
	private double houseFundBase;//公积金基数
	private double houseFundDedu;//公积金扣缴额
	private String houseFundPlace; //公积金所在地
	private double annuityBase;//年金基数
	private double annuityDedu;//年金扣缴额
	private double annuityBalance;//年金截至当月补缴差额
	private double illnessDedu; //大病医疗扣缴额
	private String illnessPlace; 	//大病医疗所在地
	private double retainedEarning;//保留效益工资
	private double reservationWage1;//保留工资1
	private double reservationWage2;//保留工资2
	private double overtimeMealAllowance;//误餐费
	private Double gradeBc; // 岗位工资（档补）
	private Double gradeBcStandard; // 岗位工资（档补）
    private Double seniorityAllowance; // 工龄津贴

	public double getBenefitRate() {
		return benefitRate;
	}

	public void setBenefitRate(double benefitRate) {
		this.benefitRate = benefitRate;
	}

	public Double getSeniorityAllowance() {
		return seniorityAllowance;
	}

	public void setSeniorityAllowance(Double seniorityAllowance) {
		this.seniorityAllowance = seniorityAllowance;
	}
	
	public Double getGradeBcStandard() {
		return gradeBcStandard;
	}
	public void setGradeBcStandard(Double gradeBcStandard) {
		this.gradeBcStandard = gradeBcStandard;
	}
	public Double getGradeBc() {
		return gradeBc;
	}
	public void setGradeBc(Double gradeBc) {
		this.gradeBc = gradeBc;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getOutJobType() {
		return outJobType;
	}
	public void setOutJobType(String outJobType) {
		this.outJobType = outJobType;
	}
	public String getOutJobName() {
		return outJobName;
	}
	public void setOutJobName(String outJobName) {
		this.outJobName = outJobName;
	}
	public Integer getOutAllowanceId() {
		return outAllowanceId;
	}
	public void setOutAllowanceId(Integer outAllowanceId) {
		this.outAllowanceId = outAllowanceId;
	}
	public String getInJobType() {
		return inJobType;
	}
	public void setInJobType(String inJobType) {
		this.inJobType = inJobType;
	}
	public Integer getInAllowanceId() {
		return inAllowanceId;
	}
	public void setInAllowanceId(Integer inAllowanceId) {
		this.inAllowanceId = inAllowanceId;
	}
	public Integer getBenefitId() {
		return benefitId;
	}
	public void setBenefitId(Integer benefitId) {
		this.benefitId = benefitId;
	}
	public Integer getDeepseaAllowanceId() {
		return deepseaAllowanceId;
	}
	public void setDeepseaAllowanceId(Integer deepseaAllowanceId) {
		this.deepseaAllowanceId = deepseaAllowanceId;
	}
	public Integer getAgentAllowanceId() {
		return agentAllowanceId;
	}
	public void setAgentAllowanceId(Integer agentAllowanceId) {
		this.agentAllowanceId = agentAllowanceId;
	}
	public Integer getTechAllowanceId() {
		return techAllowanceId;
	}
	public void setTechAllowanceId(Integer techAllowanceId) {
		this.techAllowanceId = techAllowanceId;
	}
	public Integer getTechAllowanceInId() {
		return techAllowanceInId;
	}
	public void setTechAllowanceInId(Integer techAllowanceInId) {
		this.techAllowanceInId = techAllowanceInId;
	}
	public Integer getSkillAllowanceId() {
		return skillAllowanceId;
	}
	public void setSkillAllowanceId(Integer skillAllowanceId) {
		this.skillAllowanceId = skillAllowanceId;
	}
	public Integer getTeamAllowanceId() {
		return teamAllowanceId;
	}
	public void setTeamAllowanceId(Integer teamAllowanceId) {
		this.teamAllowanceId = teamAllowanceId;
	}
	public Integer getLocalWorkingAllowanceId() {
		return localWorkingAllowanceId;
	}
	public void setLocalWorkingAllowanceId(Integer localWorkingAllowanceId) {
		this.localWorkingAllowanceId = localWorkingAllowanceId;
	}
	public double getInPercent() {
		return inPercent;
	}
	public void setInPercent(double inPercent) {
		this.inPercent = inPercent;
	}
	public double getSeaageAllowance() {
		return seaageAllowance;
	}
	public void setSeaageAllowance(double seaageAllowance) {
		this.seaageAllowance = seaageAllowance;
	}
	public double getWorkWildAllowance() {
		return workWildAllowance;
	}
	public void setWorkWildAllowance(double workWildAllowance) {
		this.workWildAllowance = workWildAllowance;
	}
	public double getHousingReformAllowance() {
		return housingReformAllowance;
	}
	public void setHousingReformAllowance(double housingReformAllowance) {
		this.housingReformAllowance = housingReformAllowance;
	}
	public double getBonusYear() {
		return bonusYear;
	}
	public void setBonusYear(double bonusYear) {
		this.bonusYear = bonusYear;
	}
	public double getBonusYearRatio() {
		return bonusYearRatio;
	}
	public void setBonusYearRatio(double bonusYearRatio) {
		this.bonusYearRatio = bonusYearRatio;
	}
	public double getBonusMonth() {
		return bonusMonth;
	}
	public void setBonusMonth(double bonusMonth) {
		this.bonusMonth = bonusMonth;
	}
	public double getBonusMonthRatio() {
		return bonusMonthRatio;
	}
	public void setBonusMonthRatio(double bonusMonthRatio) {
		this.bonusMonthRatio = bonusMonthRatio;
	}
	public double getLangPct() {
		return langPct;
	}
	public void setLangPct(double langPct) {
		this.langPct = langPct;
	}
	public double getIsuEndowmentBase() {
		return isuEndowmentBase;
	}
	public void setIsuEndowmentBase(double isuEndowmentBase) {
		this.isuEndowmentBase = isuEndowmentBase;
	}
	public double getIsuEndowmentDedu() {
		return isuEndowmentDedu;
	}
	public void setIsuEndowmentDedu(double isuEndowmentDedu) {
		this.isuEndowmentDedu = isuEndowmentDedu;
	}
	public String getIsuEndowmentPlace() {
		return isuEndowmentPlace;
	}
	public void setIsuEndowmentPlace(String isuEndowmentPlace) {
		this.isuEndowmentPlace = isuEndowmentPlace;
	}
	public double getIsuMedicalBase() {
		return isuMedicalBase;
	}
	public void setIsuMedicalBase(double isuMedicalBase) {
		this.isuMedicalBase = isuMedicalBase;
	}
	public double getIsuMedicalDedu() {
		return isuMedicalDedu;
	}
	public void setIsuMedicalDedu(double isuMedicalDedu) {
		this.isuMedicalDedu = isuMedicalDedu;
	}
	public String getIsuMedicalPlace() {
		return isuMedicalPlace;
	}
	public void setIsuMedicalPlace(String isuMedicalPlace) {
		this.isuMedicalPlace = isuMedicalPlace;
	}
	public double getIsuUnemploymentBase() {
		return isuUnemploymentBase;
	}
	public void setIsuUnemploymentBase(double isuUnemploymentBase) {
		this.isuUnemploymentBase = isuUnemploymentBase;
	}
	public double getIsuUnemploymentDedu() {
		return isuUnemploymentDedu;
	}
	public void setIsuUnemploymentDedu(double isuUnemploymentDedu) {
		this.isuUnemploymentDedu = isuUnemploymentDedu;
	}
	public String getIsuUnemploymentPlace() {
		return isuUnemploymentPlace;
	}
	public void setIsuUnemploymentPlace(String isuUnemploymentPlace) {
		this.isuUnemploymentPlace = isuUnemploymentPlace;
	}
	public double getHouseFundBase() {
		return houseFundBase;
	}
	public void setHouseFundBase(double houseFundBase) {
		this.houseFundBase = houseFundBase;
	}
	public double getHouseFundDedu() {
		return houseFundDedu;
	}
	public void setHouseFundDedu(double houseFundDedu) {
		this.houseFundDedu = houseFundDedu;
	}
	public String getHouseFundPlace() {
		return houseFundPlace;
	}
	public void setHouseFundPlace(String houseFundPlace) {
		this.houseFundPlace = houseFundPlace;
	}
	public double getAnnuityBase() {
		return annuityBase;
	}
	public void setAnnuityBase(double annuityBase) {
		this.annuityBase = annuityBase;
	}
	public double getAnnuityDedu() {
		return annuityDedu;
	}
	public void setAnnuityDedu(double annuityDedu) {
		this.annuityDedu = annuityDedu;
	}
	public double getAnnuityBalance() {
		return annuityBalance;
	}
	public void setAnnuityBalance(double annuityBalance) {
		this.annuityBalance = annuityBalance;
	}
	public double getIllnessDedu() {
		return illnessDedu;
	}
	public void setIllnessDedu(double illnessDedu) {
		this.illnessDedu = illnessDedu;
	}
	public String getIllnessPlace() {
		return illnessPlace;
	}
	public void setIllnessPlace(String illnessPlace) {
		this.illnessPlace = illnessPlace;
	}
	public double getRetainedEarning() {
		return retainedEarning;
	}
	public void setRetainedEarning(double retainedEarning) {
		this.retainedEarning = retainedEarning;
	}
	public double getReservationWage1() {
		return reservationWage1;
	}
	public void setReservationWage1(double reservationWage1) {
		this.reservationWage1 = reservationWage1;
	}
	public double getReservationWage2() {
		return reservationWage2;
	}
	public void setReservationWage2(double reservationWage2) {
		this.reservationWage2 = reservationWage2;
	}
	public double getOvertimeMealAllowance() {
		return overtimeMealAllowance;
	}
	public void setOvertimeMealAllowance(double overtimeMealAllowance) {
		this.overtimeMealAllowance = overtimeMealAllowance;
	}


}
