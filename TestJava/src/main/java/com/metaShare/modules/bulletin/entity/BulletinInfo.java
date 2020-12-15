/**  
 * @Title: BulletinInfo.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:42:20 
 */
package com.metaShare.modules.bulletin.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * @ClassName: BulletinInfo
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:42:20
 */
@TableName("bulletin_info")
public class BulletinInfo {
	@TableId(type = IdType.AUTO)
	private int bulletinPk;
	private String bulletinTitle;
	private String bulletinNote;
	private Integer bulletinType;
	private int receiveNo;
	private int readNo;
	private int state;
	private String closeDate;
	private String fileId;
	private String userPk;
	private String createTime;
	private String createName;
	private int publishState;
	private int bulletinMode;
	private String receiveInfo;
	@TableField(exist = false)
	private List<String> receiveInfos;

	@TableField(exist = false)
	private String fileName;

	@TableField(exist = false)
	private String url;
	@TableField(exist = false)
	private String sendPk;
	@TableField(exist = false)
	private String bulletinName;

	public String getBulletinName() {
		return bulletinName;
	}

	public void setBulletinName(String bulletinName) {
		this.bulletinName = bulletinName;
	}

	public String getSendPk() {
		return sendPk;
	}

	public void setSendPk(String sendPk) {
		this.sendPk = sendPk;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getReceiveInfos() {
		return receiveInfos;
	}

	public void setReceiveInfos(List<String> receiveInfos) {
		this.receiveInfos = receiveInfos;
	}

	public int getBulletinPk() {
		return bulletinPk;
	}

	public void setBulletinPk(int bulletinPk) {
		this.bulletinPk = bulletinPk;
	}

	public String getBulletinTitle() {
		return bulletinTitle;
	}

	public void setBulletinTitle(String bulletinTitle) {
		this.bulletinTitle = bulletinTitle;
	}

	public String getBulletinNote() {
		return bulletinNote;
	}

	public void setBulletinNote(String bulletinNote) {
		this.bulletinNote = bulletinNote;
	}

	public Integer getBulletinType() {
		return bulletinType;
	}

	public void setBulletinType(Integer bulletinType) {
		this.bulletinType = bulletinType;
	}

	public int getReceiveNo() {
		return receiveNo;
	}

	public void setReceiveNo(int receiveNo) {
		this.receiveNo = receiveNo;
	}

	public int getReadNo() {
		return readNo;
	}

	public void setReadNo(int readNo) {
		this.readNo = readNo;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getUserPk() {
		return userPk;
	}

	public void setUserPk(String userPk) {
		this.userPk = userPk;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public int getPublishState() {
		return publishState;
	}

	public void setPublishState(int publishState) {
		this.publishState = publishState;
	}

	public int getBulletinMode() {
		return bulletinMode;
	}

	public void setBulletinMode(int bulletinMode) {
		this.bulletinMode = bulletinMode;
	}

	public String getReceiveInfo() {
		return receiveInfo;
	}

	public void setReceiveInfo(String receiveInfo) {
		this.receiveInfo = receiveInfo;
	}

}
