package com.metaShare.common.utils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public enum StatusEnum {

	/** 日志类型 **/
	LogType, 
	LogType1(1, "后台日志", LogType), 

	/* 成功状态码 */
	SUCCESS(0, "成功"),
 
	
	/**
	 * SysConfigType
	 * 系统配置--配置类型
	 */
	SysConfigType,
	SysConfigType1(1,"系统描述",SysConfigType),
	SysConfigType2(2,"系统标题",SysConfigType),
	SysConfigType3(3,"系统版权",SysConfigType),
	SysConfigType4(4,"系统搜索关键字",SysConfigType),
	SysConfigType5(5,"文件服务器上传地址",SysConfigType),
	SysConfigType6(6,"文件服务器访问地址",SysConfigType),
	SysConfigType7(7,"项目图片地址",SysConfigType),
	SysConfigType8(8,"系统logo",SysConfigType),

	MessageType,
	MessageType1(1,"系统消息",MessageType),
	MessageType2(2,"合同预警",MessageType),
	MessageType3(3,"延迟收款预警",MessageType),
	MessageType4(4,"预算编制",MessageType),
	MessageType5(5,"流程审批",MessageType),
	MessageType6(6,"全周期预算",MessageType),
	MessageType7(7,"总结报告超期",MessageType),
	LogInfoType,
	LogInfoType1(1,"系统管理",LogInfoType),
	LogInfoType2(2,"系统配置",LogInfoType),
	LogInfoType3(3,"菜单管理",LogInfoType),
	LogInfoType4(4,"部门管理",LogInfoType),
	LogInfoType5(5,"数据字典",LogInfoType),
	LogInfoType6(6,"职务管理",LogInfoType),
	LogInfoType7(7,"用户管理",LogInfoType),
	LogInfoType8(8,"区域管理",LogInfoType),
	LogInfoType9(9,"系统公告",LogInfoType),
	LogInfoType10(10,"自定义表单",LogInfoType),
	LogInfoType11(11,"表单数据",LogInfoType),
	LogInfoType12(12,"角色管理",LogInfoType),
	LogInfoType13(13,"知识库管理",LogInfoType),
	LogInfoType14(14,"流程管理",LogInfoType),
	LogInfoType15(15,"轮播图日志",LogInfoType),
	;
	private Object value;// 值

	private String desc;// 描述

	private StatusEnum type;// 类型
	StatusEnum() {
	}
	
	StatusEnum(Object value) {
		this.value = value;
	}
	
	StatusEnum(Object value ,String desc) {
		this.value = value;
		this.desc = desc;
	}
	StatusEnum(Object value ,String desc,StatusEnum type) {
		this.value = value;
		this.desc = desc;
		this.type = type;
	}
	
	public String getDesc() {
		return desc;
	}

	public Object getValue() {
		return value;
	}
	
	public int getIntValue() {
		return Integer.parseInt(value.toString());
	}
	
	public String getStrValue() {
		return value.toString();
	}
	
	public StatusEnum getType() {
		return type;
	}

	/**
	  * @Title findEnumList
	  * @Description TODO 根据枚举常量类型 获取枚举值集合
	  * @param type
	  * @return  设定文件
	  * @return Map<Object,String>    返回类型
	  * @author XIDF
	  * @company 
	  * @date 
	 */
	public static Map<Object, String> findEnumMap(StatusEnum type) {
        EnumSet<StatusEnum> currEnumSet = EnumSet.allOf(StatusEnum.class);
        Map<Object, String> map = new HashMap<>();
        for (StatusEnum ts : currEnumSet) {
        	if(type.equals(ts.getType()))
        		map.put(ts.getValue(), ts.getDesc());
        }
        return map;
    }
	public static List findEnumList(StatusEnum type) {
        EnumSet<StatusEnum> currEnumSet = EnumSet.allOf(StatusEnum.class);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (StatusEnum ts : currEnumSet) {
        	if(type.equals(ts.getType())){
        		 Map<String,String > map = new HashMap<>();
        		 map.put("key",ts.getValue().toString());
        		 map.put("value",ts.getDesc());
        		list.add(map);
        	}
        		
        }
        return list;
    }
	
	/**
	  * @Title findEnumDescByValue
	  * @Description TODO 根据类型和值获取描述
	  * @param type
	  * @param value
	  * @return  设定文件
	  * @return String    返回类型
	  * @author XIDF
	  * @company 
	  * @date  
	 */
	public static String findEnumDescByValue(StatusEnum type,Object value){
		String desc = null;
		Map<Object, String> currEnumSet = findEnumMap(type);
			for (Object key : currEnumSet.keySet()) {
				if(String.valueOf(key).equals(String.valueOf(value)))
	        		desc = currEnumSet.get(key);
			}
		return desc;
	}
	/**
	  * @Title findValueByEnumDesc
	  * @Description TODO 根据类型获取值
	  * @param desc
	  * @param type
	  * @return  设定文件
	  * @return String    返回类型
	  * @author XIDF
	  * @company 
	  * @date 
	 */
	public static String findValueByEnumDesc(Object desc,StatusEnum ...type){
		String value = null;
		desc = desc.toString().replaceAll(" ","");
		Map<Object, String> currEnumSet = new HashMap<Object, String>();
		for (StatusEnum statusEnum : type) {
			currEnumSet.putAll(StatusEnum.findEnumMap(statusEnum));
		}
		for (Object val : currEnumSet.keySet()) {
			if(currEnumSet.get(val).toString().replaceAll(" ","").equals(String.valueOf(desc))){
				value = val.toString();
				break;
			}
		}
		return value;
	}
	/**
	  * @Title findEnumDescByValue
	  * @Description TODO 根据类型string和值获取描述 tomcat8.0以下不支持类的传输所以改为以下类型
	  * tomcat8.0以上可以直接使用le表达式传输${StatusEnum.findEnumDescByValue(StatusEnum.userType,user.acc_type)}
	  * @param type
	  * @param value
	  * @return  设定文件
	  * @return String    返回类型
	  * @author XIDF
	  * @company 
	  * @date 
	 */
	public static String findEnumDescByValue(String type,Object value){
		String desc = null;
		Map<Object, String> currEnumSet = findEnumMap(StatusEnum.valueOf(type));
		for (Object key : currEnumSet.keySet()) {
			if(String.valueOf(key).equals(String.valueOf(value)))
        		desc = currEnumSet.get(key);
		}
		return desc;
	}
	/**
	  * @Title findEnumByValue
	  * @Description TODO  根据类型和值返回枚举对象
	  * @param type
	  * @param value
	  * @return  设定文件
	  * @return StatusEnum    返回类型
	  * @author XIDF
	  * @company 
	  * @date 
	 */
	public static StatusEnum findEnumByValue(StatusEnum type,Object value){
		EnumSet<StatusEnum> currEnumSet = EnumSet.allOf(StatusEnum.class);
		StatusEnum returnTs = null;
        for (StatusEnum ts : currEnumSet) {
        	if(type.equals(ts.getType())&&ts.getValue().toString().equals(value.toString())){
        		returnTs = ts;
        		break;
        	}
        }
		return returnTs;
	}
	/**
	 * @description 根据类型和值获取描述
	 * @method findEnumDescByValue
	 * @param type 类型 
	 * @param value 值
	 * @return String
	 * @author XIDF
	 * @date 
	 */
	public static String findEnumDescByLike(StatusEnum type,Object value,int index){
		String desc = "";
		Map<Object, String> currEnumSet = findEnumMap(type);
		if(value.toString().length()<index){
			index = value.toString().length();
		}
		for (Object key : currEnumSet.keySet()) {
			if(String.valueOf(key).substring(0,index).equals(String.valueOf(value).substring(0,index)))
        		desc = currEnumSet.get(key);
		}
		return desc;
	}
}
