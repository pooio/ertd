package com.metaShare.modules.sys.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.metaShare.common.utils.AesUtil;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.sys.entity.*;
import com.metaShare.modules.sys.service.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.dao.SysUserDao;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import net.sf.ehcache.search.impl.ResultImpl;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

/**
 * 管理员管理用户的Controller.
 */
@Controller
@RequestMapping("api/sys/user")
@Api(tags = "用户管理")
public class SysUserController extends BaseController {
	@Autowired
	SysUserService userService;
	@Autowired
	SysRoleService roleService;
	@Autowired
	SysRoleUserService roleUserService;
	@Autowired
	SysOrganizationService organizationService;
	@Autowired
	SysPostManageService postManageService;
	@Autowired
	SysUserDao sysUserDao;
	@Autowired
	private SysFileAttachService sysFileAttachService;
	@Autowired
	private SysconfigService sysconfigService;
	@Autowired
	private SysEmailController sysEmailController;
	@Autowired
	private SysEmailService sysEmailService;
	/**
	 * 验证登录名是否可用
	 * 
	 * @param
	 * @param loginName
	 * @return
	 * @throws Exception
	 * @return Boolean 登录名可用返回true，登录名已被使用返回false
	 */
	@ResponseBody
	@RequestMapping(value = "checkLoginName", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "loginName", value = "登录名", required = true, dataType = "int", paramType = "query") })
	@ApiOperation(value = "验证登录名是否可用", notes = "检验用户名是否已经存在")
	@ApiModelProperty(value = "true：可用；false:用户已经存在")
	public Result checkLoginName(String id, String loginName) throws Exception {
		Boolean flag = userService.checkLoginName(loginName, id);
		if (!flag) {
			return Result.resultInfo(ResultCode.FAILURE, flag);
		}
		return Result.resultInfo(ResultCode.SUCCESS, flag);
	}

	/**
	 * 验证手机号是否可用
	 * 
	 * @param userId
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 * @return Boolean 手机号可用返回true，手机号已被使用返回false
	 */
	@ResponseBody
	@RequestMapping(value = "checkPhoneNumber", method = { RequestMethod.POST })
	@ApiOperation(value = "验证手机号是否可用用", notes = "验证手机号是否可用用")
	@ApiModelProperty(value = "true：可用；false:用户已经存在")
	public Result checkPhoneNumber(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "phoneNumber") String phoneNumber) throws Exception {
		return Result.resultInfo(ResultCode.SUCCESS, userService.checkPhoneNumber(phoneNumber, userId));
	}

	// 分页查询
	@ResponseBody
	@RequestMapping(value = "list", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "pageSize", value = "页数", required = true, dataType = "int"),
			@ApiImplicitParam(name = "pageNum", value = "每页大小", required = true, dataType = "int"),
			@ApiImplicitParam(name = "name", value = "姓名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "loginName", value = "登录名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "orgId", value = "组织机构id", required = false, dataType = "String") })
	@ApiOperation(value = "用户列表")
	public Result getList(int pageSize, int pageNum, String name, String loginName, String orgId) {
		PageDTO<SysUser> pageDTO = null;
		int total = 0;
		try {
			if (orgId.equals("1")) {
				orgId = "";
			}
			/*Wrapper<SysUser> wrapper = Condition.create().eq("deleted", false);
			if (StringUtils.isNotEmpty(name)) {
				wrapper.like("name", name);
			}
			if (StringUtils.isNotEmpty(loginName)) {
				wrapper.like("login_name", loginName);
			}
			if (StringUtils.isNotEmpty(orgId)) {
				wrapper.eq("org_id", orgId);
			}
			wrapper.orderBy("create_time", false);*/
			List<SysUser> list = userService.selectListNoImgInfo(name,loginName,orgId);
			total = list.size();
			pageDTO = new PageTool<SysUser>().getPage(list, pageSize, pageNum);
			for (SysUser user : pageDTO.getData()) {
				List<SysRole> roleList = roleService.findByUser(user.getId());
				List<String> listrole = new ArrayList<String>();
				for (SysRole role : roleList) {
					listrole.add(role.getId());
				}
				user.setRoleCodes(listrole);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	// 分页查询
	@ResponseBody
		@RequestMapping(value = "listAll", method = { RequestMethod.POST })
		@ApiImplicitParams({ 
				@ApiImplicitParam(name = "name", value = "姓名", required = false, dataType = "String"),
				@ApiImplicitParam(name = "loginName", value = "登录名", required = false, dataType = "String"),
				@ApiImplicitParam(name = "orgId", value = "组织机构id", required = false, dataType = "String") })
		@ApiOperation(value = "用户所有数据")
		public Result listAll(String name, String loginName, String orgId) {
		    List<SysUser> list = sysUserDao.listAll(name);
			return Result.resultInfo(ResultCode.SUCCESS,list);
		}

	/**
	 * 保存用户
	 * 
	 * @param m
	 * @param roleId
	 * @param result
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年4月20日 上午11:23:10
	 * @throws Exception
	 */

	// public Result saveUser(SysUser m, @RequestParam(value = "roleIds") String
	// roleIds, BindingResult result) {
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "name", value = "姓名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "sex", value = "性别", required = false, dataType = "int"),
			@ApiImplicitParam(name = "loginName", value = "登录名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "phoneNumber", value = "手机号码", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postManageId", value = "职务ID", required = false, dataType = "String"),
			@ApiImplicitParam(name = "userNo", value = "工号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "orgName", value = "组织机构名称", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postId", value = "岗位id", required = false, dataType = "String"),
			@ApiImplicitParam(name = "orgId", value = "组织机构id", required = true, dataType = "String") })
	@ApiOperation(value = "新增用户")
	public Result saveUser(SysUser m, BindingResult result, @RequestParam(value = "roleId") String roleId)
			throws Exception {

		if (m.getOrgId().equals("1")) {
			return Result.resultInfo(ResultCode.FAILURE, "部门不能为空,请选择部门");
		}
		Result res = null;
		m.setStatus(1);
		if (userService.checkUser(m)) {
			if (StringUtil.isEmpty(m.getId())) {
				m.setCreateTime(new Date());
			}
			if (StringUtils.isNotEmpty(m.getOrgId())) {
				SysOrganization org = organizationService.selectById(m.getOrgId());
				if (org != null) {
					m.setOrgName(org.getOrgName());
					m.setOrgCode(org.getOrgCode());
				}

			}
			if (StringUtils.isNotEmpty(m.getPostId())) {
				SysPostManage post = postManageService.selectById(m.getPostId());
				if (post != null) {
					m.setPostName(post.getPost());
				}

			}
			// String operator = getUserInfo().getName();
			/*
			 * String info = "新增了用户:" + m.getName(); saveOperatorInfo(info,
			 * 5,operator );
			 */
			userService.save(m, roleId);

			String operator = getUserInfo().getName();
			String info = "新增了" + m.getName() + "用户";
			saveOperatorInfo(info, StatusEnum.LogInfoType7.getIntValue(), operator);

			res = Result.resultInfo(ResultCode.SUCCESS, "新增数据成功");
		} else {
			res = Result.resultInfo(ResultCode.VAIL_ERROR, "用户登录名或者手机号或者邮箱已被占用，无法完成新增");
		}
		return res;
	}

	/**
	 * 更新用户信息
	 *
	 * @param request
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年2月17日 下午6:48:02
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "update", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "name", value = "姓名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "sex", value = "性别", required = false, dataType = "int"),
			@ApiImplicitParam(name = "loginName", value = "登录名", required = false, dataType = "String"),
			@ApiImplicitParam(name = "phoneNumber", value = "手机号码", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postManageId", value = "职务ID", required = false, dataType = "String"),
			@ApiImplicitParam(name = "userNo", value = "工号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "orgName", value = "组织机构名称", required = false, dataType = "String"),
			@ApiImplicitParam(name = "id", value = "数据ID", required = false, dataType = "String"),
			@ApiImplicitParam(name = "orgId", value = "组织机构id", required = true, dataType = "String") })
	@ApiOperation(value = "编辑用户")
	public Result updateUser(SysUser m, @RequestParam(value = "roleId") String roleId,
			HttpServletRequest request) throws Exception {
		Result res = null;
		if (userService.checkUser(m)) {
			if (StringUtils.isNotEmpty(m.getOrgId())) {
				SysOrganization org = organizationService.selectById(m.getOrgId());
				if (org != null) {
					m.setOrgName(org.getOrgName());
					m.setOrgCode(org.getOrgCode());
				}

			}
			if (StringUtils.isNotEmpty(m.getPostId())) {
				SysPostManage post = postManageService.selectById(m.getPostId());
				if (post != null) {
					m.setPostName(post.getPost());
				}

			}
			userService.updateSelective(m, roleId);

			String operator = getUserInfo().getName();
			String info = "修改了" + m.getName() + "用户";
			saveOperatorInfo(info, StatusEnum.LogInfoType7.getIntValue(), operator);

			res = Result.resultInfo(ResultCode.SUCCESS, "修改数据成功");
		}
		return res;
	}


	/**
	 * 更新用户信息(个人中心)
	 *
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年2月17日 下午6:48:02
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "updateUserInfoSelf", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "sex", value = "性别",dataType = "int"),
			@ApiImplicitParam(name = "phoneNumber", value = "手机号码", dataType = "String"),
			@ApiImplicitParam(name = "age", value = "年龄",dataType = "String"),
			@ApiImplicitParam(name = "qqAcount", value = "QQ号",dataType = "String"),
			@ApiImplicitParam(name = "email", value = "邮箱",  dataType = "String"),
			@ApiImplicitParam(name = "id", value = "用户Id",  dataType = "String"),
			@ApiImplicitParam(name = "headImg", value = "头像base64码",  dataType = "String"),
			@ApiImplicitParam(name = "company", value = "公司", dataType = "String")})
	@ApiOperation(value = "编辑用户")
	public Result updateUserInfoSelf(@RequestParam(value = "id")String id,
									 @RequestParam(value = "headImg",required = false)String headImg,
									 @RequestParam(value = "company")String company,
									 @RequestParam(value = "age",required = false) Integer age,
									 @RequestParam(value = "qqAcount",required = false)String qqAcount,
									 @RequestParam(value = "phoneNumber",required = false)String phoneNumber,
									 @RequestParam(value = "email")String email,
									 @RequestParam(value = "sex",required = false)Integer sex){
		SysUser sysUser = new SysUser();
		sysUser.setPhoneNumber(phoneNumber);
		sysUser.setQqAcount(qqAcount);
		sysUser.setAge(age);
		sysUser.setCompany(company);
		sysUser.setHeadImg(headImg);
		sysUser.setEmail(email);
		sysUser.setId(id);
		sysUser.setSex(sex);
		sysUserDao.updateByIdOnSelf(sysUser);
		return Result.resultInfo(ResultCode.SUCCESS, "修改数据成功");
	}


	/**
	 * 获取用户个人信息(个人中心)
	 *
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年2月17日 下午6:48:02
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "getUserInfoSelf", method = { RequestMethod.POST })
	@ApiImplicitParams({})
	@ApiOperation(value = "编辑用户")
	public Result getUserInfoSelf(@RequestParam(value = "userId",required = false)String userId){
		if(StringUtils.isEmpty(userId)){
			userId = getUserId();
		}
		SysUser sysUser = sysUserDao.selectById(userId);
		return Result.resultInfo(ResultCode.SUCCESS, sysUser);
	}

	/**
	 * 修改登录账号的密码
	 */
	@ResponseBody
	@RequestMapping(value = "changePwd", method = { RequestMethod.POST })
	@ApiOperation(value = "修改密码")
	@ApiImplicitParams({ @ApiImplicitParam(name = "password", value = "新密码", required = false, dataType = "String") })
	public Result changePassword(@RequestParam(value = "password") String password) {
		SysUser user = getUserInfo();
		if (StringUtils.isEmpty(password)) {
			password = SysUser.pw;
		}
		userService.updatePassword(user, password);
		return Result.resultInfo(ResultCode.SUCCESS, "密码修改成功");
	}

	/**
	 * 根据用户id重置密码
	 */
	@ResponseBody
	@RequestMapping(value = "resetPwd", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数据id", required = false, dataType = "String"),
			@ApiImplicitParam(name = "password", value = "新密码", required = true, dataType = "String") })
	@ApiOperation("密码重置")
	public Result resetPassword(@RequestParam(value = "id") String id,
			@RequestParam(value = "password") String password) {
		SysUser user = userService.selectById(id);
		if (StringUtils.isEmpty(password)) {
			password = SysUser.pw;
		}
		userService.updatePassword(user, password);
		return Result.resultInfo(ResultCode.SUCCESS, "重置密码成功");
	}

	// /**
	// * 根据用户名或者登陆名模糊查询 只所以返回map而不是返回User是避免暴露过多的用户信息
	// *
	// * @param name
	// * @return
	// */
	// @ResponseBody
	// @RequestMapping(value = "findUsers")
	// public Result findByName(@RequestParam(value = "name") String name) {
	// List<SysUser> users = userService.findByName(name);
	// List<Map<String, Object>> list = Lists.newArrayList();
	// for (SysUser user : users) {
	// Map<String, Object> map = Maps.newHashMap();
	// map.put("id", user.getId());
	// map.put("name", user.getName());
	// map.put("loginName", user.getLoginName());
	// map.put("orgId", user.getOrgId());
	// map.put("orgName", user.getOrgName());
	// map.put("phoneNumber", user.getPhoneNumber());
	// Integer sex = user.getSex();
	// String sexName = "";
	// if (sex == 1) {
	// sexName = "男";
	// } else if (sex == 0) {
	// sexName = "女";
	// }
	// map.put("sexName", sexName);
	// list.add(map);
	// }
	// return Result.resultInfo(ResultCode.SUCCESS, list);
	// }

	// /**
	// * 根据用户名或者登陆名 和组织id模糊查询 当前部门员工 只所以返回map而不是返回User是避免暴露过多的用户信息
	// *
	// * @param name,
	// * orgId
	// * @return
	// * @return List<Map<String, Object>>
	// */
	// @ResponseBody
	// @RequestMapping(value = "findOrgUsers")
	// public Result findByNameAndOrg(@RequestParam(value = "name") String name,
	// @RequestParam(value = "orgId") String orgId) {
	// List<SysUser> users = userService.findByNameAndOrg(name, orgId);
	// List<Map<String, Object>> list = Lists.newArrayList();
	// for (SysUser user : users) {
	// Map<String, Object> map = Maps.newHashMap();
	// map.put("id", user.getId());
	// map.put("name", user.getName());
	// map.put("loginName", user.getLoginName());
	// list.add(map);
	// }
	// return Result.resultInfo(ResultCode.SUCCESS, list);
	// }

	// /**
	// * 根据角色编码数组或登录名数据查询用户信息
	// *
	// * @param roleCodes
	// * @param loginNames
	// * @return
	// */
	// @ResponseBody
	// @RequestMapping(value = "listByRoleCodesOrLoginNames")
	// public Result listByRoleCodesOrLoginNames(@RequestParam(value =
	// "roleCodes", required = false) String[] roleCodes,
	// @RequestParam(value = "loginNames", required = false) String[]
	// loginNames) {
	// return Result.resultInfo(ResultCode.SUCCESS,
	// userService.findByRoleCodesOrLoginNames(roleCodes, loginNames));
	// }

	/**
	 * 查询分配给用户的菜单信息
	 * 
	 * @return
	 * @return List<Resource>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年4月20日 上午11:34:04
	 */
	// @ResponseBody
	// @RequestMapping(value = "menus")
	// public Result listReources(@RequestParam(value = "id") String id) {
	//// return Result.resultInfo(ResultCode.SUCCESS,
	// userAuthService.findResourcesByUser(userService.selectById(id)));
	// }

	// /**
	// * 查询分配给用户的授权信息
	// *
	// * @return
	// * @return List<Permission>
	// * @author: zhaojie/zh_jie@163.com
	// * @version: 2016年4月20日 上午11:34:24
	// */
	// @ResponseBody
	// @RequestMapping(value = "permissions")
	// public List<SysPermission> listPermissions(@RequestParam(value = "id")
	// String id) {
	// return userAuthService.findPermissionsByUser(userService.selectById(id));
	// }

	// /**
	// * 查询分配给用户的数据过滤信息
	// *
	// * @return
	// * @return List<DataFilter>
	// * @author: zhaojie/zh_jie@163.com
	// * @version: 2016年4月20日 上午11:34:35
	// */
	// @ResponseBody
	// @RequestMapping(value = "dataFilters")
	// public Result listDataFilters(@RequestParam(value = "id") String id) {
	// return Result.resultInfo(ResultCode.SUCCESS,
	// userAuthService.findDataFiltersByUser(userService.selectById(id)));
	// }

	// protected List<String> getFieldsFromRequest(SysUser m, HttpServletRequest
	// request) {
	// // 获得需要更新的属性名
	// List<String> fieldNames = new ArrayList<String>();
	// Set<String> paramNames = request.getParameterMap().keySet();
	// Field[] fields = m.getClass().getDeclaredFields();
	// for (Field field : fields) {
	// if (paramNames.contains(field.getName())) {
	// fieldNames.add(field.getName());
	// }
	// Class type = field.getType();
	// if (CustomBaseEntity.class.isAssignableFrom(type)) {
	// if (paramNames.contains(field.getName() + ".id")) {
	// fieldNames.add(field.getName() + ".id");
	// }
	// }
	// }
	// return fieldNames;
	// }

	// /**
	// * 分页查询
	// *
	// * @param pageSize
	// * @param pageNumber
	// * @param name
	// * 用户姓名
	// * @param loginName
	// * 登录名
	// * @param orgId
	// * 组织机构ID
	// * @param hasRole
	// * 角色ID
	// * @return
	// */
	// @ResponseBody
	// @RequestMapping(value = "listByRoleId")
	// public Result listByRoleId(int pageSize, int pageNumber, String name,
	// String loginName, String orgId,
	// String hasRole) {
	// PageDTO<SysUser> pageDTO = null;
	// try {
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("name", name);
	// map.put("orgId", orgId);
	// map.put("loginName", loginName);
	// map.put("hasRole", hasRole);
	// List<SysUser> list = userService.listByRoleId(map);
	// pageDTO = new PageTool<SysUser>().getPage(list, pageSize, pageNumber);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
	// }
	//
	// /**
	// * 返回单个实体的json数据
	// */
	// @ResponseBody
	// @RequestMapping(value = "data")
	// public Result viewData(@RequestParam(value = "id") String id) {
	// SysUser user = userService.selectById(id);
	// List<SysRole> roleList = roleService.findByUser(user.getId());
	// List<String> list = new ArrayList<String>();
	// for (SysRole role : roleList) {
	// list.add(role.getId());
	// }
	// user.setRoleCodes(list);
	// return Result.resultInfo(ResultCode.SUCCESS, user);
	// }

	// /**
	// * 返回单个实体的json数据
	// */
	// @ResponseBody
	// @RequestMapping(value = "dataByLook")
	// public Result dataByLook(@RequestParam(value = "id") String id) {
	// SysUser user = userService.selectById(id);
	// UserInfoVo userVo = new UserInfoVo();
	// userVo = userVo.changeEntity(user);
	// // 获取用户角色名称
	// List<SysRole> roleList = roleService.findByUser(user.getId());
	// List<String> roleNameList = null;
	// Set<String> set = new HashSet<String>();
	// for (SysRole r : roleList) {
	// set.add(r.getRoleName());
	// }
	// roleNameList = new ArrayList<String>(set);
	// userVo.setRoleName(roleNameList);
	// // 获取职务
	// // List<SysPostManage> postList =
	// // postManageService.findByUser(user.getId());
	// // List<String> postNameList = new ArrayList<String>();
	// // for(SysPostManage p:postList){
	// //
	// //// List<SysDict> list = dictService.findByCode(p.getPost());
	// //// if (list.size()>0) {
	// //// postNameList.add(list.get(0).getDictName());
	// //// }
	// //
	// // }
	// return Result.resultInfo(ResultCode.SUCCESS, userVo);
	// }

	@ResponseBody
	@RequestMapping(value = "batch/delete", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数据id", required = true, dataType = "String") })
	@ApiOperation("用户删除")
	public Result delete(@RequestParam(value = "id") String id) {
		// String operator = getUserInfo().getName();
		// for (String id : ids) {
		SysUser user = userService.selectById(id);
		user.setDeleted(true);
		boolean result = userService.updateAllColumnById(user);

		String operator = getUserInfo().getName();
		String info = "删除了" + user.getName() + "用户";
		saveOperatorInfo(info, StatusEnum.LogInfoType7.getIntValue(), operator);
		// }
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "系统参数错误");
		}

	}

	@ResponseBody
	@RequestMapping(value = "findAllUsers", method = { RequestMethod.POST })
	public Result findAllUsersByName(@RequestParam(value = "name") String name) {
		List<SysUser> users = userService.findAllUsersByName(name);
		List<Map<String, Object>> list = Lists.newArrayList();
		for (SysUser user : users) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", user.getId());
			map.put("name", user.getName());
			map.put("loginName", user.getLoginName());
			map.put("orgId", user.getOrgId());
			map.put("orgName", user.getOrgName());
			map.put("phoneNumber", user.getPhoneNumber());
			Integer sex = user.getSex();
			String sexName = "";
			if (sex == 1) {
				sexName = "男";
			} else if (sex == 0) {
				sexName = "女";
			}
			map.put("sexName", sexName);
			list.add(map);
		}
		return Result.resultInfo(ResultCode.SUCCESS, list);
	}

	/**
	 * 验证旧密码是否一致
	 * 
	 * @param oldPassword
	 *            旧密码
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "oldPasswordIsRight", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String") })
	@ApiOperation("旧密码验证")
	public Result oldPasswordIsRight(@RequestParam(value = "oldPassword") String oldPassword, HttpServletRequest req) {
		Result res = null;
		// 根据用ID查找出对应的用户
		SysUser findUser = getUserInfo();
		if (findUser != null) {
			// 判断原密码与新密码是否相同
			String oldPasswordmd5 = new SimpleHash("MD5", oldPassword, findUser.getSalt(),
					SysUserService.HASH_INTERATIONS).toString();

			if (oldPasswordmd5.equals(findUser.getPassword())) {
				res = Result.resultInfo(ResultCode.SUCCESS, null);
			} else {
				// 旧密码错误
				res = Result.resultInfo(ResultCode.OLD_PASSWORD_ERROR, null);
			}
		} else {
			res = Result.resultInfo(ResultCode.USER_NOT_LOGGED_IN, null);
		}
		return res;
	}

	@ResponseBody
	@RequestMapping(value = "/setStatus", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数据id", required = true, dataType = "String"),
			@ApiImplicitParam(name = "status", value = "是否可用 0可用，1不可用", required = true, dataType = "int") })
	@ApiOperation(value = "禁用/启用")
	public Result setStatus(String id, int status) {
		SysUser sysUser = userService.selectById(id);
		if (sysUser != null) {
			sysUser.setStatus(status);
			boolean result = userService.updateById(sysUser);
			String resultStr = "启用";
			if (status == 0) {
				resultStr = "禁用";
			}
			if (result) {
				return Result.resultInfo(ResultCode.SUCCESS, resultStr + "成功");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, resultStr + "失败");
			}
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "系统错误");
		}
	}


	@ResponseBody
	@RequestMapping(value = "/getUserListByRole", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String") })
	@ApiOperation(value = "根据角色获取用户列表")
	public Result getUserListByRole(@RequestParam(value = "roleId") String roleId,
									@RequestParam(value = "orgId",required = false)String orgId,
									@RequestParam(value = "pageNum")int pageNum,
									@RequestParam(value = "pageSize")int pageSize,
									@RequestParam(value = "name",required = false)String name) {
		List<SysUser> sysUserList = userService.getUserListByRole(roleId,orgId,name);
		PageDTO<SysUser> pageDTO = new PageTool<SysUser>().getPage(sysUserList, pageSize, pageNum);
		return Result.resultInfo(ResultCode.SUCCESS,sysUserList.size(),pageDTO.getData());
	}


	@ResponseBody
	@RequestMapping(value = "/getNoUserListByRoleOnOrg", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String") })
	@ApiOperation(value = "根据角色ID获取对应部门未拥有该角色的用户列表")
	public Result getNoUserListByRoleOnOrg(@RequestParam(value = "roleId") String roleId,
										   @RequestParam(value = "orgId",required = false)String orgId,
										   @RequestParam(value = "pageNum")int pageNum,
										   @RequestParam(value = "pageSize")int pageSize,
										   @RequestParam(value = "name",required = false)String name) {
		List<SysUser> sysUserList = userService.getNoUserListByRoleOnOrg(roleId,orgId,name);
		PageDTO<SysUser> pageDTO = new PageTool<SysUser>().getPage(sysUserList, pageSize, pageNum);
		return Result.resultInfo(ResultCode.SUCCESS,sysUserList.size(),pageDTO.getData());
	}


	@RequestMapping(value = "/sendRecoverEmail")
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "loginName", value = "账号", required = true, dataType = "String") })
	@ApiOperation(value = "找回密码给对应邮箱发送邮件")
	public Result sendRecoverEmail(@RequestParam(value = "loginName")String loginName,
								  @RequestParam(value = "ipAddress")String ipAddress){
		//查找是否存在该用户
		SysUser sysUser = userService.findByLoginName(loginName);
		if(sysUser == null){
			return Result.resultInfo(ResultCode.FAILURE,"账号不存在");
		}
		if(StringUtils.isEmpty(sysUser.getEmail())){
			return Result.resultInfo(ResultCode.FAILURE,"该账号未绑定邮箱，请联系系统管理员");
		}else{
			//生成密钥
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.MINUTE,5);
			String content = "{\"userId\":\""+sysUser.getId()+"\",\"endTime\":\""+calendar.getTime()+"\",\"code\":\"0\"}";
			try {
				SysConfig sysConfig = sysconfigService.getConfigData("2");
				String keys = AesUtil.toEncryption(content);
				SysEmail sysEmain = new SysEmail();
				sysEmain.setReceiver(sysUser.getEmail());
				sysEmain.setReceiverUserId(sysUser.getId());
				sysEmain.setKeys(keys);
				sysEmain.setState(0);
				sysEmain.setSubject(sysConfig.getConfigContent());
				sysEmain.setText("点击下方链接找回密码：\n" + ipAddress + "?key=" + keys);
				sysEmain.setType(1);
				sysEmain.setCreateTime(DateUtil.toStr(new Date()));
				sysEmailController.sendMail(sysEmain,"系统用户");


			} catch (Exception e) {
				e.printStackTrace();
			}
			return Result.resultInfo(ResultCode.SUCCESS,sysUser.getEmail());
		}
	}


	@RequestMapping(value = "/recoverPassword")
	@ResponseBody
	@ApiImplicitParams({ @ApiImplicitParam(name = "key", value = "密钥", required = true, dataType = "String") })
	@ApiOperation(value = "根据密钥修改密码")
	public Result recoverPassword(@RequestParam(value = "key")String key,
								  @RequestParam(value = "newPassword")String newPassword){
		try {
			String content = AesUtil.toDecrypt(key);
			if(!content.contains("code")){
				return Result.resultInfo(ResultCode.FAILURE,"密钥验证失败");
			}
			JSONObject jsonObject = JSONObject.parseObject(content);
			String code = jsonObject.getString("code");
			if(!"0".equals(code)){
				return Result.resultInfo(ResultCode.FAILURE,"密钥验证失败");
			}
			String endTimeStr = jsonObject.getString("endTime");
			Date endTime = new Date(endTimeStr);
			if(endTime.compareTo(new Date()) < 0){
				return Result.resultInfo(ResultCode.FAILURE,"已过期");
			}
			String userId = jsonObject.getString("userId");
			if(StringUtils.isEmpty(userId)){
				return Result.resultInfo(ResultCode.FAILURE,"密钥验证失败");
			}
			Wrapper wrapper = Condition.create().eq("receiver_user_id",userId).orderBy("create_time",false);
			SysEmail sysEmail = (SysEmail) sysEmailService.selectList(wrapper).get(0);
			if(!key.equals(sysEmail.getKeys())){
				return Result.resultInfo(ResultCode.FAILURE,"密钥验证失败,请点击最新链接");
			}
			SysUser sysUser = userService.selectById(userId);
			//验证旧密码是否一致
			/*String password = new SimpleHash("MD5",oldPassword, sysUser.getSalt(), SysUserService.HASH_INTERATIONS).toString();
			if(!password.equals(sysUser.getPassword())){
				return Result.resultInfo(ResultCode.FAILURE,"原密码错误");
			}*/
			//修改密码
			userService.updatePassword(sysUser, newPassword);
			return Result.resultInfo(ResultCode.SUCCESS,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.resultInfo(ResultCode.SUCCESS,"修改密码失败");
		}
	}
}
