package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * 组织机构
 * @author Ling
 * @email qinjinlingx@gmail.com
 * @date 2018/4/19 20:44
 */
@TableName("sys_org")
public class SysOrg implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer parentId;
    private String orgName;
    private Integer sort;
    private Integer deleteOrNot;

    
    public Integer getDeleteOrNot() {
		return deleteOrNot;
	}

	public void setDeleteOrNot(Integer deleteOrNot) {
		this.deleteOrNot = deleteOrNot;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
