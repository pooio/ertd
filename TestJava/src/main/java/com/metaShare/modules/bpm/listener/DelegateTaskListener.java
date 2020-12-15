package com.metaShare.modules.bpm.listener;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Lists;
import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.bpm.entity.BpmDelegate;
import com.metaShare.modules.bpm.service.BpmDelegateService;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;

public class DelegateTaskListener extends DefaultTaskListener implements InitializingBean {
	  private static Logger logger = LoggerFactory
	            .getLogger(DelegateTaskListener.class);
	  @Override
	  public void afterPropertiesSet() throws Exception {
	  }
	@Override
	public void onCreate(DelegateTask delegateTask) throws Exception {
		String taskKey = delegateTask.getTaskDefinitionKey();
		DelegateExecution execution = delegateTask.getExecution();
		String processDefineId = execution.getProcessDefinitionId();
		String processDefineKey = processDefineId.split(":")[0];
		
		//说明：如果某个审批人在某一段时间内，由于出差等原因无法及时审批，
		//     需要将自己的待办的审批任务委托给另外一个人，由这个人代为审批，这就是任务委托
		//     这里将根据用户的配置，将任务委托给指定人
		String loginName = delegateTask.getAssignee();
		if(StringUtils.isNotEmpty(loginName)){
			
			//当前任务节点有负责人的情况下
			BpmDelegateService bpmDelegateService = SpringUtils.getBean("bpmDelegateService");
			List<BpmDelegate> delegates = bpmDelegateService.findByLoginName(loginName,processDefineKey);
			
			String delegateLoginName = "";
			Integer delegateType = 0;
			for (BpmDelegate delegate : delegates) {
				delegateLoginName = getLoginName(delegate.getDelegate());
				delegateType = delegate.getDelegateType();
				break;
			}
			
			if (StringUtils.isNotEmpty(delegateLoginName)) {
				
				if(delegateType==0){
					//委托
					logger.info("流程定义：{},流程实例：{},任务:{},自动委派给：{}",
							processDefineId,delegateTask.getProcessInstanceId(),taskKey,delegateLoginName);
					delegateTask.setOwner(loginName);
					
					//只是将任务交给委托人
					//delegateTask.setAssignee(delegateLoginName);
					//移除原来的候选人
					clearCandidates(delegateTask);
					
					//将任务同时交于原负责人和委托人
					delegateTask.addCandidateUser(loginName);
					delegateTask.addCandidateUser(delegateLoginName);
					delegateTask.setAssignee(null);
					
				}    
				if(delegateType==1){
					//协作
					logger.info("流程定义：{},流程实例：{},任务:{},自动发起协作给：{}",
							processDefineId,delegateTask.getProcessInstanceId(),taskKey,delegateLoginName);
					TaskService taskService = SpringUtils.getBean("taskService");
					taskService.delegateTask(delegateTask.getId(), delegateLoginName);
				}
			}
		}else{
			//如果当前任务没有负责人，也就是为候选组合候选用户中的人设置委托人
			Set<IdentityLink> cadidates = delegateTask.getCandidates();
			List<String> roleCodes = Lists.newArrayList();
			List<String> loginNames = Lists.newArrayList();
			for(IdentityLink identityLink : cadidates){
				if(StringUtils.isNotEmpty(identityLink.getGroupId())){
					roleCodes.add(identityLink.getGroupId());
				}
				if(StringUtils.isNotEmpty(identityLink.getUserId())){
					loginNames.add(identityLink.getUserId());
				}
			}
			if(CollectionUtils.isNotEmpty(roleCodes) || CollectionUtils.isNotEmpty(loginNames)){
				SysUserService userService =  SpringUtils.getBean("sysUserService");
				List<SysUser> users = userService.findByRoleCodesOrLoginNames(roleCodes, loginNames);
				List<String> allLoginNames = Lists.newArrayList();
				for(SysUser user : users){
					allLoginNames.add(user.getLoginName());
				}
				
				BpmDelegateService bpmDelegateService = SpringUtils.getBean("bpmDelegateService");
				List<BpmDelegate> delegates = bpmDelegateService.findByLoginName(allLoginNames,processDefineKey);
				
				List<String> addUsers = Lists.newArrayList();
				String delegateLoginName = "";
				Integer delegateType = 0;
				for (BpmDelegate delegate : delegates) {
					delegateLoginName = getLoginName(delegate.getDelegate());
					delegateType = delegate.getDelegateType();
					if(delegateType==0){
						addUsers.add(getLoginName(delegate.getDelegate()));
					}
				}
				
				if (StringUtils.isNotEmpty(delegateLoginName)) {
					if(addUsers.size()>0){
						delegateTask.addCandidateUsers(addUsers);
					}  
				}
			}
			
		}
		
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
	
	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}
}
