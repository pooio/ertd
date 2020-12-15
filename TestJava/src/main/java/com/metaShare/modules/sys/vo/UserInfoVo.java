package com.metaShare.modules.sys.vo;

import java.util.Date;
import java.util.List;

import com.metaShare.modules.sys.entity.SysUser;

/**
 * 查询用户相关信息
 */
public class UserInfoVo {
	private static final long serialVersionUID = -1019266474860401390L;

	private String id;
	private String name;
	private String email;
	private String phoneNumber;
	private String orgName;
	private String sexName;
	private String orgId;
	private List<String> roleName;
	private List<String> postName;
	//用户状态
	private String sysCommState;
	private Date joinDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<String> getRoleName() {
		return roleName;
	}

	public void setRoleName(List<String> roleName) {
		this.roleName = roleName;
	}

	public String getSysCommState() {
		return sysCommState;
	}

	public void setSysCommState(String sysCommState) {
		this.sysCommState = sysCommState;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	
	public List<String> getPostName() {
		return postName;
	}

	public void setPostName(List<String> postName) {
		this.postName = postName;
	}

	/**
	 * 用户对象转为VO
	 * @param user
	 * @return
	 */
	public UserInfoVo changeEntity(SysUser user) {
		UserInfoVo vo = new UserInfoVo();
		vo.setId(user.getId());
		vo.setName(user.getName());
		vo.setEmail(user.getEmail());
		vo.setPhoneNumber(user.getPhoneNumber());
		vo.setOrgName(user.getOrgName());
		Integer sex = user.getSex();
		String sexName = "";
		if (sex == 1) {
			sexName = "男";
		}else if (sex == 0) {
			sexName = "女";
		}
		vo.setSexName(sexName);
		vo.setOrgId(user.getOrgId());
		return vo;
	}
}
