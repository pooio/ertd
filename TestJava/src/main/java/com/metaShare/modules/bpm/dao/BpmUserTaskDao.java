package com.metaShare.modules.bpm.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.bpm.entity.BpmUserTask;
import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BpmUserTaskDao extends BaseMapper<BpmUserTask> {

    BpmUserTask selectByTaskId(@Param("taskId") String taskId);

    void updateApprovalStatusSelf(@Param("id") String id,@Param("approvalStatus") int approvalStatus,@Param("processId")String processId);

    void updateApprovalStatusOnTable(@Param("tableName") String tableName,@Param("businessKey") String businessKey,@Param("type") int type,@Param("approvalStatus") int approvalStatus);

    void updateApprovalStatusAndTime(@Param("id") String id,@Param("approvalStatus") int approvalStatus,@Param("endTime") String endTime);

    Map<String, String> getTaskInfoOnProcessId(@Param("processId") String processId);

    int getHistoryProcessInstanceTotal(@Param("processName") String processName,@Param("loginName") String loginName,@Param("type")Integer type);

    BpmUserTask getByBusinessKeyAndProcessKey(@Param("businessKey") String businessKey,@Param("processDefinitionKey") String processDefinitionKey);

    int getToTasksTotal(@Param("loginName") String loginName,@Param("processDefinitionName") String processDefinitionName,@Param("approvalType") Integer approvalType,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("applyUserName")String applyUserName);

    int getHistorickTotal(@Param("loginName") String loginName,@Param("approvalType") Integer approvalType,@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("processDefinitionName") String processDefinitionName);
}
