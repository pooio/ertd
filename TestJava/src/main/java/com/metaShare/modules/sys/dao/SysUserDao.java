package com.metaShare.modules.sys.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysUser;
public interface SysUserDao extends BaseMapper<SysUser> {
	
	/**
	 * 根据用户名或者登陆名称查找用户列表
	 * @param name
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:42:55
	 */
	List<SysUser> findByNameOrLoginName(String name);
	
	
	
	/**
	 * 根据登录名查找用户
	 * @param loginName
	 * @return User
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:43:10
	 */
	List<SysUser> findByOnlyLoginName(String loginName);
	
	
	/**
	 * 根据角色编码和登录名查找用户信息
	 * @param roleCodes 角色编码List
	 * @param loginNames 登录名List
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:43:46
	 */
	List<SysUser> findByRoleCodesOrLoginNames(@Param("roleCodes")List<String> roleCodes,@Param("loginNames")List<String> loginNames);
	
	List<SysUser> findByRoleCodesAndLoginNames(@Param("roleCodes")List<String> roleCodes,@Param("id")String id);
	
	/**
	 * 根据角色编码查找用户
	 * @param roleCodes 角色编码List
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:44:26
	 */
	List<SysUser> findByRoleCodes(@Param("roleCodes")List<String> roleCodes);
	
	/**
	 * 根据登录名查找用户
	 * @param loginNames 登录名List
	 * @return 
	 * @return List<User>
	 * @author: zhaojie/zh_jie@163.com 
	 * @version: 2016年5月10日 下午3:45:02
	 */
	List<SysUser> findByLoginNames(@Param("loginNames")List<String> loginNames);
	
	Date getLastUpdateTime();


	/**
	 * 查询
	 * @return
	 */
	List<SysUser> listByRoleId(Map<String, String> map);

	/**
	 * 判断旧密码
	 * @param user
	 * @return
	 */

    String getUserNameByLoginName(String loginName);

	List<SysUser> selectUserCount(String orgCode);
    
    List<SysUser> listAll(@Param("name") String name);

    SysUser selectByLoginName(String account);


    List<SysUser> getUserListByRole(@Param("roleId") String roleId,@Param("orgId")String orgId,@Param("name")String name);

	List<SysUser> selectListNoImgInfo(@Param("name") String name,@Param("loginName") String loginName,@Param("orgId") String orgId);

	List<SysUser> getNoUserListByRoleOnOrg(@Param("roleId") String roleId,@Param("orgId") String orgId,@Param("name")String name);

    void updateByIdOnSelf(SysUser sysUser);
}
