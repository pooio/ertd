package com.metaShare.modules.bpm.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bpm.dao.BpmDelegateDao;
import com.metaShare.modules.bpm.dao.BpmUserTaskDao;
import com.metaShare.modules.bpm.dto.AllFormAppDTO;
import com.metaShare.modules.bpm.dto.BpmTask;
import com.metaShare.modules.bpm.entity.BpmDelegate;
import com.metaShare.modules.bpm.entity.BpmUserTask;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service(value="BpmUserTaskService")
public class BpmUserTaskService extends ServiceImpl<BpmUserTaskDao, BpmUserTask>{

    @Autowired
    private BpmUserTaskDao bpmUserTaskDao;

    public BpmUserTask selectByTaskId(String taskId) {
        return bpmUserTaskDao.selectByTaskId(taskId);
    }

    public void updateApprovalStatusSelf(String id,int approvalStatus,String processId) {
        bpmUserTaskDao.updateApprovalStatusSelf(id,approvalStatus,processId);
    }

    public void updateApprovalStatusOnTable(String tableName, String businessKey, int type, int approvalStatus) {
        bpmUserTaskDao.updateApprovalStatusOnTable(tableName,businessKey,type,approvalStatus);
    }

    public void updateApprovalStatusAndTime(String id, int approvalStatus, String endTime) {
        bpmUserTaskDao.updateApprovalStatusAndTime(id,approvalStatus,endTime);
    }

    public Map<String, String> getTaskInfoOnProcessId(String executionId) {
        return bpmUserTaskDao.getTaskInfoOnProcessId(executionId);
    }

    public int getHistoryProcessInstanceTotal(String processName, String loginName,Integer type) {
        return bpmUserTaskDao.getHistoryProcessInstanceTotal(processName,loginName,type);
    }

    public BpmUserTask getByBusinessKeyAndProcessKey(String businessKey, String processDefinitionKey) {
        return bpmUserTaskDao.getByBusinessKeyAndProcessKey(businessKey,processDefinitionKey);
    }

    public int getToTasksTotal(String loginName, String processDefinitionName, Integer approvalType, String startDate, String endDate,String applyUserName) {
        return bpmUserTaskDao.getToTasksTotal(loginName,processDefinitionName,approvalType,startDate,endDate,applyUserName);
    }

    public int getHistorickTotal(String loginName, Integer approvalType, String startDate, String endDate,String processDefinitionName) {
        return bpmUserTaskDao.getHistorickTotal(loginName,approvalType,startDate,endDate,processDefinitionName);
    }
}
