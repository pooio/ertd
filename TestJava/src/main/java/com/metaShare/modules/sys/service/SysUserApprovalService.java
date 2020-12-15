package com.metaShare.modules.sys.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysOrganizationDao;
import com.metaShare.modules.sys.dao.SysUserApprovalDao;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.entity.SysUserApprovalNode;


@Service
public class SysUserApprovalService extends ServiceImpl<SysUserApprovalDao, SysUserApprovalNode>{
	private static final long serialVersionUID = 1L;
	@Autowired
	protected SysUserApprovalDao userApprovalDao;
	
	//保存下级审批人
	public void saveApprovalUser(String nextProcessId, String nextBusinessId, String nextSpprovalUsers, String processFlag) {
		List<SysUserApprovalNode> approvalAllList = userApprovalDao.getApproval(nextProcessId,nextBusinessId,processFlag);
		
		//确定审批人
		if(nextSpprovalUsers.startsWith("[")){
			nextSpprovalUsers = nextSpprovalUsers.substring(1);
		}
		if(nextSpprovalUsers.endsWith("]")){
			nextSpprovalUsers = nextSpprovalUsers.substring(0,nextSpprovalUsers.length()-1);
		}
		if(nextSpprovalUsers.length()>0){
			String[] nextSpprovalIds = nextSpprovalUsers.split(",");
			boolean isEquals = false;
			for(SysUserApprovalNode u:approvalAllList){
				isEquals = false;
				for(String id:nextSpprovalIds){
					if(id.trim().equals(u.getId().trim())){
						u.setApprovalOrNo("1");
						userApprovalDao.updateAllColumnById(u);
						isEquals = true;
						break;
					}
				}
				if(!isEquals){
					u.setDeleted(true);
					this.updateAllColumnById(u);
				}
			}
		}
	}
	
	/**
	 * 获取选定审批人
	 * @param businessKey
	 * @return
	 */
	public List<SysUserApprovalNode> getSureApproval(String businessKey,String processId) {
		return userApprovalDao.getSureApproval(businessKey,processId);
	}
	
	/**
	 * 获取 是否还有为审批的用户
	 * @param businessId 业务主键
	 * @param userId 用户ID
	 * @param processId_ 
	 * @return
	 */
	public Boolean isNextNode(String businessId,String userId, String processId_) {
		Boolean flag = true;//是否到下一节点
		List<SysUserApprovalNode> list = userApprovalDao.getSureApproval(businessId,processId_);
		for(SysUserApprovalNode u:list){
			if(userId != u.getUser()){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 审批流程用户已审批
	 * @param businessId
	 * @param userId
	 */
	public void updateApproval(String businessId,String userId) {
		List<SysUserApprovalNode> list = userApprovalDao.getApprovalUser(businessId,userId);
		List<String> idsList  = new ArrayList<String>();
		for(SysUserApprovalNode node:list){
			idsList.add(node.getId());
		}
		userApprovalDao.deleteBatchIds(idsList);
	}
	
	/**
	 * 是否有确定审批人
	 * @param businessId 业务ID
	 * @param processId 
	 * @return
	 */
	public Boolean isSureApproval(String businessId, String processId) {
		List<SysUserApprovalNode> list = userApprovalDao.getSureApproval(businessId,processId);
		if(list.size()>0){
			return true;
		}
		return false;
	}
	
	
	public void deletechoseInfo(String businessKey, String processDefinitionKey) {
		userApprovalDao.deletechoseInfo(businessKey,processDefinitionKey);
	}

}
