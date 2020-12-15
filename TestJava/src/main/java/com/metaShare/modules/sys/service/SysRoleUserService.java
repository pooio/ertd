package com.metaShare.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRoleUserDao;
import com.metaShare.modules.sys.entity.SysRoleUser;

@Service
public class SysRoleUserService extends ServiceImpl<SysRoleUserDao,SysRoleUser>{
	@Autowired
	protected SysRoleUserDao roleUserDao;
	
	/**
	 * 根据用户id删除
	 * @param userIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @date: 2016年1月20日 下午7:06:57
	 */
	public void deleteByUserIds (List<String> userIds) {
		roleUserDao.deleteBatchIds(userIds);
	}
	
	/**
	 * 根据用户id获取用户角色对应数据
	 * @param userId
	 * @return 
	 * @return List<RoleUser>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月15日 下午9:45:47
	 */
	public List<String> findRoleIdsByUser(String userId){
		return roleUserDao.findByUserId(userId);
	}

	public void deleteByRoleId(String id) {
		roleUserDao.deleteByRoleId(id);
	}

    public SysRoleUser getByRoleIdAndUserId(String roleId, String userId) {
		return roleUserDao.getByRoleIdAndUserId(roleId,userId);
    }
}
