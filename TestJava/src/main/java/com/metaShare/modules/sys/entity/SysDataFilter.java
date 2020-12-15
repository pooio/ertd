package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;

import java.util.Date;

@TableName("sys_data_filter")
public class SysDataFilter extends CustomBaseEntity {
    private static final long serialVersionUID = -81378588434833793L;
    private String name;
    private String url;
    private String expression;
    private Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
