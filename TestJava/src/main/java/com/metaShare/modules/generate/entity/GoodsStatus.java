package com.metaShare.modules.generate.entity;
import java.util.*;
public enum GoodsStatus {
	OffLine2(2,"OffLine2"),
	
	OnLine(1,"OnLine1"),
	
	Deleted(3,"Deleted3"),
	
	;
	
	private Object value;
	private String desc;
	private GoodsStatus(Object value,String desc ) {
		this.value = value;
		this.desc = desc;
	}
	
	public String getDesc() {
	
	public Object getValue() {
	public static Map<Object, String> findEnumMap() {
		public static List findEnumList() {
}