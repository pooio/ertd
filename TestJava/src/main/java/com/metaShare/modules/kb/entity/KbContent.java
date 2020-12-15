package com.metaShare.modules.kb.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * Created by pc on 2020/2/18.
 */
@TableName("kb_content")
public class KbContent {
	@TableId(type = IdType.AUTO)
	private int kbPk;
	private String kbCode;
	private String kbDirPk;
	private String kbDirCode;
	private String kbDirName;
	private String kbName;
	private String keyWord;
	private String kbContent;
	private String kbLinkCode;
	private String kbTypePk;
	private String kbTypeName;
	private String auditName;
	private String auditmanDemo;
	private String auditmanTime;
	private String url;
	private String kbDemo;
	private int kbCount;
	private int kbViewCount;
	private String createId;
	private String createName;
	private String createTime;
	private String updateName;
	private String updateUserId;
	private String updateTime;
	private Integer approvalStatus;

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public int getKbPk() {
		return kbPk;
	}

	public void setKbPk(int kbPk) {
		this.kbPk = kbPk;
	}

	public String getKbCode() {
		return kbCode;
	}

	public void setKbCode(String kbCode) {
		this.kbCode = kbCode;
	}

	public String getKbDirPk() {
		return kbDirPk;
	}

	public void setKbDirPk(String kbDirPk) {
		this.kbDirPk = kbDirPk;
	}

	public String getKbDirCode() {
		return kbDirCode;
	}

	public void setKbDirCode(String kbDirCode) {
		this.kbDirCode = kbDirCode;
	}

	public String getKbDirName() {
		return kbDirName;
	}

	public void setKbDirName(String kbDirName) {
		this.kbDirName = kbDirName;
	}

	public String getKbName() {
		return kbName;
	}

	public void setKbName(String kbName) {
		this.kbName = kbName;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKbContent() {
		return kbContent;
	}

	public void setKbContent(String kbContent) {
		this.kbContent = kbContent;
	}

	public String getKbLinkCode() {
		return kbLinkCode;
	}

	public void setKbLinkCode(String kbLinkCode) {
		this.kbLinkCode = kbLinkCode;
	}

	public String getKbTypePk() {
		return kbTypePk;
	}

	public void setKbTypePk(String kbTypePk) {
		this.kbTypePk = kbTypePk;
	}

	public String getKbTypeName() {
		return kbTypeName;
	}

	public void setKbTypeName(String kbTypeName) {
		this.kbTypeName = kbTypeName;
	}

	public String getAuditName() {
		return auditName;
	}

	public void setAuditName(String auditName) {
		this.auditName = auditName;
	}

	public String getAuditmanDemo() {
		return auditmanDemo;
	}

	public void setAuditmanDemo(String auditmanDemo) {
		this.auditmanDemo = auditmanDemo;
	}

	public String getAuditmanTime() {
		return auditmanTime;
	}

	public void setAuditmanTime(String auditmanTime) {
		this.auditmanTime = auditmanTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKbDemo() {
		return kbDemo;
	}

	public void setKbDemo(String kbDemo) {
		this.kbDemo = kbDemo;
	}

	public int getKbCount() {
		return kbCount;
	}

	public void setKbCount(int kbCount) {
		this.kbCount = kbCount;
	}

	public int getKbViewCount() {
		return kbViewCount;
	}

	public void setKbViewCount(int kbViewCount) {
		this.kbViewCount = kbViewCount;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
