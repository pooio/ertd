package com.metaShare.modules.sys.vo;

import com.metaShare.modules.core.entity.CustomBaseEntity;
import com.metaShare.modules.sys.entity.SysMessage;

import java.util.List;


public class SysMessageVO extends CustomBaseEntity {

	private List<SysMessage> messageList;

	private int total;

	public List<SysMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<SysMessage> messageList) {
		this.messageList = messageList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
