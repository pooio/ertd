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
	
	public String getDesc() {		return desc;	}
	
	public Object getValue() {		return value;	}
	public static Map<Object, String> findEnumMap() {        EnumSet<LabelType> currEnumSet = EnumSet.allOf(LabelType.class);        Map<Object, String> map = new HashMap<>();        for (LabelType ts : currEnumSet) {        		map.put(ts.getValue(), ts.getDesc());        }        return map;    }
		public static List findEnumList() {        EnumSet<LabelType> currEnumSet = EnumSet.allOf(LabelType.class);        List<Map<String, String>> list = new ArrayList<Map<String, String>>();        for (LabelType ts : currEnumSet) {        		 Map<String,String > map = new HashMap<>();        		 map.put("key",ts.getValue().toString());        		 map.put("value",ts.getDesc());        		list.add(map);      		        }        return list;    }
}
