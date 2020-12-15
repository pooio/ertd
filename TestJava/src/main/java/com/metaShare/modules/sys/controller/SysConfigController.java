package com.metaShare.modules.sys.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.service.SysconfigService;

import ch.qos.logback.classic.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Created by pc on 2020/1/12.
 */
@Controller
@CrossOrigin
@Api(tags = "系統配置")
@RequestMapping("/api/sys/sysConfig")
public class SysConfigController extends BaseController {
	Logger logger = (Logger) LoggerFactory.getLogger(SysConfigController.class);
	@Autowired
	private SysconfigService sysconfigService;

	@ResponseBody
	@ApiOperation(value = "列表数据")
	@RequestMapping(value = "/list", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "int"),
			@ApiImplicitParam(name = "configType", value = "参数类型", required = true, dataType = "String") })
	public Result list(int pageNum, int pageSize, String configType) {
		PageDTO pageDTO = null;
		List<SysConfig> listInfo = sysconfigService.getList(configType,"");
		int total = listInfo.size();
		pageDTO = new PageTool<SysConfig>().getPage(listInfo, pageSize, pageNum);
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	
	@ResponseBody
	@ApiOperation(value = "获取全部数据")
	@RequestMapping(value = "/allList", method = { RequestMethod.POST })
	 public Result allList( String configType) {
		String allData="all";
		List<SysConfig> listInfo = sysconfigService.getList(configType,allData);
		int total = listInfo.size();
		return Result.resultInfo(ResultCode.SUCCESS, total, listInfo);
	}
	
	
	
	@ResponseBody
	@ApiOperation(value = "系统配置字典")
	@RequestMapping(value = "/getSelectList", method = { RequestMethod.POST })
	public Result getSelectList() {
		List map = StatusEnum.findEnumList(StatusEnum.SysConfigType);
		return Result.resultInfo(ResultCode.SUCCESS, map);
	}

	@ResponseBody
	@ApiOperation(value = "新增系统配置")
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "configContent", value = "配置内容", required = true, dataType = "String"),
			@ApiImplicitParam(name = "configType", value = "配置类型", required = true, dataType = "String") })
	public Result save(SysConfig sysConfig) {
		// 判断系统配置是否已经存在
		Wrapper wrapper = Condition.create().eq("config_type", sysConfig.getConfigType());
		int count = sysconfigService.selectCount(wrapper);
		if (count > 0) {
			return Result.resultInfo(ResultCode.VAIL_ERROR, "系统配置已经存在");
		}
		sysConfig.setUpdateTime(DateUtil.getDate(DateUtil.timeStampPattern));
		boolean result = sysconfigService.insert(sysConfig);
		String operator = getUserInfo().getName();
		String info = "新增" + StatusEnum.findEnumDescByValue(StatusEnum.SysConfigType, sysConfig.getConfigType()) ;
		saveOperatorInfo(info, StatusEnum.LogInfoType2.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "添加成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "添加失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "修改系统配置")
	@RequestMapping(value = "/updata", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
			@ApiImplicitParam(name = "configContent", value = "配置内容", required = true, dataType = "String"),
			@ApiImplicitParam(name = "configType", value = "配置类型", required = true, dataType = "String") })
	public Result updata(SysConfig sysConfig) {
		Wrapper wrapper = Condition.create().eq("config_type", sysConfig.getConfigType()).ne("id", sysConfig.getId());
		int count = sysconfigService.selectCount(wrapper);
		if (count > 0) {
			return Result.resultInfo(ResultCode.VAIL_ERROR, "系统配置已经存在");
		}
		sysConfig.setUpdateTime(DateUtil.getDate(DateUtil.timeStampPattern));
		boolean result = sysconfigService.updateById(sysConfig);
		String info = "修改" + StatusEnum.findEnumDescByValue(StatusEnum.SysConfigType, sysConfig.getConfigType());
		String operator = getUserInfo().getName();
		saveOperatorInfo(info, StatusEnum.LogInfoType2.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "编辑成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "编辑失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "刪除系统配置")
	@RequestMapping(value = "/del", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String") })
	public Result del(String id) {
		SysConfig sysConfig = sysconfigService.selectById(id);
		boolean result = sysconfigService.deleteById(id);
		if (sysConfig != null) {
			String info = "删除" + StatusEnum.findEnumDescByValue(StatusEnum.SysConfigType, sysConfig.getConfigType())
					+ ":" + sysConfig.getConfigContent();
			String operator = getUserInfo().getName();
			saveOperatorInfo(info, StatusEnum.LogInfoType2.getIntValue(), operator);
		}
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "是否启用")
	@RequestMapping(value = "/setInUse", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
			@ApiImplicitParam(name = "inUse", value = "1 启用， 0 禁用", required = true, dataType = "int"), })
	public Result setInUse(String id, int inUse) {
		SysConfig sysConfig = sysconfigService.selectById(id);
		String resultStr = "系统配置禁用";
		boolean result = false;
		if (sysConfig != null) {
			sysConfig.setInUse(inUse);
			result = sysconfigService.updateById(sysConfig);
			if (inUse == 1) {
				resultStr = "系统配置启用";
			}
		}
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, resultStr + "成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, resultStr + "失败");
		}
	}

	@ResponseBody
	@ApiOperation(value = "文件上传地址")
	@RequestMapping(value = "/getConfigData", method = { RequestMethod.POST })
	public Result getConfigData() {
		SysConfig sysConfig = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());
		return Result.resultInfo(ResultCode.SUCCESS, sysConfig);
	}

	@ResponseBody
	@ApiOperation(value = "系统参数")
	@RequestMapping(value = "/getSysConfig", method = { RequestMethod.POST })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "configType", value = "1:系统标题 ，2系统描述,3系统版权  ，4 系统搜索关键字  7 图片路由", required = true, dataType = "String") })
	public Result getSysConfig(String configType) {
		SysConfig sysConfig = sysconfigService.getConfigData(configType);
		return Result.resultInfo(ResultCode.SUCCESS, sysConfig);
	}
	// @ResponseBody
	// @ApiOperation(value = "文件访问地址")
	// @RequestMapping(value = "/getFilePath", method = {RequestMethod.POST})
	// public Result getFilePath() {
	// SysConfig sysConfig =
	// sysconfigService.getConfigData(StatusEnum.SysConfigType6.getStrValue());
	// return Result.resultInfo(ResultCode.SUCCESS, sysConfig);
	// }

}
