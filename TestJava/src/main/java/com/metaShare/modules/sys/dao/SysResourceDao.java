package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysResource;

public interface SysResourceDao extends BaseMapper<SysResource> {
	
	 /**
	  * 按照树形结构层次返回所有的菜单信息
	  * @return 
	  */
	 List<SysResource> treeData();
	 
	 /**
	  * 根据角色id获取所有的Resource
	  * @param roleId
	  * @return 
	  * @return List<Resource>
	  */
	 List<SysResource> findByRole(String[] roleId);
	 
	 
	 /**
	  * 根据id获取所有的子记录
	  * @param id
	  * @return 
	  * @return List<Resource>
	  */
	 List<SysResource> findChildren(@Param("id") String id);


	/**
	 * 获取1级菜单
	 * @return
	 */
	 SysResource selectTopResource();


	/**
	 * 获取2级菜单列表
	 * @param id
	 * @return
	 */
    List<SysResource> selectChildrenResources(@Param("id") String id,@Param("roleIds")String[] roleIds,@Param("name") String name,@Param("type")String type);

	List<SysResource> selectRolesBtn(String[] array);

	SysResource selectBtnByName(String btnName);

	List<SysResource> getRolesBtn(String id);


	List<String> selectAllBtnId();

    List<String> getResourceListByRoleId(@Param("roleId") String roleId);

	List<SysResource> selectChildrenResourcesAuthorized(@Param("id") String id,@Param("array") String[] array);

	List<String> getAllResourceId();


	SysResource checkMenuName(@Param("menuName") String menuName,@Param("menuParentId") String menuParentId,@Param("type") String type);

	SysResource checkURL(@Param("menuName") String menuName,@Param("menuParentId") String menuParentId);
}
