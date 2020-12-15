package com.metaShare.modules.sys.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysResourceDao;
import com.metaShare.modules.sys.entity.SysResource;

@Service
public class SysResourceService extends ServiceImpl<SysResourceDao,SysResource> {
	private static Logger logger = LoggerFactory.getLogger(SysResourceService.class);
	@Autowired
	private SysResourceDao resourceDao;
	
	/**
	 * 获得根节点
	 */
	public SysResource findRootResource(){
		Wrapper wrapper = Condition.create().isNull("parent_id");
		List<SysResource> list = resourceDao.selectList(wrapper);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据名称获取资源
	 */
	public SysResource findByName(String name){
		Wrapper wrapper = Condition.create().eq("name", name);
		List<SysResource> list = resourceDao.selectList(wrapper);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取菜单资源树形信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月8日 下午4:31:13
	 */
	public List<SysResource> treeData(String roleId){
		return resourceDao.treeData();
	}
	
	/**
	 * 根据角色获取资源树形信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月13日 下午9:02:59
	 */
	public List<SysResource> findByRole(String roleId){
		if (roleId.length()>0 && roleId.endsWith(",")) {
			roleId = roleId.substring(0, roleId.length()-1);
		}
		
		String[] roleIds = roleId.split(",");
		return resourceDao.findByRole(roleIds);
	}
	
	/**
	 * 根据id获取所有的子元素
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月8日 下午4:30:33
	 */
	public List<SysResource> findChildren(String id){
		return resourceDao.findChildren(id);
	}

	/**
	 * 名字是否可用
	 * @param menuName 菜单名称
	 * @param menuId 菜单ID
	 * @param menuParentId 菜单父ID
	 * @return
	 */
	public Boolean checkMenuName(String menuName, String menuId, String menuParentId,String type) {
		Boolean flag = true;
		SysResource sysResource  = resourceDao.checkMenuName(menuName,menuParentId,type);
		if(sysResource != null){
            if(StringUtils.isNotEmpty(menuId)){
                if(!sysResource.getId().equals(menuId)){
                    flag = false;
                }
            }else{
            	flag = false;
			}
        }
		/*if("0".equals(type)){
			Wrapper wrapper = Condition.create().eq("deleted", false)
					.eq("name", menuName)
					.eq("parent_id", menuParentId);

			//如果是修改要排除要检查的项目
			if(StringUtils.isNotEmpty(menuId)){
				wrapper.ne("id", menuId);
			}
			int count = resourceDao.selectCount(wrapper);
			if(count>0){
				flag = false;
			}
		}else if("1".equals(type)){
			Wrapper wrapper = Condition.create().eq("deleted", false)
					.eq("btn_type", menuName);
			int count = resourceDao.selectCount(wrapper);
			if(count>0){
				flag = false;
			}
		}*/
		return flag;
	}
	
	public Boolean checkURL(String menuName, String menuId, String menuParentId) {
	    SysResource sysResource = resourceDao.checkURL(menuName,menuParentId);
		Boolean flag = true;
		if(sysResource != null){
			if(StringUtils.isNotEmpty(menuId)){
				if(!sysResource.getId().equals(menuId)){
					flag = false;
				}
			}else{
				flag = false;
			}

        }

		/*Wrapper wrapper = Condition.create().eq("deleted", false)
				.eq("url", menuName)
				.eq("parent_id", menuParentId);
		
		//如果是修改要排除要检查的项目
		if(StringUtils.isNotEmpty(menuId)){
			wrapper.ne("id", menuId);
		}
		int count = resourceDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}*/
		return flag;
	}
	
	/**
	 * 获得1级菜单信息
	 * @return
	 */
	public SysResource selectTopResource() {
		return resourceDao.selectTopResource();
	}

	/**
	 * 获取2级菜单列表
	 * @param id 1级菜单Id
	 * @return
	 */
	public List<SysResource> selectChildrenResources(String id,String[] roleIds,String name,String type) {
		return resourceDao.selectChildrenResources(id,roleIds,name,type);
	}

	/**
	 * 获取角色拥有的按钮权限
	 * @param roleIds
	 * @return
	 */
	public List<SysResource> selectRolesBtn(String[] roleIds) {
		return resourceDao.selectRolesBtn(roleIds);
	}

	public SysResource selectBtnByName(String btnName) {
		return resourceDao.selectBtnByName(btnName);
	}

	public List<SysResource> getRolesBtn(String id) {
		return resourceDao.getRolesBtn(id);
	}


	public List<String> selectAllBtnId() {
		return resourceDao.selectAllBtnId();
	}

	public List<String> getResourceListByRoleId(String roleId) {
		return resourceDao.getResourceListByRoleId(roleId);
	}

	/**
	 * 获取对应角色已授权的菜单和已存在的按钮
	 * @param id
	 * @param resourceId
	 * @return
	 */
	public List<SysResource> selectChildrenResourcesAuthorized(String id, String[] resourceId) {
		return resourceDao.selectChildrenResourcesAuthorized(id,resourceId);
	}

	public List<String> getAllResourceId() {
		return resourceDao.getAllResourceId();
	}
}
