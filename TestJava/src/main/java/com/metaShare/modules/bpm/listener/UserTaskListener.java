package com.metaShare.modules.bpm.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

import com.metaShare.common.utils.SessionHelper;
import com.metaShare.common.utils.SpringUtils;
import com.metaShare.modules.sys.entity.SysPostManage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysPostManageService;
import com.metaShare.modules.sys.service.SysUserService;

/**
 * 根据部门职务查找用户信息
 *
 */
public class UserTaskListener implements TaskListener{
	
	
	private FixedValue posit;//职务id
	
	private FixedValue orgId; //部门ID

	/**
	 * 根据职务获取人员信息
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {
//		String postId = this.getPosit().getValue(delegateTask).toString();
//		String deptId = this.getDeptId(delegateTask);
//		SysPostManageService postManageService = SpringUtils.getBean("sysPostManageService");
//		List<SysPostManage> postList = postManageService.findByOrgAndPost(deptId,postId);
//		List<String> list = new ArrayList<String>();
//		for(SysPostManage po:postList){
//			list.add(getLoginName(po.getUserId()));
//		}
//
//		if(list.size()>0){
//			delegateTask.setVariable(delegateTask.getTaskDefinitionKey()+"Assignee", list.get(0));
//		}
	}

	
	
	/**
	 * 获取职务信息
	 * @param delegateTask 
	 * @return
	 */
	private String getDeptId(DelegateTask delegateTask) {
		String orgId = null;
		if(null ==  this.getOrgId() || "" .equals(this.getOrgId())){
			orgId = this.getUser().getOrgId();
		}else{
			orgId = this.getOrgId().getValue(delegateTask).toString();
		}
		return orgId;
	}

	//以下为get、set方法
	public FixedValue getPosit() {
		return posit;
	}

	public void setPosit(FixedValue posit) {
		this.posit = posit;
	}
	
	public FixedValue getOrgId() {
		return orgId;
	}

	public void setOrgId(FixedValue orgId) {
		this.orgId = orgId;
	}
	
	private SysUser getUser(){
		return SessionHelper.getUser();
	}

	private String getLoginName(String userId){
		SysUserService userService = SpringUtils.getBean(SysUserService.class);
		SysUser user = userService.selectById(userId);
		return user.getLoginName();
	}
}
