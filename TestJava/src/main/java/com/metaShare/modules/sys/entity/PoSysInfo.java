package com.metaShare.modules.sys.entity;

import com.metaShare.modules.core.entity.CustomBaseEntity;

import java.util.Date;

public class PoSysInfo extends CustomBaseEntity {
    private String module;
    private String info;
    private Date createTime;
    private String operator;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
