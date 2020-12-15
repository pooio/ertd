package com.metaShare.modules.bpm.util;

import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.User;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.procedure.internal.Util.ResultClassesResolutionContext;

import com.metaShare.common.utils.SpringHelper;
import com.metaShare.modules.sys.entity.SysPostManage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;

public class ProcessUtils {
	
	/**
	 * 为任务节点设置负责人
	 * @param variables 流程变量
	 * @param orgPosts 部门的所有职务信息
	 * @param taskKey 任务节点key
	 * @param postKey  职务标识
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月19日 上午10:09:32
	 */
	public static void setTaskUsers(Map<String, Object> variables,
			Map<String, List<SysPostManage>> orgPosts,String taskKey,String postKey){
		List<SysPostManage>  posts = orgPosts.get(postKey);
		//为任务设置负责人 负责人只有一个
		if( posts!=null && posts.size()==1){
			//variables.put(taskKey+"Assignee", getUser(posts.get(0).getUserId()).getLoginName());
		}
		//设置多个负责人
		if(posts!=null && posts.size()>1 ){
			String[] loginNames = new String[posts.size()];
			int index = 0;
			for(SysPostManage post : posts){
				//loginNames[index] = getUser(post.getUserId()).getLoginName();
				index ++;
			}
			String loginNamesStr = StringUtils.join(loginNames,",");
			if(StringUtils.isNotEmpty(loginNamesStr)){
				variables.put(taskKey+"CandidateUsers", loginNamesStr);
			}
		}
	}
	
	private static SysUser getUser(String userId){
		SysUserService userService = SpringHelper.getBeanByBeanname("SysUserService");
		return userService.selectById(userId);
	}
	
}
