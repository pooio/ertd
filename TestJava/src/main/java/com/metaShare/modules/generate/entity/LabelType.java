package com.metaShare.modules.generate.entity;
import java.util.*;
public enum LabelType {
	Language(3,"Language"),
	
	Application(2,"Application"),
	
	Label(1,"Label"),
	
	Database(4,"Database"),
	
	;
	
	private Object value;
	private String desc;
	private LabelType(Object value,String desc ) {
		this.value = value;
		this.desc = desc;
	}
	
	public String getDesc() {
	
	public Object getValue() {
	public static Map<Object, String> findEnumMap() {
		public static List findEnumList() {
}