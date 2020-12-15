package com.metaShare.modules.bpm.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.sys.entity.SysPostManage;
import com.metaShare.modules.sys.entity.SysUserApprovalNode;
import com.metaShare.modules.sys.service.SysPostManageService;
import com.metaShare.modules.sys.service.SysUserApprovalService;

/**
 * 下一节点审批人
 *
 */
public class NextNodeApprovalUserListener implements TaskListener{

	private FixedValue nextRoleCode;//下一节点角色编码

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		//参数
		String postId = this.getNextRoleCode().getValue(delegateTask).toString();//下一节点角色ID
		String id = delegateTask.getId();//节点ID
		String businessKey = delegateTask.getVariable("businessKey").toString();//业务主键
		String processDefinitionKey =delegateTask.getExecution().getProcessDefinitionId();
		processDefinitionKey = processDefinitionKey.split(":")[0];
		
//		//构建下一级审批用户并保存
//		SysPostManageService postManageService = SpringUtils.getBean("sysPostManageService");
//		SysUserApprovalService userApprovalService = SpringUtils.getBean(SysUserApprovalService.class);
//		List<SysPostManage> postList = postManageService.findByOrgAndPost(postId);
//		for(SysPostManage po:postList){
//			SysUserApprovalNode userNode = new SysUserApprovalNode();
//			userNode.setBusinessId(businessKey);
//			userNode.setProcessId(id);
//			userNode.setUser(po.getUserId());
//			userNode.setApprovalOrNo("0");
//			userNode.setProcessFlag(processDefinitionKey.toString());
//			userApprovalService.insert(userNode);
//		}
	}

	public FixedValue getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(FixedValue nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
}
