package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysRoleType;
import com.metaShare.modules.sys.service.SysRoleTypeService;

import io.swagger.annotations.Api;

@Controller
@RequestMapping("api/sys/roleType")
@Api(tags = "角色类型")
public class SysRoleTypeController extends BaseController {
	@Autowired
	private SysRoleTypeService roleTypeService;

	// 保存对象
	@RequestMapping(value = "/save",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "typeName",value = "类型名称",required = false,dataType = "String"),
			@ApiImplicitParam(name = "sort",value = "排序",required = false,dataType = "int"),
			@ApiImplicitParam(name = "remark",value = "备注",required = false,dataType = "String"),
			@ApiImplicitParam(name = "parentId",value = "父节点ID",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "新增角色类型")
	public Result save(SysRoleType roleType) {
		boolean flag = roleTypeService.insert(roleType);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "新增失败");
		}

	}

	// 修改对象
	@RequestMapping(value = "/update",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "typeName",value = "类型名称",required = false,dataType = "String"),
			@ApiImplicitParam(name = "sort",value = "排序",required = false,dataType = "int"),
			@ApiImplicitParam(name = "remark",value = "备注",required = false,dataType = "String"),
			@ApiImplicitParam(name = "parentId",value = "父节点ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "id",value = "id",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "编辑角色类型")
	public Result update(SysRoleType roleType) {
		boolean flag = roleTypeService.updateAllColumnById(roleType);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "编辑成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "编辑失败");
		}
	}

	// 删除对象
	@RequestMapping(value = "/deleted",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "id",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "删除角色类型")
	public Result deleted(String id) {
		SysRoleType dict = roleTypeService.selectById(id);
		dict.setDeleted(true);
		//逻辑删除
		boolean flag = roleTypeService.updateAllColumnById(dict);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
	}

	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "data",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "id",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "获取角色类型信息")
	public Result viewData(String id) {
		return Result.resultInfo(ResultCode.SUCCESS, roleTypeService.selectById(id));
	}

	// 分页查询
	@ResponseBody
	@RequestMapping(value = "lists",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页展示个数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "页码",required = true,dataType = "int",paramType = "query")
	})
	@ApiOperation(value = "获取角色类型列表")
	public Result listData(int pageSize, int pageNum) {
		PageDTO<SysRoleType> pageDTO = null;
		try {
			Wrapper wrapper = Condition.create().eq("deleted", false).orderBy("createTime", true);
			List<SysRoleType> list = roleTypeService.selectList(wrapper);
			pageDTO = new PageTool<SysRoleType>().getPage(list, pageSize, pageNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
	}

	// 加载树
	@ResponseBody
	@RequestMapping(value = "treeData")
	public Result treeData() {
		//获取了1级节点，根据父子关系获得关联
		SysRoleType sysRoleType = roleTypeService.selectTopResource();
		List<SysRoleType> secondResources = roleTypeService.selectChildrenResources(sysRoleType.getId());
		List<SysRoleType> childrenList = roleTypeService.getChildrenList(secondResources);
		sysRoleType.setChildren(childrenList);
		List<SysRoleType> normalList = new ArrayList<>();
		normalList.add(sysRoleType);
		return Result.resultInfo(ResultCode.SUCCESS, normalList);
	}
}
