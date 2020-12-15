/**   
* @Title: SDicinfo.java 
* @Package com.metaShare.modules.sys.entity 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2020年1月7日 下午2:03:50 
* @version V1.0   
*/
package com.metaShare.modules.sys.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.dom4j.tree.AbstractEntity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * 
 * @ClassName: SDicinfo
 * @Description: 数据字典
 * @author eric.xi
 * @date 2020年1月7日 下午2:05:40
 *
 */
@TableName("sys_dicinfo")
public class SysDicinfo{
	private int id;

	private String dicName;

	private String dicCode;

	private String dicDesc;

	private int parentId;

	private int sortNo;

	private int inUse= 1;

	private Date createTime;
	
	public Boolean deleted = Boolean.FALSE;
	
	@TableField(exist=false)
	private List<SysDicinfo> children =new ArrayList<>();

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<SysDicinfo> getChildren() {
		return children;
	}

	public void setChildren(List<SysDicinfo> children) {
		this.children = children;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDicName() {
		return dicName;
	}

	public void setDicName(String dicName) {
		this.dicName = dicName;
	}

	public String getDicCode() {
		return dicCode;
	}

	public void setDicCode(String dicCode) {
		this.dicCode = dicCode;
	}

	public String getDicDesc() {
		return dicDesc;
	}

	public void setDicDesc(String dicDesc) {
		this.dicDesc = dicDesc;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getSortNo() {
		return sortNo;
	}

	public void setSortNo(int sortNo) {
		this.sortNo = sortNo;
	}

	public int getInUse() {
		return inUse;
	}

	public void setInUse(int inUse) {
		this.inUse = inUse;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	 

}

