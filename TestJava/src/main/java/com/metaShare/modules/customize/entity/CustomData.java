package com.metaShare.modules.customize.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.metaShare.modules.core.entity.CustomBaseEntity;

public class CustomData  extends CustomBaseEntity {

    private String customFormId;

    private String customDataId;

    private String customData;

    private String customVersionId;

    public String getCustomVersionId() {
        return customVersionId;
    }

    public void setCustomVersionId(String customVersionId) {
        this.customVersionId = customVersionId;
    }

    public String getCustomDataId() {
        return customDataId;
    }

    public void setCustomDataId(String customDataId) {
        this.customDataId = customDataId;
    }

    public String getCustomFormId() {
        return customFormId;
    }

    public void setCustomFormId(String customFormId) {
        this.customFormId = customFormId;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }
}
