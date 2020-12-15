package com.metaShare.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.sys.dao.SysRoleDao;
import com.metaShare.modules.sys.entity.SysDataFilter;
import com.metaShare.modules.sys.entity.SysPermission;
import com.metaShare.modules.sys.entity.SysResource;
import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.entity.SysRoleDataFilter;
import com.metaShare.modules.sys.entity.SysRoleOrg;
import com.metaShare.modules.sys.entity.SysRolePermission;
import com.metaShare.modules.sys.entity.SysRoleResource;
import com.metaShare.modules.sys.entity.SysRoleUser;
import com.metaShare.modules.sys.entity.SysUser;

@Service
public class SysRoleService extends ServiceImpl<SysRoleDao,SysRole> {
	private static Logger logger = LoggerFactory.getLogger(SysRoleService.class);
	@Autowired
	private SysRoleDao roleDao; 
	@Autowired
	private SysResourceService resourceService;
	@Autowired
	SysRoleResourceService roleResourceService;
	@Autowired
	SysPermissionService permissionService;
	@Autowired
	SysRolePermissionService rolePermissionService;
	@Autowired
	SysRoleDataFilterService roleDataFilterService;
	
	@Autowired
	SysRoleUserService roleUserService;
	@Autowired
	SysUserService userService;
	
	@Autowired
	SysRoleOrgService sysRoleOrgService;
	
	
	public  List<SysRole> allList(){
		 return roleDao.allList();
	}
	
	
	/**
	 * 检查角色编码是否可用
	 * @param
	 * @param
	 * @return 
	 * @return Boolean
	 */
	public Boolean checkRoleCode(String roleCode,String excludeRoleId){
		Boolean flag = true;
		Wrapper wrapper = Condition.create().eq("deleted", false).eq("role_code", roleCode);
		
		//如果是修改要排除要检查的角色
		if(StringUtils.isNotEmpty(excludeRoleId)){
			wrapper.ne("id", excludeRoleId);
		}
		
		int count = roleDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 根据用户id查询拥有的角色信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 下午4:37:53
	 */
	public List<SysRole> findByUser(String userId){
		return roleDao.findByUser(userId);
	}
	
	
	/**
	 * 为角色添加菜单
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月14日 上午9:16:51
	 */
	public void saveMenusForRole(String roleId,String[] menuIds){
		//查询出menuIds所对应的菜单，即前台勾选的菜单
		List<SysResource> menus = new ArrayList<>();
		if(menuIds.length != 0){
			@SuppressWarnings("unchecked")
			Wrapper<SysResource> resourceWrapper = Condition.create().eq("deleted", false)
					.in("id", menuIds);
			menus =  resourceService.selectList(resourceWrapper);
		}

		//查询出roleId对应的所有的菜单
		Wrapper roleResourcerapper = Condition.create().eq("deleted", false)
				.eq("role_id", roleId);
		List<SysRoleResource> roleResources = roleResourceService.selectList(roleResourcerapper);
		
		//获得需要删除的菜单
		//List<String> deletedList = getDeletedRoleResources(menus,roleResources);
		List<String> deletedList = resourceService.getAllResourceId();


		//获得新增的RoleResource
		SysRole role = roleDao.selectById(roleId);
		List<SysRoleResource> newList = getNewRoleResources(role, menus, roleResources);
		if (deletedList.size()>0) {
			//删除对应菜单
			String[] allResourceId = deletedList.toArray(new String[deletedList.size()]);
			roleResourceService.deleteOnRoleIdAndResArr(roleId,allResourceId);
		}
		if (newList.size()>0) {
			roleResourceService.insertBatch(newList);
			saveMenusPermissionsForRole(role,newList);
		}
	}
	/**
	 * 为新增菜单添加对应权限
	 * @param role
	 * @param newList
	 */
	public void saveMenusPermissionsForRole(SysRole role,List<SysRoleResource> newList){
		List<String> permissionUrls = new ArrayList<>(newList.size());
		for (SysRoleResource roleResource : newList) {
			SysResource resource = resourceService.selectById(roleResource.getResourceId());
			String url = resource.getUrl();
			if (StringUtils.isEmpty(url)) {
				continue;
			}
			url = url.replaceAll("\\/", "\\:");
			url = url.substring(1, url.length());
			url = url+":*";
			permissionUrls.add(url);
		}
		
		Wrapper<SysPermission> permissionWrapper = Condition.create().eq("deleted", false).in("permission_str", permissionUrls);
		List<SysPermission> permissions =  permissionService.selectList(permissionWrapper);
		List<String> permissionsIds = new ArrayList<String>();
		for(SysPermission per:permissions){
			permissionsIds.add(per.getId());
		}
		
		Wrapper<SysRolePermission> rolePermissionWrapper = Condition.create()
				.eq("deleted", false)
				.in("permission_id", permissionsIds)
				.eq("role_id", role.getId());
		
		List<SysRolePermission> rolePermissions = rolePermissionService.selectList(rolePermissionWrapper);
		
		List<SysRolePermission> list = getCreateRolePermission(role,permissions,rolePermissions);
		if (list.size()>0) {
			rolePermissionService.insertBatch(list);
		}
	}
	
	
	/**
	 * 为角色新增授权信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月15日 上午10:58:47
	 */
	public void savePermissionsForRole(String roleId,String[] permissionIds){
		Wrapper wrapper = Condition.create().eq("deleted", false).in("id", permissionIds);
		List<SysPermission> permissions =  permissionService.selectList(wrapper);
		
		Wrapper rolePermissionWrapper = Condition.create().eq("deleted", false).in("role_id", roleId);
		List<SysRolePermission> rolePermissions = rolePermissionService.selectList(rolePermissionWrapper);
		SysRole role = roleDao.selectById(roleId);
		rolePermissionService.insertBatch(getCreateRolePermission(role,permissions,rolePermissions));
	}

	
	/**
	 * 获得需要删除的资源
	 * @param menus
	 * @param roleResources
	 * @return 
	 * @return List<RoleResource>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:44:23
	 */
	private List<String> getDeletedRoleResources(List<SysResource> menus,List<SysRoleResource> roleResources){
		//menus前台勾选的菜单，该角色有menus菜单的权限。  roleResource该角色之前拥有的菜单
		List<String> list = new ArrayList<String>();
		for(SysRoleResource roleResource : roleResources){
			boolean flag = false;
			//若该角色之前拥有的菜单在menus里，则不删除，反正则删除该角色对应菜单
			flag =  menus.contains(roleResource.getResourceId());
			//如果roleResource 中的resource不存在于menus中证明已删除
			if(!flag){
				list.add(roleResource.getId());
			}
		}
		return list;
	}
	
	/**
	 * 获取新增的资源
	 * @param role
	 * @param menus
	 * @param roleResources
	 * @return 
	 * @return List<RoleResource>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:44:32
	 */
	private List<SysRoleResource> getNewRoleResources(SysRole role,List<SysResource> menus,List<SysRoleResource> roleResources){
		//之前已经与角色存在关系的Resource
		List<SysResource> oldMenus = new ArrayList<SysResource>();
		List<String> resourceIdList = new ArrayList<String>();
		for(SysRoleResource roleResource : roleResources){
			resourceIdList.add(roleResource.getResourceId());
		}
		if(resourceIdList.size()>0){
			Wrapper wrapper = Condition.create()
					.eq("deleted", false)
					.in("id", resourceIdList);
			oldMenus = resourceService.selectList(wrapper);
		}
		
		List<SysRoleResource> list = new ArrayList<SysRoleResource>();
			for(SysResource menu : menus){
				boolean flag = false;
				flag = oldMenus.contains(menu);
				//如果menu不存在于roleResources则证明为新增的
				if(!flag){
					SysRoleResource roleResourceNew = new SysRoleResource();
					roleResourceNew.setRoleId(role.getId());
					roleResourceNew.setResourceId(menu.getId());
					list.add(roleResourceNew);
				}
			}
		return list;
	}
	
	
	
	/**
	 * 为角色删除授权信息
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月15日 上午11:29:33
	 */
	public void deletePermissionsForRole(String roleId,String[] permissionIds){
		Wrapper permissionWrapper = Condition.create().eq("deleted", false).in("id", permissionIds);
		List<SysPermission> permissions =  permissionService.selectList(permissionWrapper);
		
		Wrapper rolePermissionWrapper = Condition.create().eq("deleted", false).eq("role_id", roleId);
		List<SysRolePermission> rolePermissions = rolePermissionService.selectList(rolePermissionWrapper);
		SysRole role = roleDao.selectById(roleId);
		List<SysRolePermission> list = getDeleteRolePermission(role,permissions,rolePermissions);
		List<String> rolePermissionId = new ArrayList<String>();
		for(SysRolePermission per:list){
			rolePermissionId.add(per.getId());
		}
		rolePermissionService.deleteBatchIds(rolePermissionId);
	}
	
	/**
	 * 获取需要新增的RolePermission
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月15日 上午10:58:33
	 */
	private List<SysRolePermission> getCreateRolePermission(SysRole role,List<SysPermission> permissions,List<SysRolePermission> rolePermissions){
		//已经与角色存在关系的Permissions
		List<SysRolePermission> oldPermissions = new ArrayList<SysRolePermission>();
		List<String> oldPermissionsId = new ArrayList<String>();
		for(SysRolePermission rolePermission : rolePermissions){
			oldPermissionsId.add(rolePermission.getPermissionId());
		}
		if (oldPermissionsId.size()>0) {
			Wrapper wrapper = Condition.create().in("id", oldPermissionsId);
			oldPermissions = rolePermissionService.selectList(wrapper);
		}
		
		List<SysRolePermission> list = new ArrayList<SysRolePermission>();
			for(SysPermission permission : permissions){
				boolean flag = false;
				flag = oldPermissions.contains(permission.getId());
				//如果permission不存在于rolePermissions则证明为新增的
				if(!flag){
					SysRolePermission rolePermissionNew = new SysRolePermission();
					rolePermissionNew.setRoleId(role.getId());
					rolePermissionNew.setPermissionId(permission.getId());
					list.add(rolePermissionNew);
				}
			}
		return list;
	}
	
	/**
	 * 获得需要删除的RolePermission
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月15日 上午11:29:00
	 */
	private List<SysRolePermission> getDeleteRolePermission(SysRole role,List<SysPermission> delPermissions,List<SysRolePermission> rolePermissions){
		List<SysRolePermission> list = new ArrayList<SysRolePermission>();
		List<String> delPermissionsList = new ArrayList<String>();
		for(SysPermission per:delPermissions){
			delPermissionsList.add(per.getId());
		}
		for(SysRolePermission rolePermission : rolePermissions){
			if(delPermissionsList.contains(rolePermission.getPermissionId())){
				list.add(rolePermission);
			}
		}
		return list;
	}
	
	
	
	/**
	 * 为角色添加数据过滤信息
	 * @param roleId
	 * @param dataFilterIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:25:10
	 */
	public void saveDataFiltersForRole(String roleId,String[] dataFilterIds){
		Wrapper wrapper = Condition.create().eq("deleted", false).in("id", dataFilterIds);

		Wrapper wrapperRoleData= Condition.create().eq("deleted", false).eq("role_id", roleId);
		
		List<SysRoleDataFilter> roleDataFilters = roleDataFilterService.selectList(wrapperRoleData);
		SysRole role = roleDao.selectById(roleId);
		//roleDataFilterService.insertBatch(getCreateRoleDataFilter(role, dataFilters, roleDataFilters));

	}
	
	
	/**
	 * 为角色删除数据过滤信息
	 * @param roleId
	 * @param dataFilterIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:25:23
	 */
	public void deleteDataFiltersForRole(String roleId,String[] dataFilterIds){
		Wrapper<SysDataFilter> wrapper = Condition.create().eq("deleted", false).in("id", dataFilterIds);
		//List<SysDataFilter> dataFilters =  dataFilterService.selectList(wrapper);
		Wrapper<SysRoleDataFilter> roleDataFilterwrapper = Condition.create().eq("deleted", false).eq("role_id", roleId);
		List<SysRoleDataFilter> roleDataFilters = roleDataFilterService.selectList(roleDataFilterwrapper);
		SysRole role = roleDao.selectById(roleId);
		//List<SysRoleDataFilter> list = getDeleteRoleDataFilter(role, dataFilters, roleDataFilters);
		List<String> listId = new ArrayList<String>();
		
//		for(SysRoleDataFilter per:list){
//			listId.add(per.getId());
//		}
		roleDataFilterService.deleteBatchIds(listId);
	}
	
	
	/**
	 * 获取需要新增的RoleDataFilter
	 * @param role
	 * @param dataFilters
	 * @param roleDataFilters
	 * @return 
	 * @return List<RoleDataFilter>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:21:35
	 */
	private List<SysRoleDataFilter> getCreateRoleDataFilter(SysRole role,List<SysDataFilter> dataFilters,List<SysRoleDataFilter> roleDataFilters){
		List<SysDataFilter> oldDataFilters = new ArrayList<SysDataFilter>();
		List<String> oldDataFiltersId = new ArrayList<String>();
		for(SysRoleDataFilter roleDataFilter : roleDataFilters){
			oldDataFiltersId.add(roleDataFilter.getDataFilterId());
		}
		if (oldDataFiltersId.size()>0) {
			Wrapper wrapper = Condition.create().in("id", oldDataFiltersId);
			//oldDataFilters = dataFilterService.selectList(wrapper);
		}
		
		List<SysRoleDataFilter> list = new ArrayList<SysRoleDataFilter>();
			for(SysDataFilter dataFilter : dataFilters){
				boolean flag = false;
				flag = oldDataFilters.contains(dataFilter.getId());
				//如果dataFilter没有存在于角色对应的dataFilters中，证明是新增的
				if(!flag){
					SysRoleDataFilter roleDataFilter = new SysRoleDataFilter();
					roleDataFilter.setRoleId(role.getId());
					roleDataFilter.setDataFilterId(dataFilter.getId());
					list.add(roleDataFilter);
				}
			}
		return list;
	}
	
	
	/**
	 * 获得已删除的dataFilters
	 * @param role
	 * @param dataFilters
	 * @param roleDataFilters
	 * @return 
	 * @return List<RoleDataFilter>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月30日 上午10:23:58
	 */
	private List<SysRoleDataFilter> getDeleteRoleDataFilter(SysRole role, List<SysDataFilter> delDataFilters,
			List<SysRoleDataFilter> roleDataFilters) {
		List<SysRoleDataFilter> list = new ArrayList<SysRoleDataFilter>();
		List<String> delDataFiltersIds = new ArrayList<String>();
		for (SysDataFilter del : delDataFilters) {
			delDataFiltersIds.add(del.getId());
		}
		for (SysRoleDataFilter roleDataFilter : roleDataFilters) {
			if (delDataFiltersIds.contains(roleDataFilter.getDataFilterId())) {
				list.add(roleDataFilter);
			}
		}
		return list;
	}
	
	
	/**
	 * 为角色新增用户
	 * @param roleId
	 * @param userIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月8日 下午5:12:40
	 */
	public void saveUsersForRole(String roleId,String[] userIds){
		//找出需要添加的用户信息
		Wrapper wrapper = Condition.create().eq("deleted", false).in("id", userIds);
		List<SysUser> users =  userService.selectList(wrapper);

		//找出目前已存在该角色下的关联关系
		/*Wrapper roleDataFilterwrapper = Condition.create().eq("deleted", false).eq("role_id", roleId);
		List<SysRoleUser> roleUsers = roleUserService.selectList(roleDataFilterwrapper);*/

		//找出对应角色
		//SysRole role = roleDao.selectById(roleId);

		//删除该角色下所有用户关系，之后重新新增这次关联的用户
		//roleUserService.deleteByRoleId(roleId);
		for(SysUser sysUser : users){
			SysRoleUser sysRoleUser = new SysRoleUser();
			sysRoleUser.setDeleted(false);
			sysRoleUser.setRoleId(roleId);
			sysRoleUser.setUserId(sysUser.getId());
			roleUserService.insert(sysRoleUser);
		}
		//获取需要新增的关联关系
		//List<SysRoleUser> list = getCreateUsers(role, users, roleUsers);
	}
	/**
	 * 为角色新增用户
	 * @param roleId
	 * @param userIds 
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月8日 下午5:12:40
	 */
	public boolean saveRoleOrg(String roleId,String roleType,String[] orgIds){
		boolean result=false;
		if(roleType.equals("1")){
			//删除所有的数据权限
			Wrapper roleOrg = Condition.create().eq("role_id", roleId);
			sysRoleOrgService.delete(roleOrg);
			//添加新数据
			for (String orgId : orgIds) {
				SysRoleOrg sysRoleOrg = new SysRoleOrg();
				sysRoleOrg.setOrgId(orgId);
				sysRoleOrg.setRoleId(roleId);
				result = sysRoleOrgService.insert(sysRoleOrg);
			}
			
		}
		SysRole role = roleDao.selectById(roleId);
		role.setRoleType(Integer.valueOf(roleType));
		result = this.updateById(role);
		 return result;
	}
	
	/**
	 * 为角色删除用户
	 * @param roleId
	 * @param userId
	 * @return void
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月8日 下午5:15:09
	 */
	public boolean deleteUsersForRole(String roleId,String userId){
		/*Wrapper wrapper = Condition.create().eq("deleted", false).in("id", userIds);
		//List<SysUser> users = userService.selectList(new EntityWrapper<SysUser>().eq("deleted", false).in("id",userIds));
		List<SysUser> users =  userService.selectList(wrapper);
		Wrapper roleDataFilterwrapper = Condition.create().eq("deleted", false).eq("role_id", roleId);
		List<SysRoleUser> roleUsers = roleUserService.selectList(roleDataFilterwrapper);
		SysRole role = roleDao.selectById(roleId);
		List<SysRoleUser> list = getDeleteUsers(role, users, roleUsers,roleId);
		List<String> ids = new ArrayList<String>();
		for(SysRoleUser user:list){
			ids.add(user.getId());
		}*/
		SysRoleUser sysRoleUser = roleUserService.getByRoleIdAndUserId(roleId,userId);
		boolean flag = true;
		if(sysRoleUser != null){
			flag = roleUserService.deleteById(sysRoleUser.getId());
		}
		return flag;
}
	
	/**
	 * 获取需要新增的用户
	 * @param role
	 * @param users
	 * @param roleUsers
	 * @return 
	 * @return List<RoleUser>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月8日 下午5:15:23
	 */
	private List<SysRoleUser> getCreateUsers(SysRole role,List<SysUser> users,List<SysRoleUser> roleUsers){
		List<SysUser> oldUsers = new ArrayList<SysUser>();
		List<String> oldUsersId = new ArrayList<String>();
		for(SysRoleUser roleUser : roleUsers){
			oldUsersId.add(roleUser.getUserId());
		}
		if (oldUsersId.size()>0) {
			Wrapper wrapper = Condition.create().eq("deleted", false).in("id", oldUsersId);
			oldUsers = userService.selectList(wrapper);
		}
		
		List<SysRoleUser> list = new ArrayList<SysRoleUser>();
			for(SysUser user : users){
				boolean flag = false;
				flag = oldUsers.contains(user);
				//如果user没有存在于角色对应的roleUsers中，证明是新增的
				if(!flag){
					SysRoleUser roleUser = new SysRoleUser();
					roleUser.setRoleId(role.getId());
					roleUser.setUserId(user.getId());
					list.add(roleUser);
				}
			}
		return list;
	}
	
	
	/**
	 * 获取需要删除的用户
	 * @param role
	 * @param delUsers
	 * @param roleUsers
	 * @return 
	 * @return List<RoleUser>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年1月8日 下午5:15:37
	 */
	private List<SysRoleUser> getDeleteUsers(SysRole role,List<SysUser> delUsers,List<SysRoleUser> roleUsers,String roleId){
		List<SysRoleUser> list = new ArrayList<SysRoleUser>();
			for(SysRoleUser roleUser : roleUsers){
				for(SysUser user : delUsers){
					if(user.id.contains(roleUser.getUserId()) && roleUser.getRoleId().equals(roleId)){
						list.add(roleUser);
					}
				}
			}
		return list;
	}
	
	/**
	 * 根据角色标识符查找用户
	 * @param roleCode 角色标识符
	 * @return
	 */
	public List<SysRoleUser> getRoleUserByRoleCode(String roleCode){
		List<SysRoleUser> list = new ArrayList<SysRoleUser>();
		Wrapper wrapper = Condition.create().eq("deleted", false).eq("roleCode", roleCode);
		List<SysRole> roleList = roleDao.selectList(wrapper);
		if(!roleList.isEmpty()){
			String role = roleList.get(0).getId();
			Wrapper roleuserwrapper = Condition.create().eq("deleted", false).eq("roleId", role);
			list = roleUserService.selectList(roleuserwrapper);
		}
		return list;
	}
	
	/**
	 * 角色类型查询角色
	 * @param typeId
	 * @return
	 */
	public List<SysRole> selectListByTypeId(String orgName,String orgCode) {
		return roleDao.selectListByTypeId(orgName,orgCode);
	}
	
	/**
	 * 判断当前用户是否具有系统管理员角色
	 * @param userId
	 * @return
	 */
	public Boolean checkIsAdmin(String userId) {
		Boolean flag = false;
		List<SysRole> sysRolesList = findByUser(userId);
		for (SysRole role: sysRolesList) {
			if ("supersysman".equals(role.getRoleCode())) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	public List<SysRoleResource>getRoleResource(String roleId ){
		return roleDao.getRoleResource(roleId);
	}
	public List<SysRoleOrg>getRoleOrg(String roleId ){
		return roleDao.getRoleOrg(roleId);
	}

	public void saveBtnForRole(String roleId, String[] btnNames) {
		//List<String> menuId = new ArrayList<>();
		//btnNames为需要给对应角色添加的按钮
		//找出所有启用的按钮Id
		List<String> btnList = resourceService.selectAllBtnId();
		String[] allBtnId = btnList.toArray(new String[btnList.size()]);
		roleResourceService.deleteOnRoleIdAndResArr(roleId,allBtnId);
		if(btnNames != null){
			for(String btnName : btnNames){
				SysResource btn = resourceService.selectBtnByName(btnName);
				SysRoleResource sysRoleResource = new SysRoleResource();
				sysRoleResource.setRoleId(roleId);
				sysRoleResource.setResourceId(btn.getId());
				roleResourceService.insert(sysRoleResource);
			}
		}
		//String[] menusId = menuId.toArray(new String[menuId.size()]);
		//saveMenusForRole(roleId, menusId);
	}

}
