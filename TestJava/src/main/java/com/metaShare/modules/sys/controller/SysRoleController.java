package com.metaShare.modules.sys.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.entity.SysRoleOrg;
import com.metaShare.modules.sys.entity.SysRoleResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("api/sys/role")
@Api(tags = "角色管理")
public class SysRoleController extends BaseController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleUserService sysRoleUserService;
	@Autowired
	private SysRoleDataFilterService sysRoleDataFilterService;
	@Autowired
	private SysRoleOrgService sysRoleOrgService;
	@Autowired
	private SysRolePermissionService sysRolePermissionService;
	@Autowired
	private SysRoleResourceService sysRoleResourceService;
	@Autowired
	private SysUserService sysUserService;

	/**
	 * 检查角色编码是否合法
	 * 
	 * @param roleId
	 * @param roleCode
	 * @return
	 * @throws Exception
	 * @return Boolean
	 */
	@ResponseBody
	@RequestMapping(value = "checkRoleCode", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "roleId", value = "角色Id", required = false, dataType = "String"),
			@ApiImplicitParam(name = "roleCode", value = "角色编码", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "判断角色编码唯一性")
	public Result checkRoleCode(String roleId, String roleCode) throws Exception {
		boolean flag = sysRoleService.checkRoleCode(roleCode, roleId);
		if (flag) {
			return Result.resultInfo(ResultCode.SUCCESS, "该角色编码可使用");
		} else {
			return Result.resultInfo(ResultCode.DATA_ALREADY_EXISTED, "该角色编码已存在");
		}
	}

	/**
	 * 根据用户id查询角色信息
	 * 
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2015年12月10日 下午4:45:43
	 */
	@ResponseBody
	@RequestMapping(value = "getRoleByUser", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "根据用户查询角色信息")
	public Result findUserRoles(String userId) {
		return Result.resultInfo(ResultCode.SUCCESS, sysRoleService.findByUser(userId));
	}

	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "data", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "获取角色信息")
	public Result viewData(String id) {
		return Result.resultInfo(ResultCode.SUCCESS, sysRoleService.selectById(id));
	}

	/**
	 * 为角色添加菜单
	 */
	@ResponseBody
	@RequestMapping(value = "addMenusForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "menuIds", value = "菜单Id", required = true, dataType = "String[]", paramType = "query") })
	@ApiOperation(value = "为角色添加菜单")
	public Result addMenusForRole(String roleId, String[] menuIds) {
		sysRoleService.saveMenusForRole(roleId, menuIds);
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		String info = "为" + sysRole.getRoleName()+ "角色添加菜单";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "添加菜单成功");
	}

	/**
	 * 为角色添加按钮
	 */
	@ResponseBody
	@RequestMapping(value = "addBtnForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "btnName", value = "按钮名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "menuIds", value = "菜单IDS", required = true, dataType = "String", paramType = "query")})
	@ApiOperation(value = "为角色添加按钮菜单")
	public Result addBtnForRole(String roleId, String btnName,String menuIds) {
		String[] btnArray = null;
		if(StringUtils.isNotEmpty(btnName)){
			btnArray = btnName.split(",");
		}
		sysRoleService.saveBtnForRole(roleId, btnArray);
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		if(StringUtils.isNotEmpty(menuIds)){
			String[] menuId = menuIds.split(",");
			addMenusForRole(roleId,menuId);
		}
		String info = "为" + sysRole.getRoleName()+ "角色添加按钮";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "添加按钮成功");
	}


	/**
	 * 为角色添加授权
	 */
	@ResponseBody
	@RequestMapping(value = "savePermissionsForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "permissionIds", value = "授权Id", required = true, dataType = "String[]", paramType = "query") })
	@ApiOperation(value = "为角色添加授权")
	public Result savePermissionsForRole(String roleId, String[] permissionIds) {
		// String operator = getUserInfo().getName();
		sysRoleService.savePermissionsForRole(roleId, permissionIds);
		/*
		 * String info = "为角色添加了授权"; saveOperatorInfo(info, 5, operator);
		 */
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		String info = "为" + sysRole.getRoleName()+ "角色添加授权";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "添加授权成功");
	}

	/**
	 * 为角色删除授权信息
	 */
	@ResponseBody
	@RequestMapping(value = "deletePermissionsForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "permissionIds", value = "授权Id", required = true, dataType = "String[]", paramType = "query") })
	@ApiOperation(value = "为角色删除授权")
	public Result deletePermissionsForRole(String roleId, String[] permissionIds) {
		// String operator = getUserInfo().getName();
		sysRoleService.deletePermissionsForRole(roleId, permissionIds);
		/*
		 * String info = "为角色删除了授权"; saveOperatorInfo(info, 5, operator);
		 */
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		String info = "对" + sysRole.getRoleName()+ "角色进行了删除授权操作";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "删除授权成功");
	}

	/**
	 * 为角色添加数据过滤信息
	 *
	 * @param roleId
	 * @param dataFilterIds
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2015年12月30日 上午11:28:18
	 */
	@ResponseBody
	@RequestMapping(value = "saveDataFiltersForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "dataFilterIds", value = "数据过滤Id", required = true, dataType = "String[]", paramType = "query") })
	@ApiOperation(value = "为角色添加数据过滤")
	public Result saveDataFiltersForRole(String roleId, String[] dataFilterIds) {
		// String operator = getUserInfo().getName();
		Map<String, Object> map = new HashMap<String, Object>();
		sysRoleService.saveDataFiltersForRole(roleId, dataFilterIds);
		/*
		 * String info = "为角色添加数据过滤信息"; saveOperatorInfo(info, 5, operator);
		 */
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		String info = "为" + sysRole.getRoleName()+ "角色添加数据过滤范围";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "添加数据过滤信息成功");
	}

	/**
	 * 为角色删除数据过滤信息
	 * 
	 * @param roleId
	 * @param dataFilterIds
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2015年12月30日 上午11:28:32
	 */
	@ResponseBody
	@RequestMapping(value = "deleteDataFiltersForRole", method = { RequestMethod.POST })
	public Result deleteDataFiltersForRole(@RequestParam(value = "roleId") String roleId,
			@RequestParam(value = "dataFilterIds") String[] dataFilterIds) {
		// String operator = getUserInfo().getName();
		sysRoleService.deleteDataFiltersForRole(roleId, dataFilterIds);
		/*
		 * String info = "为角色删除数据过滤信息"; saveOperatorInfo(info, 5, operator);
		 */
		return Result.resultInfo(ResultCode.SUCCESS, "删除数据过滤信息成功");
	}

	/**
	 * 为角色添加用户
	 * 
	 * @param roleId
	 * @param userIds
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年1月8日 下午5:18:46
	 */
	@ResponseBody
	@RequestMapping(value = "saveUsersForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
				@ApiImplicitParam(name = "userIds", value = "用户Id", required = true, dataType = "String[]", paramType = "query") })
	@ApiOperation(value = "为角色添加用户")
	public Result saveUsersForRole(String roleId, String[] userIds) {
		sysRoleService.saveUsersForRole(roleId, userIds);
		SysRole sysRole = sysRoleService.selectById(roleId);
		String operator = getUserInfo().getName();
		String info = "为" + sysRole.getRoleName()+ "角色添加用户";
		saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
		return Result.resultInfo(ResultCode.SUCCESS, "为角色添加用户成功");
	}

	/**
	 * 为角色删除用户
	 * 
	 * @param roleId
	 * @param userId
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年1月8日 下午5:18:54
	 */
	@ResponseBody
	@RequestMapping(value = "deleteUsersForRole", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleId", value = "角色Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "userIds", value = "用户Id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "为角色删除用户")
	public Result deleteUsersForRole(String roleId, String userId) {
		boolean flag = sysRoleService.deleteUsersForRole(roleId, userId);
		if (flag) {
			SysUser sysUser = sysUserService.selectById(userId);
			SysRole sysRole = sysRoleService.selectById(roleId);
			String operator = getUserInfo().getName();
			String info = "删除了" + sysRole.getRoleName() + "角色下的\"" + sysUser.getName() + "\"用户";
			saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}

	}

	// 分页查询
	@ResponseBody
	@RequestMapping(value = "list", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页展示个数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "orgName", value = "类型Id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "获取角色信息列表")
	public Result listData(int pageSize, int pageNum, String orgName,String orgCode) {
		PageDTO<SysRole> pageDTO = null;
		int total = 0;
		try {
			List<SysRole> list = sysRoleService.selectListByTypeId(orgName,orgCode);
			total = list.size();
			pageDTO = new PageTool<SysRole>().getPage(list, pageSize, pageNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	@RequestMapping(value = "allList", method = { RequestMethod.POST })
	@ApiOperation(value = "所有可用角色")
	@ResponseBody
	public Result allList() {
		List<SysRole> list = sysRoleService.allList();
		return Result.resultInfo(ResultCode.SUCCESS, list);
	}

	// 保存对象
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleName", value = "角色名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "roleCode", value = "角色编码", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "typeId", value = "角色类型ID", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String") })
	@ApiOperation(value = "获取角色信息列表")
	public Result saveRole(SysRole sysRole) {
		sysRole.setCreateTime(new Date());
		boolean flag = sysRoleService.insert(sysRole);
		if (flag) {
			String operator = getUserInfo().getName();
			String info = "新增角色：" + sysRole.getRoleName();
			saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "新增角色成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "新增角色失败");
		}

	}

	// 编辑对象
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "roleName", value = "角色名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "roleCode", value = "角色编码", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "typeId", value = "角色类型ID", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String"),
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "编辑角色信息")
	public Result updateRole(SysRole sysRole) {
		boolean flag = sysRoleService.updateById(sysRole);
		if (flag) {
			String operator = getUserInfo().getName();
			String info = "编辑了" + sysRole.getRoleName()+ "角色";
			saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "编辑角色成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "编辑角色失败");
		}
	}

	// 删除角色
	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "id", required = true, dataType = "String", paramType = "query") })
	@ApiOperation(value = "删除角色")
	public Result deleteRole(String id) {
		SysRole sysRole = sysRoleService.selectById(id);
		boolean flag = false;
		if (sysRole != null) {
			sysRole.setDeleted(true);
			flag = sysRoleService.updateById(sysRole);
		}
		if(flag){
			//删除角色与用户的关系
			sysRoleUserService.deleteByRoleId(id);
			//删除角色与菜单的关系
			sysRoleResourceService.deleteByRoleId(id);
			//删除角色与数据过滤的关系
			sysRoleDataFilterService.deleteByRoleId(id);
			//删除角色与权限的关系
			sysRolePermissionService.deleteByRoleId(id);
		}

		if (flag) {
			String operator = getUserInfo().getName();
			String info = "删除角色：" + sysRole.getRoleName();
			saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "删除角色成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除角色失败");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/setInUse", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数据id", required = true, dataType = "String"),
			@ApiImplicitParam(name = "inUse", value = "是否可用 1可用，0不可用", required = true, dataType = "int") })
	@ApiOperation(value = "禁用/启用")
	public Result setInUse(String id, int inUse) {
		SysRole sysRole = sysRoleService.selectById(id);
		if (sysRole != null) {
			sysRole.setInUse(inUse);
			boolean result = sysRoleService.updateById(sysRole);
			String resultStr = "禁用";
			if (inUse == 1) {
				resultStr = "启用";
			}
			if (result) {
				String operator = getUserInfo().getName();
				String info = resultStr  + sysRole.getRoleName() + "角色";
				saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
				return Result.resultInfo(ResultCode.SUCCESS, resultStr + "成功");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, resultStr + "失败");
			}
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "系统错误");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getRoleResource", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "String") })
	@ApiOperation(value = "根据角色ID获取，获取已经授权的菜单ID")
	public Result getRoleResource(String id) {
		List<SysRoleResource> list = sysRoleService.getRoleResource(id);
		String resourceIds = "";
		for (SysRoleResource sysRoleResource : list) {
			resourceIds += sysRoleResource.getResourceId() + ",";
		}
		if (list.size() > 0) {
			resourceIds = resourceIds.substring(0, resourceIds.length() - 1);
		}

		return Result.resultInfo(ResultCode.SUCCESS, resourceIds);
	}

	@ResponseBody
	@RequestMapping(value = "/saveRoleOrg", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "String"),
			@ApiImplicitParam(name = "roleType", value = "数据类型", required = true, dataType = "String"),
			@ApiImplicitParam(name = "orgIds", value = "组织机构ids", required = false, dataType = "String") })
	@ApiOperation(value = "数据授权保存")
	public Result saveRoleOrg(String id, String roleType, String[] orgIds) {
		boolean flag = sysRoleService.saveRoleOrg(id, roleType, orgIds);
		if (flag) {
			SysRole sysRole = sysRoleService.selectById(id);
			String operator = getUserInfo().getName();
			String info = "为" + sysRole.getRoleName() + "添加数据授权";
			saveOperatorInfo(info, StatusEnum.LogInfoType12.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "数据授权成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "数据授权失败");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getRoleOrg", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "String") })
	@ApiOperation(value = "数据权限组织机构")
	public Result getRoleOrg(String id) {
		List<SysRoleOrg> list = sysRoleService.getRoleOrg(id);
		String resourceIds = "";
		for (SysRoleOrg sysRoleResource : list) {
			resourceIds += sysRoleResource.getOrgId() + ",";
		}
		if (list.size() > 0) {
			resourceIds = resourceIds.substring(0, resourceIds.length() - 1);
		}

		return Result.resultInfo(ResultCode.SUCCESS, resourceIds);
	}
}
