package com.metaShare.modules.generate.entity;
import java.util.*;
public enum GoodsType {
	Domain(2,"Domain"),
	
	Application(1,"Application"),
	
	RoadMap(3,"RoadMap"),
	
	;
	
	private Object value;
	private String desc;
	private GoodsType(Object value,String desc ) {
		this.value = value;
		this.desc = desc;
	}
	
	public String getDesc() {
	
	public Object getValue() {
	public static Map<Object, String> findEnumMap() {
		public static List findEnumList() {
}