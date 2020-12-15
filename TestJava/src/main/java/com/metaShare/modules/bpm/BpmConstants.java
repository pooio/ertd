package com.metaShare.modules.bpm;

public interface BpmConstants {
	/**
	 * 申请人流程变量名称
	 */
	String APPLY_USER = "applyUser";
	/**
	 * 通过
	 */
	String PASS = "pass";
	/**
	 * 不通过
	 */
	String NOT_PASS = "notPass";
	/**
	 * 业务主键流程变量名称
	 */
	String BUSINESS_KEY = "businessKey";
	/**
	 * 流程结束类型流程变量名称
	 */
	String PROCESS_END_TYPE = "processEndType";
	
	/**
	 * 业务信息
	 */
	String BUSINESS_INFO = "businessInfo";
	
	/**
	 * 消息提醒url
	 */
	String MESSAGE_URL = "messageURL";
	
	
	/**
	 * 流程条件金额
	 */
	String PROCESS_MONEY="money";
	
	/**
	 * 是否到下一节点
	 */
	String PROCESS_IS_NEXT_NODE = "isNextNode";
	
	/**
	 * 是否选择用户
	 */
	String PROCESS_IS_CHOOSE_USER = "isChooseUser";
}
