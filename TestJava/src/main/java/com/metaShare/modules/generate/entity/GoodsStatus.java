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
	
	public String getDesc() {		return desc;	}
	
	public Object getValue() {		return value;	}
	public static Map<Object, String> findEnumMap() {        EnumSet<GoodsStatus> currEnumSet = EnumSet.allOf(GoodsStatus.class);        Map<Object, String> map = new HashMap<>();        for (GoodsStatus ts : currEnumSet) {        		map.put(ts.getValue(), ts.getDesc());        }        return map;    }
		public static List findEnumList() {        EnumSet<GoodsStatus> currEnumSet = EnumSet.allOf(GoodsStatus.class);        List<Map<String, String>> list = new ArrayList<Map<String, String>>();        for (GoodsStatus ts : currEnumSet) {        		 Map<String,String > map = new HashMap<>();        		 map.put("key",ts.getValue().toString());        		 map.put("value",ts.getDesc());        		list.add(map);      		        }        return list;    }
}
