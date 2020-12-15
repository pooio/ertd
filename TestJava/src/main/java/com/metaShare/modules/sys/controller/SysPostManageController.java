package com.metaShare.modules.sys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysPostManage;
import com.metaShare.modules.sys.service.SysPostManageService;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("api/sys/post")
@Api(tags = "职务管理")
public class SysPostManageController extends BaseController {
	@Autowired
	SysPostManageService postManageService;
	@Autowired
	SysUserService sysUserService;

	@ResponseBody
	@ApiOperation(value = "列表数据")
	@RequestMapping(value = "/list", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "int"),
			@ApiImplicitParam(name = "postName", value = "职务名称或编码", required = true, dataType = "String") })
	public Result list(int pageNum, int pageSize, String postName) {
		PageDTO<SysPostManage> pageDTO = null;
		List<SysPostManage> list = postManageService.findPostManageList(postName);
		pageDTO = new PageTool<SysPostManage>().getPage(list, pageSize, pageNum);
		int total = list.size();
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	@ResponseBody
	@ApiOperation(value = "全部数据")
	@RequestMapping(value = "/allList", method = { RequestMethod.POST })
	public Result allList() {
		List<SysPostManage> ListSysPostManage = postManageService.findPostManageAllList();
		return Result.resultInfo(ResultCode.SUCCESS, ListSysPostManage);
	}

	@ResponseBody
	@ApiOperation(value = "新增职务")
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "post", value = "职务名称", required = true, dataType = "String"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postCode", value = "职务编码", required = true, dataType = "String") })
	public Result save(SysPostManage sysPostManage) {
		sysPostManage.setCreateTime(DateUtil.getDate(DateUtil.timeStampPattern));

		String operator = getUserInfo().getName();
		String info = "新增了" + sysPostManage.getPost() + "职务";
		saveOperatorInfo(info, StatusEnum.LogInfoType6.getIntValue(), operator);
		boolean result = postManageService.insert(sysPostManage);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "新增失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "修改职务")
	@RequestMapping(value = "/updata", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数据ID", required = true, dataType = "String"),
			@ApiImplicitParam(name = "post", value = "职务名称", required = true, dataType = "String"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postCode", value = "职务编码", required = true, dataType = "String") })
	public Result updata(SysPostManage sysPostManage) {
		sysPostManage.setUpdateTime(DateUtil.getDate(DateUtil.timeStampPattern));
		boolean result = postManageService.insertOrUpdate(sysPostManage);

		String operator = getUserInfo().getName();
		String info = "修改了" + sysPostManage.getPost() + "职务";
		saveOperatorInfo(info, StatusEnum.LogInfoType6.getIntValue(), operator);

		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "删除")
	@RequestMapping(value = "/del", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "数组ID", required = true, dataType = "String") })
	public Result del(String id) {

		Wrapper wrapper = Condition.create().eq("deleted", false);
		wrapper.eq("post_id", id);
		int count = sysUserService.selectCount(wrapper);
		if (count > 0) {
			return Result.resultInfo(ResultCode.VAIL_ERROR, "职务下有人员,不能删除");
		}
		String[] ids = id.split(",");
		boolean result = false;
		for (String string : ids) {
			SysPostManage postManage = postManageService.selectById(string);
			if (postManage != null) {
				postManage.setDeleted(true);
				result = postManageService.updateById(postManage);

				String operator = getUserInfo().getName();
				String info = "删除了" + postManage.getPost() + "职务";
				saveOperatorInfo(info, StatusEnum.LogInfoType6.getIntValue(), operator);
			}

		}
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "职务编码验证")
	@RequestMapping(value = "/checkPostCode", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "ID", required = false, dataType = "String"),
			@ApiImplicitParam(name = "postCode", value = "职务编码", required = true, dataType = "String") })
	public Result checkPostCode(String id, String postCode) {
		SysPostManage oldSysPostManage = getPostCode(postCode);
		boolean result = (oldSysPostManage == null || oldSysPostManage.getId().equals(id));
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, result);
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "编码已经存在");
		}
	}

	public SysPostManage getPostCode(String postCode) {
		List<SysPostManage> list = postManageService.getPostCode(postCode);
		SysPostManage sysPostManage = null;
		if (list.size() > 0) {
			sysPostManage = list.get(0);
		}
		return sysPostManage;
	}

	@ResponseBody
	@ApiOperation(value = "职务名称验证")
	@RequestMapping(value = "/checkPostName", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "ID", required = false, dataType = "String"),
			@ApiImplicitParam(name = "post", value = "职务名称", required = true, dataType = "String") })
	public Result checkPostName(String id, String post) {
		SysPostManage oldSysPostManage = getPostName(post);
		boolean result = (oldSysPostManage == null || oldSysPostManage.getId().equals(id));
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, result);
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "职务名称已经存在");
		}
	}

	public SysPostManage getPostName(String post) {
		List<SysPostManage> list = postManageService.getPostName(post);
		SysPostManage sysPostManage = null;
		if (list.size() > 0) {
			sysPostManage = list.get(0);
		}
		return sysPostManage;
	}

}
