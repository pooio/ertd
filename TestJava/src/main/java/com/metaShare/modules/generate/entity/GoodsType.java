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
	
	public String getDesc() {		return desc;	}
	
	public Object getValue() {		return value;	}
	public static Map<Object, String> findEnumMap() {        EnumSet<GoodsType> currEnumSet = EnumSet.allOf(GoodsType.class);        Map<Object, String> map = new HashMap<>();        for (GoodsType ts : currEnumSet) {        		map.put(ts.getValue(), ts.getDesc());        }        return map;    }
		public static List findEnumList() {        EnumSet<GoodsType> currEnumSet = EnumSet.allOf(GoodsType.class);        List<Map<String, String>> list = new ArrayList<Map<String, String>>();        for (GoodsType ts : currEnumSet) {        		 Map<String,String > map = new HashMap<>();        		 map.put("key",ts.getValue().toString());        		 map.put("value",ts.getDesc());        		list.add(map);      		        }        return list;    }
}
