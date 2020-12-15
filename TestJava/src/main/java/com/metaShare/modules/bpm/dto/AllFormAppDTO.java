package com.metaShare.modules.bpm.dto;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 所有申请任务(新增加表单请改写视图：view_findmyapp)
 * @author Ade.tong
 *
 */
@Entity
public class AllFormAppDTO  implements Serializable {
	//业务ID
	@Id
	private String businessKey;
	
	//申请人ID
	private String businessApp;
	
	//申请人姓名
	private String businessAppName;
	
	//业务名称
	private String businessName;
	
	//申请单状态ID
	private String businessStatue;
	
	//申请状态名称
	private String businessStatueName;
	
	//流程标识符
	private String processCode;
	
	//流程名称
	private String processName;
	
	//流程实例ID
	private String processInstanceId;
	
	//当前所处节点
	private String currNode;
	
	//当前审批人ID
	private String currAppUser;
	
	//当前审批人姓名
	private String currAppUserName;
	
	//当前节点表单
	private String formUrl;
	
	//创建时间
	private  String createTime;

	//结束时间
	private String endTime;
	
	public AllFormAppDTO(String business_key,String business_app,String business_app_name,
			String business_name ,String business_statue,String business_statue_name,
			String process_code, String process_name, String process_instance_id,String curr_node,
			String curr_app_user, String curr_app_user_name, String form_url,String create_time,String end_time) {
		super();
		this.businessKey = business_key;
		this.businessApp = business_app;
		this.businessAppName = business_app_name;
		this.businessName = business_name;
		this.businessStatue = business_statue;
		this.businessStatueName = business_statue_name;
		this.processCode = process_code;
		this.processName = process_name;
		this.processInstanceId = process_instance_id;
		this.currNode = curr_node;
		this.currAppUser = curr_app_user;
		this.currAppUserName = curr_app_user_name;
		this.formUrl = form_url;
		this.createTime = create_time;
		this.endTime = end_time;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getBusinessApp() {
		return businessApp;
	}

	public void setBusinessApp(String businessApp) {
		this.businessApp = businessApp;
	}

	public String getBusinessAppName() {
		return businessAppName;
	}

	public void setBusinessAppName(String businessAppName) {
		this.businessAppName = businessAppName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessStatue() {
		return businessStatue;
	}

	public void setBusinessStatue(String businessStatue) {
		this.businessStatue = businessStatue;
	}

	public String getBusinessStatueName() {
		return businessStatueName;
	}

	public void setBusinessStatueName(String businessStatueName) {
		this.businessStatueName = businessStatueName;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getCurrNode() {
		return currNode;
	}

	public void setCurrNode(String currNode) {
		this.currNode = currNode;
	}

	public String getCurrAppUser() {
		return currAppUser;
	}

	public void setCurrAppUser(String currAppUser) {
		this.currAppUser = currAppUser;
	}

	public String getCurrAppUserName() {
		return currAppUserName;
	}

	public void setCurrAppUserName(String currAppUserName) {
		this.currAppUserName = currAppUserName;
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
