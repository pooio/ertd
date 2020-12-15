package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysPermission;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysPermissionService;

import io.swagger.annotations.Api;

@Controller
@RequestMapping("api/sys/permission")
@Api(tags = "权限管理")
public class SysPermissionController extends BaseController {

	@Autowired
	private SysPermissionService permissionService;

	@ResponseBody
	@RequestMapping(value = "batchSave")
	public Result batchSave(@RequestParam(value = "permissionStr") String permissionStr,
							@RequestParam(value = "permissionDesc") String permissionDesc) {
		String operator = getUserInfo().getName();
		if (checkPermissionStr(permissionStr)) {
			return Result.resultInfo(ResultCode.FAILURE, "验证失败");
		}
		
		List<SysPermission> list = new ArrayList<SysPermission>();

		SysPermission permissionCreate = new SysPermission();
		permissionCreate.setPermissionStr(permissionStr + ":create");
		permissionCreate.setPermissionDesc(permissionDesc + "：新增");
		permissionCreate.setCreateTime(new Date());
		list.add(permissionCreate);
		SysPermission permissionUpdate = new SysPermission();
		permissionUpdate.setPermissionStr(permissionStr + ":update");
		permissionUpdate.setPermissionDesc(permissionDesc + "：修改");
		permissionUpdate.setCreateTime(new Date());
		list.add(permissionUpdate);
		SysPermission permissionView = new SysPermission();
		permissionView.setPermissionStr(permissionStr + ":view");
		permissionView.setPermissionDesc(permissionDesc + "：查看");
		permissionView.setCreateTime(new Date());
		list.add(permissionView);
		SysPermission permissionDelete = new SysPermission();
		permissionDelete.setPermissionStr(permissionStr + ":delete");
		permissionDelete.setPermissionDesc(permissionDesc + "：删除");
		permissionDelete.setCreateTime(new Date());
		list.add(permissionDelete);
		SysPermission permissionAll = new SysPermission();
		permissionAll.setPermissionStr(permissionStr + ":*");
		permissionAll.setCreateTime(new Date());
		permissionAll.setPermissionDesc(permissionDesc + "：所有权限");
		list.add(permissionAll);
		permissionService.insertBatch(list);
/*		String info = "新增了" + permissionDesc + "相关的授权信息";
		saveOperatorInfo(info, 5, operator);*/
		return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
	}

	private boolean checkPermissionStr(String permissionStr) {
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.like("permission_str", permissionStr+":");
		int count = permissionService.selectCount(wrapper);
		if (count>0) {
			return true;
		}
		return false;
	}

	// 保存对象
	@RequestMapping(value = "/save")
	@ResponseBody
	public Result saveDict(SysPermission sysPermission) {
		if (StringUtils.isEmpty(sysPermission.getId())) {
			sysPermission.setCreateTime(new Date());
		}
		permissionService.insertOrUpdate(sysPermission);
		return Result.resultInfo(ResultCode.SUCCESS, "成功");
	}

	// 修改对象
	@RequestMapping(value = "/update")
	@ResponseBody
	public Result update(SysPermission sysPermission) {
		String operator = getUserInfo().getName();
		permissionService.updateAllColumnById(sysPermission);
/*		String info = "修改了" + sysPermission.getPermissionDesc() + "相关的授权信息";
		saveOperatorInfo(info, 5, operator);*/
		return Result.resultInfo(ResultCode.SUCCESS, "成功");
	}

	// 删除对象
	@RequestMapping(value = "/deleted")
	@ResponseBody
	public Result deleted(String[] ids) {
		String operator = getUserInfo().getName();
		for(String id:ids){
			SysPermission sysPermission = permissionService.selectById(id);
			sysPermission.setDeleted(true);
			permissionService.updateAllColumnById(sysPermission);
/*			String info = "删除了" + sysPermission.getPermissionDesc() + "的授权信息";
			saveOperatorInfo(info, 5, operator);*/
		}
		return Result.resultInfo(ResultCode.SUCCESS, "成功");
	}

	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "data")
	public Result viewData(@RequestParam(value = "id") String id) {
		return Result.resultInfo(ResultCode.SUCCESS, permissionService.selectById(id));
	}

	// 分页查询
	@ResponseBody
	@RequestMapping(value = "list")
	public Result listData(int pageSize, int pageNumber,String permissionStr,String permissionDesc) {
		PageDTO<SysPermission> pageDTO = null;
		try {
			Wrapper wrapper = Condition.create().eq("deleted", false);
			if (StringUtils.isNotEmpty(permissionStr)) {
				wrapper.like("permission_str", permissionStr);
			}
			if (StringUtils.isNotEmpty(permissionDesc)) {
				wrapper.like("permission_desc", permissionDesc);
			}
			wrapper.orderBy("create_time", false);
			
			List<SysPermission> list = permissionService.selectList(wrapper);
			pageDTO = new PageTool<SysPermission>().getPage(list, pageSize, pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
	}
	
	// 分页查询
		@ResponseBody
		@RequestMapping(value = "listByRoleId")
		public Result listByRoleId(int pageSize, int pageNumber,String permissionStr,String permissionDesc,String hasRole) {
			PageDTO<SysPermission> pageDTO = null;
			try {
				Map<String,String> map = new HashMap<String,String>();
				map.put("permissionStr", permissionStr);
				map.put("permissionDesc", permissionDesc);
				map.put("hasRole", hasRole);
				List<SysPermission> list = permissionService.listByRoleId(map);
				pageDTO = new PageTool<SysPermission>().getPage(list, pageSize, pageNumber);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
		}
	
	/**
	 * 授权字符串是否可用
	 * @param permissionStr 授权字符串
	 * @param id id
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value="checkPermissionStr")
	public Result checkPermissionStr(@RequestParam(value="permissionStr",required=true) String permissionStr,
			@RequestParam(value="id",required=true) String id) throws Exception{
		return Result.resultInfo(ResultCode.SUCCESS, permissionService
				.checkcheckPermissionStr(permissionStr, id));
	}
}
