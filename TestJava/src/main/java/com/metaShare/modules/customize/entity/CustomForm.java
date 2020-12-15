package com.metaShare.modules.customize.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.metaShare.modules.core.entity.CustomBaseEntity;

public class CustomForm  extends CustomBaseEntity {

    private String formIdentification;//表单标识

    private String formJson;//自定义表单Json数据

    private String businessName;//业务名称

    private Integer postStatus;//发布状态 0为未发布 1为已发布   发布需走审批

    private String dataTableName;//数据库表名

    private Integer approvalStatus;//审批状态 0为待审批 1为审批中 2为审批通过 99为审批不通过

    private String description;//描述

    private String icon;//图标

    private Integer isCreateTable;//通过该字段判断数据库表是否已创建 0为未创建 1为已创建

    private String createUserId;

    private String createTime;

    private String processDefinitionKey;

    private Integer isUseNumber;

    private Integer wordFileId;//word模板文件ID

    private String color;//颜色

    @TableField(exist = false)
    private String parentId;

    @TableField(exist = false)
    private String isOwnChildren;//是否拥有子表  0为没有 1为有

    @TableField(exist = false)
    private String fileType;

    @TableField(exist = false)
    private String fileName;

    public String getColor() {
        return color;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIsOwnChildren() {
        return isOwnChildren;
    }

    public void setIsOwnChildren(String isOwnChildren) {
        this.isOwnChildren = isOwnChildren;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getWordFileId() {
        return wordFileId;
    }

    public void setWordFileId(Integer wordFileId) {
        this.wordFileId = wordFileId;
    }

    public Integer getIsUseNumber() {
        return isUseNumber;
    }

    public void setIsUseNumber(Integer isUseNumber) {
        this.isUseNumber = isUseNumber;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getIsCreateTable() {
        return isCreateTable;
    }

    public void setIsCreateTable(Integer isCreateTable) {
        this.isCreateTable = isCreateTable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Integer postStatus) {
        this.postStatus = postStatus;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getFormIdentification() {
        return formIdentification;
    }

    public void setFormIdentification(String formIdentification) {
        this.formIdentification = formIdentification;
    }

    public String getFormJson() {
        return formJson;
    }

    public void setFormJson(String formJson) {
        this.formJson = formJson;
    }

}
