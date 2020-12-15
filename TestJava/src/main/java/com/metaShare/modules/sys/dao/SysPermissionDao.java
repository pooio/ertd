package com.metaShare.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

public interface SysPermissionDao extends BaseMapper<SysPermission> {
	
	/**
	 * 根据角色id获取所有的授权信息
	 * @param roleId
	 * @return 
	 * @return List<Permission>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:48:55
	 */
	 List<SysPermission> findByRole(String roleId);

	List<SysPermission> listByRoleId(Map<String, String> map);
}
