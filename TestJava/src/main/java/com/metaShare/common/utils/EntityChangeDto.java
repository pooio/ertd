package com.metaShare.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class EntityChangeDto {

	/**
	 * 原对象转为新的对象
	 * 
	 * @param sourceList
	 *            原对象集合
	 * @param target
	 *            目标类
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> objectToOtherObjectList(List<?> sourceList, Class<T> target) throws Exception {
		List<T> result = new ArrayList<T>();
		if (null != sourceList && !sourceList.isEmpty()) {
			for (Object o : sourceList) {
				result.add((T) EntityChangeDto.objectToOtherObject(o, target.newInstance()));
			}
		}
		return result;
	}

	/*
	 * 
	 * 原对象属性复制到新对象中：source 的属性 和target 属性相相同的可互相转化
	 * 
	 * @param source 原对象
	 * 
	 * @param target 目标对象
	 * 
	 * @return 目标对象
	 */
	public static <T> T objectToOtherObject(Object source, T target) throws Exception {
		if (null != source) {
			BeanUtils.copyProperties(source, target);
		}
		return target;
	}


	/**
	 * 对比两个对象是否相同
	 * 
	 * @param source
	 *            原对象
	 * @param target
	 *            目标对象
	 @param filed
	 *            修改的属性         
	 * @return
	 */
	public static <T> Map<String, String> companyEntity(T source, T target,List<String> filed) {
		Map<String, String> map = new HashMap<String,String>();
		map.put("id", "");
		map.put("updateInfo","");
		Set<String> keySet = null;
		ObjectMapper mapper = new ObjectMapper();
		StringBuffer modifies = new StringBuffer();
		if(!EntityChangeDto.vailIsNull(source,target) && EntityChangeDto.vailIsSameClass(source,target) ){
			Map<String, Object> sourceMap = mapper
					.convertValue(source, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> targetMap = mapper
					.convertValue(target, new TypeReference<Map<String, Object>>() {});
			if(null == filed || filed.size()==0 ){
				keySet = sourceMap.keySet();
			}else{
				keySet = new HashSet<>(filed);
			}
			for (String key : keySet) {
				Object sourceObject = sourceMap.get(key);
				Object targetObject = targetMap.get(key);
				Boolean sourceObjectIsnull = null == sourceObject?true:false;
				Boolean targetObjectIsnull = null == targetObject?true:false;
				Boolean targetObjectIsEntitny = EntityChangeDto.IsEntitny(targetObjectIsnull,targetObject);//是否是对象
				Boolean isCompara = EntityChangeDto.notCompara(key);//是否可做比较
				if(isCompara){
					EntityChangeDto.compara(sourceObjectIsnull,sourceObject, targetObjectIsnull,targetObject,targetObjectIsEntitny,key, modifies);
				}
			}
			map.put("id", sourceMap.get("id").toString());
			map.put("updateInfo", modifies.toString());
		}
		
		return map;
	}

	
	public static String getId(Object source){
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> sourceMap = mapper
				.convertValue(source, new TypeReference<Map<String, Object>>() {});
		return sourceMap.get("id").toString();
	}
	
	/**
	 * 类是否相同
	 * @param source 原类
	 * @param target 目标类
	 * @return
	 */
	private static  <T>  boolean vailIsSameClass(T source, T target) {
		if (source.getClass().getName().equals(target.getClass().getName())) {
			return true;
		}
		return false;
	}

	private static Boolean vailIsNull(Object source,Object target){
		if (null == source || null == target) {
			return true;
		}
		return false;
	}
	
	/**
	 * 对比
	 * @param sourceObjectIsnull 原是否空
	 * @param sourceObject  原对象
	 * @param targetObjectIsnull 目标是否空
	 * @param targetObject 目标对象
	 * @param targetObjectIsEntitny  目标是否实体
	 * @param key 标识符
	 * @param modifies 修改结果
	 */
	private static void compara(Boolean sourceObjectIsnull, Object sourceObject, Boolean targetObjectIsnull, Object targetObject, Boolean targetObjectIsEntitny, String key, StringBuffer modifies) {
		if(targetObjectIsEntitny){
			EntityChangeDto.comparaEntityId(sourceObjectIsnull,sourceObject,targetObjectIsnull,targetObject,modifies);
		}else{
			EntityChangeDto.comparaValueId(sourceObjectIsnull,sourceObject,targetObjectIsnull,targetObject,key,modifies);
		}
		
	}

	/**
	 * 比较值是否一致
	 * @param sourceObjectIsnull  原对象是否空
	 * @param sourceObject 原对象
	 * @param targetObjectIsnull 目标是否空
	 * @param targetObject 目标是否实体
	 * @param key 标识符
	 * @param modifies 修改结果
	 */
	private static void comparaValueId(Boolean sourceObjectIsnull, Object sourceObject, Boolean targetObjectIsnull,
			Object targetObject, String key, StringBuffer modifies) {
		if(!sourceObjectIsnull && !targetObjectIsnull && !sourceObject.toString().equals(targetObject.toString())){
			modifies.append(key).append(":由").append(sourceObject.toString()).append("变为 ")
			.append(targetObject.toString()).append(".\n");
		}else if(sourceObjectIsnull && !targetObjectIsnull ){
			modifies.append(key).append(":由空").append("变为 ")
			.append(targetObject.toString()).append(".\n");
		}else if(!sourceObjectIsnull && targetObjectIsnull){
			modifies.append(key).append(":由").append(sourceObject.toString())
			.append("变为空").append(".\n");
		}
	}

	/**
	 * 比较实体ID是否一致
	 * @param sourceObjectIsnull 原是否为空
	 * @param sourceObject  原对象
	 * @param targetObjectIsnull 目标是否为空
	 * @param targetObject  目标对象
	 * @param modifies 修改信息
	 */
	private static void comparaEntityId(Boolean sourceObjectIsnull, Object sourceObject, Boolean targetObjectIsnull, Object targetObject, StringBuffer modifies) {
		String sourceObjecId = "";
		String targetObjecId = "";
		ObjectMapper mapper = new ObjectMapper();
		if(!sourceObjectIsnull){
			Map<String, Object> sourceMap = mapper
			.convertValue(sourceObject, new TypeReference<Map<String, Object>>() {});
			if(null != sourceMap.get("id")){
				sourceObjecId = sourceMap.get("id").toString();
			}
		}
		if(!targetObjectIsnull){
			Map<String, Object> targetMap = mapper
			.convertValue(sourceObject, new TypeReference<Map<String, Object>>() {});
			if(null != targetMap.get("id")){
				targetObjecId = targetMap.get("id").toString();
			}
		}
		if(StringUtils.isNotEmpty(sourceObjecId) && StringUtils.isNotEmpty(targetObjecId) && !sourceObjecId.equals(targetObjecId)){
			modifies.append("id").append(":由").append(sourceObjecId).append("变为 ")
			.append(targetObjecId).append(".\n");
		}else if(StringUtils.isEmpty(sourceObjecId) && StringUtils.isNotEmpty(targetObjecId)){
			modifies.append("id").append(":由空").append("变为 ")
			.append(targetObjecId).append(".\n");
		}else if(StringUtils.isNotEmpty(sourceObjecId) && StringUtils.isEmpty(targetObjecId)){
			modifies.append("id").append(":由").append(sourceObjecId)
			.append("变为空").append(".\n");
		}
	}

	/**
	 * 对象是否是实体
	 * @param targetObjectIsnull
	 * @param targetObject
	 * @return
	 */
	private static Boolean IsEntitny(Boolean targetObjectIsnull, Object targetObject) {
		Boolean flag = false;
		try {
			if(!targetObjectIsnull){//非空
				String name = targetObject.getClass().getAnnotation(Table.class).name();
				if(StringUtils.isNotEmpty(name)){
					flag = true;
				}
			}
		} catch (Exception e) {
		}
		
		return flag;
	}

	private static Boolean notCompara(String key) {
		Boolean flag = true;
		switch (key) {
		case "id":
		case "createTime":
		case "updateTime":
		case "createUser":
		case "updateUser":
		case "new":
			flag = false;
		}

		return flag;
	}
}
