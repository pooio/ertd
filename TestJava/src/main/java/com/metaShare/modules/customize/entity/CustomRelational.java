package com.metaShare.modules.customize.entity;

import com.metaShare.modules.core.entity.CustomBaseEntity;

public class CustomRelational extends CustomBaseEntity {

    private String parentId; //父表单ID

    private String childrenId;//子表单ID

    private String createTime;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(String childrenId) {
        this.childrenId = childrenId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
