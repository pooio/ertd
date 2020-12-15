package com.metaShare.modules.bpm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.bpm.entity.BpmStatus;

public interface  BpmStatusDao extends BaseMapper<BpmStatus>{
	BpmStatus findByProcessInstinceId(@Param("processInstinceId") String processInstinceId);
	
	List<BpmStatus> findByBusinessKey(@Param("businessKey") String businessKey);
	
	List<BpmStatus> findByBusinessKeyIn(String[] businessKey);
	
	void deleteByBusinessKeyAndDefineKey(@Param("businessKey") String businessKey, @Param("processDefinitionKey") String processDefinitionKey);
	
	void updateActiveFalseBybusinessKey(@Param("businessKey") String businessKey);
	
	void deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
	
	List<String> findactruexecution(@Param("businessKey") String businessKey);
	
	void deleteactruidentitylink(@Param("procinstId") String procinstId);
	
	void deleteactrutask(@Param("procinstId") String procinstId);
	
	void deleteactruvariable(@Param("procinstId") String procinstId);
	
	void deleteactruexecution(@Param("procinstId") String procinstId);
}