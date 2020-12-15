package com.metaShare.modules.bpm.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.entity.BpmDelegate;

public interface BpmDelegateDao extends BaseMapper<BpmDelegate>{
	
	List<BpmDelegate> findAllWithNoPageNoSort(@Param("loginName") String loginName,
                                              @Param("processKey") String processKey,
                                              @Param("date") Date date);

	List<BpmDelegate> findAllByLoginNames(@Param("loginName") List<String> loginNames,
                                          @Param("processKey") String processKey,
                                          @Param("date") Date date);

	List<BpmDelegate> findAllDateAndId(@Param("checkStartDate") Date checkStartDate,
                                       @Param("checkEndDate") Date checkEndDate,
                                       @Param("user") String user,
                                       @Param("processKey") String processKey,
                                       @Param("id") String id);

	List<AllFormAppDTO> findApp(@Param("processName") String processName, @Param("loginName") String loginName,@Param("approvalType")Integer approvalType,@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("processDefinitionName")String processDefinitionName);

}
