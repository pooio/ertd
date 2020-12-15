package com.metaShare.modules.bpm.listener;

import org.activiti.engine.impl.el.FixedValue;

/**
 * 根据申请人所在部门查找职务头衔的用户
 *
 */
public class ApplyDeptListener {
	
	private FixedValue posit;//职务id

	/**
	 * 根据职务获取人员信息
	 */
	private static final long serialVersionUID = 1L;

//	@Override
//	public void notify(DelegateTask delegateTask) {
////		String postId = this.getPosit().getValue(delegateTask).toString();
////		String deptId = this.getOrgan(delegateTask.getVariable(BpmConstants.APPLY_USER).toString());
////
////		SysPostManageService postManageService = SpringUtils.getBean("sysPostManageService");
////		List<SysPostManage> postList = postManageService.findByOrgAndPost(deptId,postId);
////		List<String> list = new ArrayList<String>();
////		for(SysPostManage po:postList){
////			list.add(getLoginName(po));
////		}
////
////		if(list.size()>0){
////			delegateTask.setVariable(delegateTask.getTaskDefinitionKey()+"Assignee", list.get(0));
////		}
//	}
//
//	/**
//	 * 获取申请人登录名称
//	 * @param variable
//	 * @return
//	 * @throws Exception
//	 */
//	private String getLoginName(SysPostManage po) {
////		SysUserService userService = SpringUtils.getBean(SysUserService.class);
////		SysUser u = userService.selectById(po.getUserId());
////		return u.getLoginName();
//		return "";
//	}
//
//	/**
//	 * 获取申请人所在部门
//	 * @param variable
//	 * @return
//	 * @throws Exception
//	 */
//	private String getOrgan(String loginName) {
//		SysUser u = null;
//		try {
//			SysUserService userService = SpringUtils.getBean(SysUserService.class);
//			u = userService.findByLoginName(loginName);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return u.getOrgId();
//	}
//
//	//以下为get、set方法
//	public FixedValue getPosit() {
//		return posit;
//	}
//
//	public void setPosit(FixedValue posit) {
//		this.posit = posit;
//	}
}
