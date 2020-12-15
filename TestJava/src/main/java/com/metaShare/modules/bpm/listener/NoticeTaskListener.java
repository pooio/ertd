package com.metaShare.modules.bpm.listener;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.google.common.collect.Lists;
import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.bpm.BpmConstants;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysMessageService;
import com.metaShare.modules.sys.service.SysUserService;


public class NoticeTaskListener extends DefaultTaskListener {
	private static Logger logger = LoggerFactory
            .getLogger(NoticeTaskListener.class);
	
    @Override
    public void onCreate(DelegateTask delegateTask) throws Exception {
//    	SysMessageService messageService = SpringUtils.getBean("sysMessageService");
//    	SysUserService userService = SpringUtils.getBean("sysUserService");
//    	SysUser user = userService.findByLoginName(delegateTask.getAssignee());
//    	SysMessage message = new SysMessage();
//    	message.setSubject("新任务到达");
//    	message.setContent(user.getName()+"您好，有新任务需要处理。");
//    	message.setStatus(0L);
//    	message.setReceiver(user.getId());
//    	String taskId = delegateTask.getId();
//    	message.setUrl("/bpm/task/message?taskId="+taskId);
//    	message.setCreateTime(new Date());
//    	messageService.save(message);
//        CacheManager cacheManager =  SpringUtils.getBean("cacheManager");
//        Cache noticeCache = cacheManager.getCache("noticeCache");
//        List<SysUser> users = getUsers(delegateTask);
//        for(SysUser user1 : users) {
//            noticeCache.put(user1.getLoginName()+":todo", true);
//        }
        
    }

    @Override
    public void onComplete(DelegateTask delegateTask) throws Exception {
    	String name = Context.getCommandContext().getProcessDefinitionEntityManager().findProcessDefinitionById(delegateTask.getProcessDefinitionId()).getName();
    	SysMessageService messageService = SpringUtils.getBean("sysMessageService");
    	SysUserService userService = SpringUtils.getBean("sysUserService");
    	Map<String, Object> variables = delegateTask.getVariables();
    	SysUser applyUser = userService.findByLoginName((String)variables.get(BpmConstants.APPLY_USER));
    	SysMessage message = new SysMessage();
    	String msgURL = (String)variables.get(BpmConstants.MESSAGE_URL);
    	if(StringUtils.isNotEmpty(msgURL)){
    		message.setUrl(msgURL);
    	}
    	message.setSubject(name);
    	//message.setContent(applyUser.getName()+"您好，您的流程<b>"+name+"</b>已完成<b>"+delegateTask.getName()+"</b>环节。");
    	message.setContent("您好，您的流程"+name+"已完成"+delegateTask.getName()+"环节。");
    	message.setReceiver(applyUser.getId());
    	message.setStatus(0L);
    	message.setCreateTime(new Date());
    	messageService.save(message);
    }
    
    private  List<SysUser> getUsers(DelegateTask delegateTask){
    	SysUserService userService = SpringUtils.getBean("sysUserService");
        List<SysUser> users = Lists.newArrayList();
        List<String> roleCodes = Lists.newArrayList();
        List<String> loginNames = Lists.newArrayList();
        for(IdentityLink identityLink : delegateTask.getCandidates()){
            if(StringUtils.isNotEmpty(identityLink.getGroupId())){
                roleCodes.add(identityLink.getGroupId());
            }
            if(StringUtils.isNotEmpty(identityLink.getUserId())){
                loginNames.add(identityLink.getUserId());
            }
        }
        if(CollectionUtils.isNotEmpty(roleCodes) || CollectionUtils.isNotEmpty(loginNames)){
            users = userService.findByRoleCodesOrLoginNames(roleCodes, loginNames);
        }
        return users;
    }
}
