/**  
 * @Title: BulletinReadItem.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:42:52 
 */ 
package com.metaShare.modules.bulletin.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**  
 * @ClassName: BulletinReadItem
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:42:52 
*/
@TableName("bulletin_read_item")
public class BulletinReadItem {
	@TableId(type = IdType.AUTO)
	private int itemPk;
	private int bulletinPk;
	private int sendPk;
	private int state;
	private String userPk;
	private String createTime;
	private String createName;
	public int getItemPk() {
		return itemPk;
	}
	public void setItemPk(int itemPk) {
		this.itemPk = itemPk;
	}
	public int getBulletinPk() {
		return bulletinPk;
	}
	public void setBulletinPk(int bulletinPk) {
		this.bulletinPk = bulletinPk;
	}
	public int getSendPk() {
		return sendPk;
	}
	public void setSendPk(int sendPk) {
		this.sendPk = sendPk;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
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

}
