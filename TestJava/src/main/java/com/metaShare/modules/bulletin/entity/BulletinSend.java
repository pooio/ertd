/**  
 * @Title: BulletinSend.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:43:08 
 */ 
package com.metaShare.modules.bulletin.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**  
 * @ClassName: BulletinSend
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:43:08 
*/
@TableName("bulletin_send")
public class BulletinSend {
	@TableId(type = IdType.AUTO)
	private int sendPk;
	private String userPk;
	private int bulletinPk;
	private String bulletinTitle;
	private int bulletinType;
	private int bulletinIsread;
	private int state;
	private String createPk;
	private String createTime;
	private String createName;
	public int getSendPk() {
		return sendPk;
	}
	public void setSendPk(int sendPk) {
		this.sendPk = sendPk;
	}
	public String getUserPk() {
		return userPk;
	}
	public void setUserPk(String userPk) {
		this.userPk = userPk;
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
	public int getBulletinType() {
		return bulletinType;
	}
	public void setBulletinType(int bulletinType) {
		this.bulletinType = bulletinType;
	}
	public int getBulletinIsread() {
		return bulletinIsread;
	}
	public void setBulletinIsread(int bulletinIsread) {
		this.bulletinIsread = bulletinIsread;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCreatePk() {
		return createPk;
	}
	public void setCreatePk(String createPk) {
		this.createPk = createPk;
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
	

}
