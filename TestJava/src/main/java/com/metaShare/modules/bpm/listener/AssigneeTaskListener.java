package com.metaShare.modules.bpm.listener;

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


public class AssigneeTaskListener extends DefaultTaskListener {
	  private static Logger logger = LoggerFactory
	            .getLogger(AssigneeTaskListener.class);
	  
	@Override
	public void onCreate(DelegateTask delegateTask) throws Exception {
		String taskKey = delegateTask.getTaskDefinitionKey();
		DelegateExecution execution = delegateTask.getExecution();
		
		//说明：例如键值为HRTask的任务节点，只设置了候选审批人为角色Leader,
		//     如果要再提交流程的时候动态指定HRTask任务节点的审批人为Leader角色中的某一个,
		//     则需要在提交流程的时候把Leader角色中某个人的登录名（zhangsan）,
		//     存入流程变量HRTaskAssignee（命名规则：任务key+Assignee）,
		//     那么zhagnsan则成了HRTask任务的实际负责人也就是审批人
		String taskAssigee = (String) execution.getVariable(taskKey+"Assignee");
		if("" == taskAssigee || null == taskAssigee){
			taskAssigee = delegateTask.getAssignee();
		}
		
		//如果对应节点设置了默认审批人，验证设置的人是否在该任务的所分配的组中，如果在，则设置为默认审批人
		if(StringUtils.isNotEmpty(taskAssigee)){
			//if(validateUser(taskAssigee,delegateTask)){
				logger.info("流程定义：{},流程实例：{},任务:{},自动设置审批人为：{}",
						execution.getProcessDefinitionId(),delegateTask.getProcessInstanceId(),taskKey,taskAssigee);
				//移除原来的候选人
				clearCandidates(delegateTask);
				delegateTask.setAssignee(taskAssigee);
			//}
		}
		
	}
	
	/**
	 * 验证用户id是否存在于候选组或者候选人列表中
	 * @param user
	 * @param delegateTask
	 * @return 
	 * @return boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年4月7日 下午4:43:40
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
	
	private String addSelectUser(DelegateTask delegateTask, String taskCandidateUsers) {
		String businessKey = delegateTask.getVariable("businessKey").toString();//业务主键
		SysUserApprovalService userApprovalService = SpringUtils.getBean(SysUserApprovalService.class);
		
		List<SysUserApprovalNode> selectUserList = userApprovalService.getSureApproval(businessKey,delegateTask.getProcessDefinitionId().split(":")[0]);
		
		for(SysUserApprovalNode u:selectUserList){
			if(!StringUtils.isEmpty(taskCandidateUsers)){
				taskCandidateUsers += ","+getLoginName(u.getUser());
			}else{
				taskCandidateUsers = getLoginName(u.getUser());
			}
		}
		
		return taskCandidateUsers;
		
	}
	
	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}

}
