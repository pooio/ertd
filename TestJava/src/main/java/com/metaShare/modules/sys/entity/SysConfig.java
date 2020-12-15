package com.metaShare.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.metaShare.modules.core.entity.CustomBaseEntity;

@TableName("sys_config")
public class SysConfig extends CustomBaseEntity{
	
	private String configContent;
	
	private String configType;
	
	private int inUse = 1;
	
	private String updateTime;
 

	public String getConfigContent() {
		return configContent;
	}

	public void setConfigContent(String configContent) {
		this.configContent = configContent;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public int getInUse() {
		return inUse;
	}

	public void setInUse(int inUse) {
		this.inUse = inUse;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
