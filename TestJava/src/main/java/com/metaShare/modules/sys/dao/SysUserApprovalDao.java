package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysUserApprovalNode;


public interface SysUserApprovalDao extends BaseMapper<SysUserApprovalNode> {
	
	//根据业务主键、节点获取审批人
	List<SysUserApprovalNode> getApproval(@Param("nextProcessId") String nextProcessId,
										  @Param("nextBusinessId") String nextBusinessId,
										  @Param("processFlag") String processFlag);
	
	List<SysUserApprovalNode> getSureApproval(@Param("businessKey") String businessKey,
											  @Param("processFlag") String processFlag);

	List<SysUserApprovalNode> getApprovalUser(@Param("businessKey") String businessKey,
											  @Param("userId") String userId);
	
	void deletechoseInfo(@Param("businessKey") String businessKey,
					     @Param("processDefinitionKey") String processDefinitionKey);
}
