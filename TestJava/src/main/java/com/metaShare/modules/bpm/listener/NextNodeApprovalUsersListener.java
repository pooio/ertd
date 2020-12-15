package com.metaShare.modules.bpm.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.entity.SysUserApprovalNode;
import com.metaShare.modules.sys.service.SysUserApprovalService;
import com.metaShare.modules.sys.service.SysUserService;


/**
 * 获取审批人
 *
 */
public class NextNodeApprovalUsersListener implements TaskListener{

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		//参数
		String businessKey = delegateTask.getVariable("businessKey").toString();//业务主键
		String processId = delegateTask.getProcessDefinitionId().toString();
		
		//构建下一级审批用户并保存
		SysUserApprovalService userApprovalService = SpringUtils.getBean(SysUserApprovalService.class);		
		List<SysUserApprovalNode> postList = userApprovalService.getSureApproval(businessKey,processId.split(":")[0]);
		List<String> list = new ArrayList<String>();
		for(SysUserApprovalNode userApprovalNode:postList){
			list.add(userApprovalNode.getUser());
		}
		
		if(list.size()>0){
			delegateTask.setVariable(delegateTask.getTaskDefinitionKey()+"Assignee", list.get(0));
		}
	}
	
	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}
}
