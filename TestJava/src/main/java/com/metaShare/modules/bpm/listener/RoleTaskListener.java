package com.metaShare.modules.bpm.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.sys.entity.SysRoleUser;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysRoleService;
import com.metaShare.modules.sys.service.SysUserService;

/**
 * 根据角色查询用户信息
 *
 */
public class RoleTaskListener implements TaskListener{

	private FixedValue roleCode;//角色表示

	/**
	 * 根据职务获取人员信息
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
		String postId = this.getRoleCode().getValue(delegateTask).toString();
		
		SysRoleService roleService = SpringUtils.getBean(SysRoleService.class);		
		List<SysRoleUser> roleList = roleService.getRoleUserByRoleCode(postId);
		List<String> list = new ArrayList<String>();
		for(SysRoleUser po:roleList){
			list.add(getLoginName(po.getUserId()));
		}
		
		if(list.size()>0){
			delegateTask.setVariable(delegateTask.getTaskDefinitionKey()+"Assignee", list.get(0));
		}
	}

	public FixedValue getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(FixedValue roleCode) {
		this.roleCode = roleCode;
	}
	
	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}
}
