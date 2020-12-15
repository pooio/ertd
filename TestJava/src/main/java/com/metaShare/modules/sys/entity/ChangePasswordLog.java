package com.metaShare.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

public class ChangePasswordLog  implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date updateDate; // 修改时间
	private String after; // 修改后密码
	private String before; // 修改前密码
	private Integer userId; // 修改人
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
