package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;

import java.util.Date;
import java.util.List;
@TableName("sys_organization")
public class SysOrganization extends CustomBaseEntity {
    private static final long serialVersionUID = 7195765094071320415L;
    private String orgName;
    private int inUse = 1;
    private Integer sort;
    private String remark;
    private String parentId;
    private String code;
    private String orgCode;
    private Date createTime;
//    private String orgShort;//部门简写(生成计划编号使用)
//    private String orgOrder;//部门顺序(生成计划编号使用)

    @TableField(exist = false)
    private List<SysOrganization> children;

    @TableField(exist = false)
    private String orgType;

    @TableField(exist = false)
    private int userSize;


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public List<SysOrganization> getChildren() {
        return children;
    }

    public void setChildren(List<SysOrganization> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    
    public int getInUse() {
		return inUse;
	}

	public void setInUse(int inUse) {
		this.inUse = inUse;
	}

	public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public int getUserSize() {
        return userSize;
    }

    public void setUserSize(int userSize) {
        this.userSize = userSize;
    }
    //    public String getOrgShort() {
//        return orgShort;
//    }
//
//    public void setOrgShort(String orgShort) {
//        this.orgShort = orgShort;
//    }
//
//    public String getOrgOrder() {
//        return orgOrder;
//    }
//
//    public void setOrgOrder(String orgOrder) {
//        this.orgOrder = orgOrder;
//    }
}
