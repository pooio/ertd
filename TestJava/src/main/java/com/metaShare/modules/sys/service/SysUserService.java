package com.metaShare.modules.sys.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.metaShare.common.utils.SessionHelper;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.sys.dao.SysRoleUserDao;
import com.metaShare.modules.sys.dao.SysUserDao;
import com.metaShare.modules.sys.entity.SysDict;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.entity.SysRoleUser;
import com.metaShare.modules.sys.entity.SysUser;

@Service
public class SysUserService extends ServiceImpl<SysUserDao,SysUser> {
	//private static Logger logger = LoggerFactory.getLogger(SysUserService.class);
	public static final int HASH_INTERATIONS = 10;
	@Autowired
	private SysUserDao userDao;
	@Autowired 
	protected SysRoleUserService roleUserService;
	@Autowired
	protected SysRoleService roleService;
	@Autowired
	protected SysOrganizationService organizationService;
	@Autowired
	private SysRoleUserDao sysRoleUserDao;
	public int getCount(){
		Wrapper<SysUser> wrapper = Condition.create().eq("deleted", false);
		List<SysUser> list = this.selectList(wrapper);
		return list.size();
	}
	/**
	 * 检查登录名是否可用
	 * @param loginName
	 * @param excludeUserId
	 * @return 
	 * @return Boolean
	 */
	public Boolean checkLoginName(String loginName,String excludeUserId){
		SessionHelper sessionHelper = new SessionHelper();
		Boolean flag = true;
		Wrapper wrapper = Condition.create().eq("deleted", false).eq("login_name", loginName);
		//如果是修改要排除要检查的用户
		if(StringUtils.isNotEmpty(excludeUserId)){
			wrapper.ne("id", excludeUserId);
		}
		
		int count = userDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}

	 public int selectUserCount(String orgCode){
         List<SysUser> userList = userDao.selectUserCount(orgCode);
		 return userList.size();
	 }
	
	/**
	 * 验证手机号是否可用
	 * @param phoneNumber
	 * @param excludeUserId
	 * @return 
	 * @return Boolean
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月18日 上午9:06:33
	 */
	public Boolean checkPhoneNumber(String phoneNumber,String excludeUserId){
		Boolean flag = true;
		
		if(StringUtils.isEmpty(phoneNumber)){
			return flag;
		}
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.eq("phoneNumber", phoneNumber);
		//如果是修改要排除要检查的用户
		if(StringUtils.isNotEmpty(excludeUserId)){
			wrapper.ne("id", excludeUserId);
		}
		int count = userDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 检查用户是否合法
	 * @param user
	 * @return 
	 * @return Boolean 如果合法返回true，如果验证不通过返回false
	 */
	public Boolean checkUser(SysUser user){
	    if(StringUtils.isEmpty(user.getEmail()) && StringUtils.isEmpty(user.getPhoneNumber())) {
            return true;
        }
		Boolean flag = true;
		Wrapper wrapper = Condition.create()
				.eq("deleted", false);
		//未逻辑删除的
		//如果是修改要排除要检查的用户
		if(StringUtils.isNotEmpty(user.getId())){
			wrapper.ne("id", user.getId());
		}
		
		//查找手机号等于要检查用户的手机号
		//或者
		//登录名等于要检查用户的登录名
		//或者
		//邮箱等于要验证的邮箱
		wrapper.eq("login_name", user.getLoginName());
		int count = userDao.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 保存用户信息
	 * @param user
	 * @param roleIds
	 * @return 
	 * @return User
	 */
	public SysUser save(SysUser user,String roleIds) throws Exception{
		user.setPlainPassword(SysUser.pw);
		entryptPassword(user);
		if(StringUtils.isEmpty(user.getPhoneNumber())){
			user.setPhoneNumber(null);
		}
		this.insertOrUpdate(user);
		user = this.findByLoginName(user.getLoginName());
		
		//先查询用户与角色关系
		List<SysRoleUser> addRoles = new ArrayList<SysRoleUser>();
		List<SysRoleUser> deleteRoles = new ArrayList<SysRoleUser>();
		
		prepareSaveOrUpdate(user,roleIds,addRoles,deleteRoles);
		        
        //保存用户与角色间的关系
    	if(CollectionUtils.isNotEmpty(addRoles)){
			roleUserService.insertBatch(addRoles);
		}
		if(CollectionUtils.isNotEmpty(deleteRoles)){
			List<String> idList = new ArrayList<String>();
			for(SysRoleUser roleUser:deleteRoles){
				idList.add(roleUser.getId());
			}
			roleUserService.deleteBatchIds(idList);
		}
		
		return user;
	}
	
	/**
	 * 选择性的更新用户字段值
	 * @param user 用户
	 * @param roleIds 角色以逗号分隔
	 * @param fields 要更新的字段
	 * @return void
	 * @throws SecurityException 
	 * @throws Exception 
	 */
	public void updateSelective(SysUser user,String roleIds) throws Exception {
		//先查询用户与角色关系
		List<SysRoleUser> addRoles = new ArrayList<SysRoleUser>();
		List<SysRoleUser> deleteRoles = new ArrayList<SysRoleUser>();
		
		prepareSaveOrUpdate(user,roleIds,addRoles,deleteRoles);
        //保存用户与角色间的关系
    	if(CollectionUtils.isNotEmpty(addRoles)){
    		roleUserService.insertBatch(addRoles);
		}
    	this.updateById(user);
		if(CollectionUtils.isNotEmpty(deleteRoles)){
			List<String> idList = new ArrayList<String>();
			for(SysRoleUser roleuser :deleteRoles){
				idList.add(roleuser.getId());
			}
			roleUserService.deleteBatchIds(idList);
		}
	}
	
	
	/**
	 * 查询并整理出需要新增以及需要删除的角色与用户的关系
	 * @param user
	 * @param roleIds
	 * @param addRoles
	 * @param deleteRoles 
	 * @return void
	 */
	private void prepareSaveOrUpdate(SysUser user,String roleIds,List<SysRoleUser> addRoles,List<SysRoleUser> deleteRoles){
		if(StringUtils.isNotEmpty(roleIds)){
			List<SysRoleUser> roleUsers = new ArrayList<SysRoleUser>();
			if(StringUtils.isNotEmpty(user.getId())){
				//查询用户已分配的角色
				Wrapper roleUserWrapper = Condition.create();
				roleUserWrapper.eq("deleted", false).in("user_id", user.getId());
	    		roleUsers = roleUserService.selectList(roleUserWrapper);
			}
    		
    		//页面传入的角色
    		List<SysRole> roles = new ArrayList<SysRole>();
    		String[] arrRoleIds = roleIds.split(",");
    		for(String roleId : arrRoleIds){
    			SysRole role = new SysRole();
    			role.setId(roleId);
    			roles.add(role);
    		}
    		//获取需要新增的角色
    		getAddRoles(user, roles, roleUsers,addRoles);
    		//获取需要删除的角色
    		getDeleteRoles(user, roles, roleUsers,deleteRoles);
    	}
	}
	
	/**
	 * 根据角色列表或用户登录名列表查询用户信息
	 * @param roleCodes
	 * @param loginNames
	 * @return 
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年3月31日 上午11:17:42
	 */
	public  List<SysUser> findByRoleCodesOrLoginNames(List<String> roleCodes,List<String> loginNames){
		if(CollectionUtils.isNotEmpty(roleCodes) 
				&& CollectionUtils.isNotEmpty(loginNames)){
			return userDao.findByRoleCodesOrLoginNames(roleCodes, loginNames);
		}
		if(CollectionUtils.isNotEmpty(loginNames)){
			return userDao.findByLoginNames(loginNames);
		}
		if(CollectionUtils.isNotEmpty(roleCodes)){
			return userDao.findByRoleCodes(roleCodes);
		}
		//如果两个参数都为空则返回空List
		return new ArrayList<SysUser>();
	}
	
	/**
	 * 根据用户id和角色id查询用户信息
	 * @param roleCodes
	 * @param loginNames
	 * @return
	 */
	public  List<SysUser> findByRoleCodesAndLoginNames(List<String> roleCodes,String id){
		List list = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(roleCodes) 
				&& StringUtils.isNotEmpty(id)){
			list = userDao.findByRoleCodesAndLoginNames(roleCodes, id);
		}
	
		//如果两个参数都为空则返回空List
		return list;
	}
	
	/**
	 * 根据角色数组或登录名数组查询用户
	 * @param roleCodes
	 * @param loginNames
	 * @return 
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年3月31日 上午11:19:01
	 */
	public List<SysUser> findByRoleCodesOrLoginNames(String[] roleCodes,String[] loginNames){
		if(roleCodes==null){
			roleCodes = new String[0];
		}
		if(loginNames==null){
			loginNames = new String[0];
		}
		return findByRoleCodesOrLoginNames(Arrays.asList(roleCodes), Arrays.asList(loginNames));
	}
	
	/**
	 * 重置密码
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午10:07:12
	 */
	private void entryptPassword(SysUser user) {
		user.randomSalt();
		user.setPassword(new SimpleHash("MD5",user.getPlainPassword(), user.getSalt(), SysUserService.HASH_INTERATIONS).toString());
	}
	
	/**
	 * 修改密码
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午10:22:22
	 */
	public SysUser updatePassword(SysUser user,String plainPassword) {
		user.randomSalt();
		user.setPlainPassword(plainPassword);
		entryptPassword(user);
		this.updateById(user);
		return user;
	}
	
	
	/**
	 * 根据姓名或者登陆名模糊查找用户
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午10:07:59
	 */
	public List<SysUser> findByName(String userName){
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.like("name", userName)
				.or().like("login_name", userName)
				.orderBy("login_name", false);
		Page<SysUser> page = new Page(0,10) ;
		List<SysUser> list = userDao.selectPage(page, wrapper);
		return list;
		
	}
	
	/**
	 * 根据姓名或者登陆名和组织id模糊查询 当前部门员工
	 */
	public List<SysUser> findByNameAndOrg(String name,String orgId) {
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.eq("orgId", orgId)
				.or().like("name", name)
				.like("login_name", name)
				.orderBy("login_name", false);
		Page<SysUser> page = new Page(0,10) ;
		List<SysUser> list = userDao.selectPage(page, wrapper);
		return list;
	}
	public List<SysUser> getUserByOrgId(String orgId){
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.eq("org_id", orgId)
				.orderBy("login_name", false);
		List<SysUser> list = userDao.selectList(wrapper);
		return list;
	}
	
	/**
	 * 改变用户状态
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午9:12:30
	 */
	public SysUser changeStatus(SysUser user,Integer status){
		user.setStatus(status);
		userDao.updateById(user);
		return user;
	}
	
	public SysUser findOne(String id){
		return userDao.selectById(id);
	}
	/**
	 * 根据登录名查找用户
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午9:12:54
	 */
	public List<SysUser> findByOnlyLoginName(String loginName) throws Exception{
		Wrapper wrapper = Condition.create()
				.eq("login_name", loginName);
		List<SysUser> userList = userDao.selectList(wrapper);
		return userList;
	}

	/**
	 * 根据登录名查找用户
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2015年12月10日 上午9:12:54
	 */
	public SysUser findByLoginName(String loginName){
		Wrapper<SysUser> wrapper = Condition.create()
				.eq("login_name", loginName)
				.eq("deleted", false);
		SysUser user = null;
		List<SysUser> userList = userDao.selectList(wrapper);
		if (userList.size()>0) {
			user = userList.get(0);
		}else{
			return null;
		}
		return user;
	}
	
	/**
	 * 根据邮箱查询用户
	 * @param email
	 * @return 
	 * @return User
	 */
	public SysUser findByEmail(String email){
		SysUser user = new SysUser();
		user.setDeleted(false);
		user.setEmail(email);
		return userDao.selectOne(user);
	}
	
	/**
	 * 根据手机查询用户
	 * @param phone
	 * @return 
	 * @return User
	 */
	public SysUser findByPhoneNumber(String phone){
		SysUser user = new SysUser();
		user.setDeleted(false);
		user.setPhoneNumber(phone);;
		return userDao.selectOne(user);
	}
	
	/**
	 * 删除用户
	 */
	 public void delete(SysUser user) {
		 String[] ids = new String[1];
		 ids[0] = user.getId();
		 delete(ids);
    }

    /**
     * 根据主键批量删除用户
     * @param ids 
     */
    public void delete(String[] ids) {
    	List<String> list = Arrays.asList(ids);
    	//先删除与角色的关系
    	roleUserService.deleteByUserIds(list);
    	//再删除用户
    	userDao.deleteBatchIds(Arrays.asList(ids));
    }
	
	private void getAddRoles(SysUser user,List<SysRole> roles,List<SysRoleUser> roleUsers,List<SysRoleUser> addRoles){
		List<SysRole> oldRoles = new ArrayList<SysRole>();
		List<String> oldRolesId = new ArrayList<String>();
		for(SysRoleUser roleUser : roleUsers){
			oldRolesId.add(roleUser.getRoleId());
		}
		
		if (oldRolesId.size()>0) {
			Wrapper wrapper = Condition.create()
					.in("id", oldRolesId);
			oldRoles = roleService.selectList(wrapper);
		}
		
		for(SysRole role : roles){
			if(!oldRoles.contains(role)){
				SysRoleUser roleUser = new SysRoleUser();
				roleUser.setRoleId(role.getId());
				roleUser.setUserId(user.getId());
				addRoles.add(roleUser);
			}
		}
	}
	
	private void getDeleteRoles(SysUser user,List<SysRole> roles,List<SysRoleUser> roleUsers,List<SysRoleUser> deleteRoles){
			for(SysRoleUser roleUser : roleUsers){
				if(!roles.contains(roleUser.getRoleId())){
					deleteRoles.add(roleUser);
				}
			}
	}
	
	public Date getLastUpdateTime(){
		return userDao.getLastUpdateTime();
	}
	
	public SysUser findByUserNo(String userNo){
		SysUser user = new SysUser();
		user.setDeleted(false);
		user.setUserNo(userNo);;
		return userDao.selectOne(user);
	}
	
	
	/**
	 * 当前登录人所在部门是否主责部门
	 * @param list
	 * @return
	 */
	private boolean isMainDept(List<SysDict> list,String orgId) {
		boolean flag = false;
		String[] s= list.get(0).getDictName().split(",");//主责部门ID
		for(String deptId:s){
			if(orgId.equals(deptId)){//是主责部门
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	/**
	 * 用户是否是部门领导
	 * @param user
	 * @return
	 */
	private Boolean getUserIsLeader(SysUser user) {
		Boolean flag = false;
		Wrapper wrapper = Condition.create()
				.eq("userId", user.getId());
		
		List<SysRoleUser> list = sysRoleUserDao.selectList(wrapper);
		for(SysRoleUser roleUser:list){
			SysRole role = roleService.selectById(roleUser.getRoleId());
			if(role.getRoleCode().equals("depart_manger")){;//部门领导
				flag = true;
				break;
			}
			
		}
		return flag;
	}
	/**
	 * 根据组织机构id获取组织机构信息及其所有下级组织机构信息
	 * @param ids
	 * @return 
	 * @return List<Organization>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月13日 下午3:42:41
	 */
	private List<SysOrganization> findOrgAndChildren(String[] ids){
		List<SysOrganization> all = organizationService.treeData();
		List<SysOrganization> list = Lists.newArrayList();
		for(String id : ids){
			for(SysOrganization org : all){
				if(org.getId().equals(id)){
					list.add(org);
					break;
				}
			}
		}
		
		List<SysOrganization> retList = Lists.newArrayList();
		retList.addAll(list);
		for(SysOrganization organization : list){
			getChildren(all,retList,organization);
		}
		
		return retList;
	}
	private void getChildren(List<SysOrganization> all,List<SysOrganization> childrenList,SysOrganization parent){
		for(SysOrganization org : all){
			if(parent.getId().equals(org.getParentId())){
				childrenList.add(org);
				getChildren(all, childrenList, org);
			}
		}
	}

	public SysUser getByAccount(String account) {
		/*SysUser user = new SysUser();
		user.setLoginName(account);
		user.setDeleted(false);*/
		return userDao.selectByLoginName(account);
	}

	/**
	 * 查询
	 * @return
	 */
	public List<SysUser> listByRoleId(Map<String, String> map) {
		return userDao.listByRoleId(map);
	}


	public List<SysUser> findAllUsersByName(String name) {
		Wrapper<SysUser> wrapper = Condition.create()
				.eq("deleted", false)
				.like("name", name)
				.orderBy("login_name");
		List<SysUser> userList = userDao.selectList(wrapper);
		return userList;
	}


	public Set<String> listUserPerms(String id) {
		// TODO Auto-generated method stub
		return null;
	}


	public Set<String> listUserRoles(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserNameByLoginName(String loginName) {
		return userDao.getUserNameByLoginName(loginName);
	}

	public SysUser findUserByLoginName(String username) {
		return userDao.selectByLoginName(username);
	}

	/**
	 * 根据角色获取用户列表
	 * @param roleId
	 * @return
	 */
	public List<SysUser> getUserListByRole(String roleId,String orgId,String name) {
		return userDao.getUserListByRole(roleId,orgId,name);
	}

	public List<SysUser> selectListNoImgInfo(String name, String loginName, String orgId) {
		return userDao.selectListNoImgInfo(name,loginName,orgId);
	}

	public List<SysUser> getNoUserListByRoleOnOrg(String roleId, String orgId,String name) {
		return userDao.getNoUserListByRoleOnOrg(roleId,orgId,name);
	}
}
