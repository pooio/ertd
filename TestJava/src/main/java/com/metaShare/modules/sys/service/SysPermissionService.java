package com.metaShare.modules.sys.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysPermissionDao;
import com.metaShare.modules.sys.entity.SysPermission;

@Service
public class SysPermissionService extends ServiceImpl<SysPermissionDao,SysPermission> {
	@Autowired
	SysPermissionDao permissionDao;
	
	/**
	 * 根据角色查询权限信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月15日 上午11:06:00
	 */
	public List<SysPermission> findByRole(String roleId){
		return permissionDao.findByRole(roleId);
	}

	/**
	 * 授权字符串是否可用
	 * @param permissionStr 授权字符串
	 * @param id id
	 * @return
	 * @throws Exception 
	 */
	public Boolean checkcheckPermissionStr(String permissionStr, String id) {
		Boolean flag = true;
		Wrapper wrapper = Condition.create().eq("deleted", false);
		if (StringUtils.isNoneEmpty(permissionStr)) {
			wrapper.eq("permission_str", permissionStr);
		}
		//如果是修改要排除要检查的用户
		if(StringUtils.isNotEmpty(id)){
			wrapper.ne("id", id);
		}
		int count = permissionDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}

	public List<SysPermission> listByRoleId(Map<String, String> map) {
		return permissionDao.listByRoleId(map);
	}
	
}
