package com.metaShare.modules.bpm.listener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.metaShare.modules.bpm.entity.BpmUserTask;
import com.metaShare.modules.bpm.service.BpmUserTaskService;
import com.metaShare.modules.customize.entity.CustomData;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.customize.service.CustomDataService;
import com.metaShare.modules.customize.service.CustomFormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.bpm.entity.BpmStatus;
import com.metaShare.modules.bpm.service.BpmStatusService;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysMessageService;
import com.metaShare.modules.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessCompletedEventListener implements ActivitiEventListener, Serializable {
	private static Logger logger = LoggerFactory.getLogger(ProcessCompletedEventListener.class);

	@Autowired
	private BpmUserTaskService bpmUserTaskService;
	@Autowired
	private CustomDataService customDataService;
	@Autowired
	private CustomFormService customFormService;

	@Override
	public void onEvent(ActivitiEvent event) {

		switch (event.getType()) {
		case PROCESS_COMPLETED:
			try {
				this.onEnd(event);
			} catch (Exception e) {
				logger.error("流程结束时，写入流程状态表信息失败！");
				e.printStackTrace();
			}
		case ACTIVITY_COMPLETED:
			try {
				System.out.println("ACTIVITY_COMPLETED");
				logger.info("ACTIVITY_COMPLETED");
			} catch (Exception e) {
				logger.error("ACTIVITY_COMPLETED");
				e.printStackTrace();
			}
		default:
			logger.debug("activiti事件到达: " + event.getType());

		}

	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

	public void onEnd(ActivitiEvent event) throws Exception {
		HistoryService historyService = event.getEngineServices().getHistoryService();
		RuntimeService runtimeService = event.getEngineServices().getRuntimeService();
		// 流程结束插入流程状态信息
		Map<String, Object> valiables = runtimeService.getVariables(event.getExecutionId());
		Object endType = valiables.get(BpmConstants.PROCESS_END_TYPE);

		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(event.getProcessInstanceId()).singleResult();
		historicProcessInstance.getBusinessKey();
		BpmStatusService bpmStatusService = SpringUtils.getBean("bpmStatusService");
		BpmStatus bpmStatus = bpmStatusService.findByProcessInstinceId(historicProcessInstance.getId());
		if (BpmConstants.NOT_PASS.equals(endType)) {// 审批不通过
			bpmStatus.setProcessStatus(2L);
		} else {
			bpmStatus.setProcessStatus(1L);
		}
		bpmStatusService.insertOrUpdate(bpmStatus);

		// 流程结束给流程发起人发送消息
		// ContractorBaseInfoService as =
		// SpringHelper.getBeanByClassname(ContractorBaseInfoService.class);
		SysMessageService messageService = SpringUtils.getBean("sysMessageService");
		SysUserService userService = SpringUtils.getBean("sysUserService");
		SysUser applyUser = userService.findByLoginName((String) valiables.get(BpmConstants.APPLY_USER));
		SysMessage message = new SysMessage();
		message.setReceiver(applyUser.getId());
		message.setStatus(0L);
		message.setCreateTime(new Date());
		String msgURL = (String) valiables.get(BpmConstants.MESSAGE_URL);
		if (StringUtils.isNotEmpty(msgURL)) {
			message.setUrl(msgURL);
		}
		if (BpmConstants.NOT_PASS.equals(endType)) {// 审批不通过
			//message.setSubject("<font color='red'>" + valiables.get(BpmConstants.BUSINESS_INFO) + "</font>");
			message.setSubject(String.valueOf(valiables.get(BpmConstants.BUSINESS_INFO)));
			// message.setContent(applyUser.getName() + "您好，您的流程<b>" +
			// valiables.get(BpmConstants.BUSINESS_INFO)
			// + "</b>未审批通过");
			message.setContent("您好，您的流程" + valiables.get(BpmConstants.BUSINESS_INFO) + "未审批通过");
		} else {
			//message.setSubject("<font color='green'>" + valiables.get(BpmConstants.BUSINESS_INFO) + "</font>");
			message.setSubject(String.valueOf(valiables.get(BpmConstants.BUSINESS_INFO)));
			// message.setContent(applyUser.getName() + "您好，您的流程<b>" +
			// valiables.get(BpmConstants.BUSINESS_INFO)
			// + "</b>已审批通过");
			message.setContent("您好，您的流程" + valiables.get(BpmConstants.BUSINESS_INFO) + "已审批通过");
		}
		messageService.saveAndFlush(message);
		// 业务的主键ID
		String id = (String) valiables.get(BpmConstants.BUSINESS_KEY);
		// 流程标识
		String processKey = bpmStatus.getProcessDefinitionKey();
//		// 修改业务状态
		BpmUserTask bpmUserTask = bpmUserTaskService.selectByTaskId(historicProcessInstance.getId());
		//修改自定义表单isUseNumber参数
		CustomData customData = customDataService.getDataJson(bpmUserTask.getBusinessKey());
		CustomForm customForm = customFormService.selectById(customData.getCustomFormId());
		customForm.setIsUseNumber(customForm.getIsUseNumber() - 1);
		customFormService.updateById(customForm);
		String tableName = bpmUserTask.getTableName();
		int type = 0;
		int approvalStatus = 2;
		if(BpmConstants.NOT_PASS.equals(endType)){
			approvalStatus = 3;
		}
		if(StringUtils.isNotEmpty(tableName)){
			if("kb_content".equals(tableName)){
				type = 1;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String endTime = sdf.format(new Date());
			bpmUserTaskService.updateApprovalStatusAndTime(bpmUserTask.getId(),approvalStatus,endTime);
			bpmUserTaskService.updateApprovalStatusOnTable(tableName,bpmUserTask.getBusinessKey(),type,approvalStatus);
		}
	}
}
