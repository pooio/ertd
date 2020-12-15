package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.entity.SysRoleOrg;
import com.metaShare.modules.sys.entity.SysRoleResource;
public interface SysRoleDao extends BaseMapper<SysRole> {
	
	/**
	 * 根据用户id获取角色列表
	 * @param userId
	 * @return 
	 * @return List<Role>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:46:52
	 */
	public List<SysRole> findByUser(@Param("userId") String userId);
	
	/**
	 * 角色类型ID查询角色信息
	 * @param typeId 角色类型ID
	 * @return
	 */
	public List<SysRole> selectListByTypeId(@Param("orgName") String orgName,@Param("orgCode")String orgCode);
	
	public List<SysRole> allList();
	
	/**
	 * 角色类型ID查询角色信息
	 * @param typeId 角色类型ID
	 * @return
	 */
	public List<SysRoleResource> getRoleResource(@Param("roleId") String roleId);
	/**
	 * 角色类型ID查询角色信息
	 * @param typeId 角色类型ID
	 * @return
	 */
	public List<SysRoleOrg> getRoleOrg(@Param("roleId") String roleId);
	
}
