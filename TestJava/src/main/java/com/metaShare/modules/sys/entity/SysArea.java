/**  
 * @Title: sys_area.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 09:51:42 
 */ 
package com.metaShare.modules.sys.entity;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

/**  
 * @ClassName: sys_area
 * @Description: 区域管理
 * @author eric.xi
 * @date 2020-02-13 09:51:42 
*/
@TableName("sys_area")
public class SysArea {
	private int id;
	private String areaCode;
	private String areaName;
	private String parentCode;
	private String areaType;
	@TableField(exist=false)
	private List<SysArea> children =new ArrayList<>();
	
	public List<SysArea> getChildren() {
		return children;
	}
	public void setChildren(List<SysArea> children) {
		this.children = children;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getAreaType() {
		return areaType;
	}
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}
	

}
