package com.metaShare.modules.bpm.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.entity.SysUserApprovalNode;
import com.metaShare.modules.sys.service.SysUserApprovalService;
import com.metaShare.modules.sys.service.SysUserService;

public class CandidateTaskListener extends DefaultTaskListener {
	  private static Logger logger = LoggerFactory
	            .getLogger(CandidateTaskListener.class);
	  
	@Override
	public void onCreate(DelegateTask delegateTask) throws Exception {
		String taskKey = delegateTask.getTaskDefinitionKey();
		DelegateExecution execution = delegateTask.getExecution();
		
		//动态指定候选用户以及候选组
		String taskCandidateUsers = (String) execution.getVariable(taskKey+"CandidateUsers");
		String taskCandidateGroups = (String) execution.getVariable(taskKey+"CandidateGroups");
		
		//用户指定审批人
//		taskCandidateUsers = this.addSelectUser(delegateTask,taskCandidateUsers);
//		execution.setVariable(taskKey+"CandidateUsers", taskCandidateUsers);
		
		List<String> newUsers = new ArrayList<String>();
		List<String> newGoups = new ArrayList<String>();
		if(StringUtils.isNotEmpty(taskCandidateUsers) || 
				StringUtils.isNotEmpty(taskCandidateGroups)){
			
			if(StringUtils.isNotEmpty(taskCandidateUsers)){
				String[] users = taskCandidateUsers.split(",");
				for(String user : users){
					//if(validateUser(user,delegateTask)){
						newUsers.add(user);
					//}
				}
			}
			if(StringUtils.isNotEmpty(taskCandidateGroups)){
				String[] goups = taskCandidateGroups.split(",");
				for(String group : goups){
					//if(validateGroup(group,delegateTask)){
						newGoups.add(group);
					//}
				}
			}
			
			if(CollectionUtils.isNotEmpty(newUsers)
					|| CollectionUtils.isNotEmpty(newGoups)){
				clearCandidates(delegateTask);
			}
			
			for(String userId : newUsers){
				delegateTask.addCandidateUser(userId);
			}
			for(String groupId : newGoups){
				delegateTask.addCandidateGroup(groupId);
			}
		}
	}

	private String addSelectUser(DelegateTask delegateTask, String taskCandidateUsers) {
		String businessKey = delegateTask.getVariable("businessKey").toString();//业务主键
		SysUserApprovalService userApprovalService = SpringUtils.getBean(SysUserApprovalService.class);
		String processDefinitionId = delegateTask.getProcessDefinitionId();
		List<SysUserApprovalNode> selectUserList = userApprovalService.getSureApproval(businessKey,processDefinitionId.split(":")[0]);
		
		for(SysUserApprovalNode u:selectUserList){
			if(!StringUtils.isEmpty(taskCandidateUsers)){
				taskCandidateUsers += ","+getLoginName(u.getUser());
			}else{
				taskCandidateUsers = getLoginName(u.getUser());
			}
		}
		
		return taskCandidateUsers;
	}

	/**
	 * 验证用户是否存在于候选用户以及候选用户组中
	 * @param user
	 * @param delegateTask
	 * @return 
	 * @return boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月7日 下午4:45:43
	 */
	private boolean validateUser(String user,DelegateTask delegateTask){
		boolean flag = false;
		//验证所指定的负责人是否存在于候选用户以及候选组
		List<Group> list = Context.getCommandContext().getGroupIdentityManager().findGroupsByUser(user);
		Set<IdentityLink> cadidates = delegateTask.getCandidates();
		
		if(CollectionUtils.isNotEmpty(list)){
			for(Group group : list){
				for(IdentityLink identityLink : cadidates){
					if(group.getId().equals(identityLink.getGroupId())){
						flag = true;
						break;
					}
					if(user.equals(identityLink.getUserId())){
						flag = true;
						break;
					}
				}
				if(flag){
					break;
				}
			}
		}
		return flag;
		
	}
	
	/**
	 * 验证用户组标识 是否存在于候选组列表中
	 * @param groupId
	 * @param delegateTask
	 * @return 
	 * @return boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月7日 下午4:46:07
	 */
	private boolean validateGroup(String groupId,DelegateTask delegateTask){
		boolean flag = false;
		//验证所指定的负责人是否存在于候选用户以及候选组
		Set<IdentityLink> cadidates = delegateTask.getCandidates();
		for(IdentityLink identityLink : cadidates){
			if(groupId.equals(identityLink.getGroupId())){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 清楚当前任务的候选用户和候选组
	 * @param delegateTask 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月7日 下午4:46:33
	 */
	private void clearCandidates(DelegateTask delegateTask){
		Set<IdentityLink> cadidates = delegateTask.getCandidates();
		Iterator<IdentityLink> it = cadidates.iterator();  
        while(it.hasNext()){
        	IdentityLink identityLink = it.next();
        	if(StringUtils.isNotEmpty(identityLink.getUserId())){
        		delegateTask.deleteCandidateUser(identityLink.getUserId());
        	}
        	if(StringUtils.isNotEmpty(identityLink.getGroupId())){
        		delegateTask.deleteCandidateGroup(identityLink.getGroupId());
        	}
        }  
	}
	
	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}

}
