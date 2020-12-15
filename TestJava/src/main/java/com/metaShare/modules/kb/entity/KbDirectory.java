/**  
 * @Title: BbDirectory.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:21:27 
 */
package com.metaShare.modules.kb.entity;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * @ClassName: BbDirectory
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:21:27
 */
@TableName("kb_directory")
public class KbDirectory {
	@TableId(type = IdType.AUTO)
	private int id;
	private String KbDirName;
	private String KbDirCode;
	private String KbDirGbcode;
	private String CreateTime;
	private int kbDirClevel;

	public int getKbDirClevel() {
		return kbDirClevel;
	}

	public void setKbDirClevel(int kbDirClevel) {
		this.kbDirClevel = kbDirClevel;
	}

	@TableField(exist = false)
	private List<KbDirectory> children = new ArrayList<>();

	public List<KbDirectory> getChildren() {
		return children;
	}

	public void setChildren(List<KbDirectory> children) {
		this.children = children;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKbDirName() {
		return KbDirName;
	}

	public void setKbDirName(String kbDirName) {
		KbDirName = kbDirName;
	}

	public String getKbDirCode() {
		return KbDirCode;
	}

	public void setKbDirCode(String kbDirCode) {
		KbDirCode = kbDirCode;
	}

	public String getKbDirGbcode() {
		return KbDirGbcode;
	}

	public void setKbDirGbcode(String kbDirGbcode) {
		KbDirGbcode = kbDirGbcode;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

}
