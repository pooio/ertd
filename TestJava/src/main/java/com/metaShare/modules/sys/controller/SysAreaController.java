/**  
 * @Title: SysAreaController.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:56 
 */ 
package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.dao.SysAreaDao;
import com.metaShare.modules.sys.entity.SysArea;
import com.metaShare.modules.sys.service.SysAreaService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**  
 * @ClassName: SysAreaController
 * @Description: 区域管理
 * @author eric.xi
 * @date 2020-02-13 10:05:56 
*/
@Controller
@CrossOrigin
@Api(tags = "区域管理")
@RequestMapping(value = "/api/sys/area")
public class SysAreaController extends BaseController {

	@Autowired
	private SysAreaService sysAreaService;
	@Autowired
	private SysAreaDao sysAreaDao;
	
	@RequestMapping(value = "/treeData", method = {RequestMethod.POST})
    @ApiOperation(value="数据树节点数据")
	@ResponseBody
	public Result treeData() {
		List<SysArea> normalList = new ArrayList<>();
		SysArea data = recursiveTree("0");
		normalList.add(data);
		return Result.resultInfo(ResultCode.SUCCESS,normalList);
	}
	public SysArea recursiveTree(String id) {
		// 根据cid获取节点对象
		SysArea node = sysAreaService.selectById(id);
		if(node== null){
			node = new SysArea();
			node.setId(1);
			node.setAreaCode("0");
			node.setAreaName("中华人民共和国");
			node.setAreaType("0");
		}
		// 查询cid下的所有子节点
		List<SysArea> childTreeNodes = sysAreaDao.findareaList(node.getAreaCode(),"");
		// 遍历子节点
		for (SysArea child : childTreeNodes) {
			if(Integer.valueOf(child.getAreaType())<2){
				SysArea n = recursiveTree(child.getId()+""); // 递归
				node.getChildren().add(n);
			}
			
		}
//
		return node;
	}
	
	@ResponseBody
	@RequestMapping(value = "/list", method = {RequestMethod.POST})
	@ApiImplicitParams({
    @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "int"),
    @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "int"),
    @ApiImplicitParam(name = "areaName", value = "地区名称", required = false, dataType = "String"),
    @ApiImplicitParam(name = "areaCode", value = "父节点编码", required = true, dataType = "String")})
    
	@ApiOperation(value="列表数据")
	public Result list(Integer pageNum, Integer pageSize, String areaName,String areaCode) {
		PageDTO<SysArea> pageDTO = null;
		List<SysArea> ListDic = sysAreaDao.findareaList(areaCode,areaName);
		pageDTO = new PageTool<SysArea>().getPage(ListDic, pageSize, pageNum);
		int total=ListDic.size();
		return Result.resultInfo(ResultCode.SUCCESS, total,pageDTO.getData());
	}
	
	@RequestMapping(value ="/save",method={RequestMethod.POST})
	@ApiImplicitParams({
    @ApiImplicitParam(name="areaName",value="地区名称",required=true,dataType="String"),
    @ApiImplicitParam(name="areaCode",value="地区编码",required=true,dataType="String"), 
    @ApiImplicitParam(name="parentCode",value="上级地区编码",required=true,dataType="String"),	
	})
	@ResponseBody
	@ApiOperation(value="新增")
	public Result save(SysArea  sysArea) {
		if(StrUtils.isEmpty(sysArea.getParentCode())){
			sysArea.setAreaCode("0");
		}
		//根据父节点编码获取父节点
		Boolean flag = true;
		Wrapper wrapper = Condition.create().eq("area_code", sysArea.getParentCode());
		SysArea  parSysArea = sysAreaService.selectOne(wrapper);
		if(parSysArea!= null){
			int type= 0;
			if(!StrUtils.isEmpty(parSysArea.getAreaType())){
				type = Integer.valueOf(parSysArea.getAreaType())+1;
			}
			sysArea.setAreaType(type+"");
		}else{
			sysArea.setAreaType("1");
		}
		
		boolean result = sysAreaService.insert(sysArea);
		String operator = getUserInfo().getName();
		String info = "新增了" + sysArea.getAreaName() + "地区";
		saveOperatorInfo(info, StatusEnum.LogInfoType8.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "添加成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "添加失败");
		}
	}
	@ResponseBody
	@RequestMapping(value ="/updata",method={RequestMethod.POST})
	@ApiImplicitParams({
    @ApiImplicitParam(name="id",value="数据id",required=true,dataType="int"),
    @ApiImplicitParam(name="areaName",value="地区名称",required=true,dataType="String"),
    @ApiImplicitParam(name="areaCode",value="地区编码",required=true,dataType="String"), 
    @ApiImplicitParam(name="parentCode",value="上级地区编码",required=true,dataType="String")
	})
	@ApiOperation(value="修改")
	public Result updata(SysArea sysArea) {
		
		SysArea oldSysArea = sysAreaService.selectById(sysArea.getId());
		if(oldSysArea!= null){
			sysArea.setAreaType(oldSysArea.getAreaType());
		}else{
			sysArea.setAreaType("1");
		}
		boolean result = sysAreaService.updateAllColumnById(sysArea);
		
		String operator = getUserInfo().getName();
		String info = "修改了" + sysArea.getAreaName() + "地区";
		saveOperatorInfo(info, StatusEnum.LogInfoType8.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		}
	}
	
	@ResponseBody
	@RequestMapping(value ="/del",method={RequestMethod.POST})
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="数据id",required=true,dataType="String")	
	})
	@ApiOperation(value="删除")
	public Result del(String  id) {
		String [] ids = id.split(",");
		boolean result=false;
		for (String string : ids) {
			SysArea sysArea = sysAreaService.selectById(string);
			if(sysArea!=null){
				Wrapper wrapper = Condition.create().eq("id", string);
				result = sysAreaService.delete(wrapper);
//				sysDict.setDeleted(true);
//				result = sDicinfoService.updateAllColumnById(sysDict);
				String operator = getUserInfo().getName();
				String info = "删除了" + sysArea.getAreaName()+ "地区";
				saveOperatorInfo(info, StatusEnum.LogInfoType8.getIntValue(), operator);
			}else{
				return Result.resultInfo(ResultCode.FAILURE, "系统参数错误");
			}
			
		}
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value ="/checkAreaCode",method={RequestMethod.POST})
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="id",required=true,dataType="int"),
		@ApiImplicitParam(name="areaCode",value="字典名称",required=true,dataType="String")
	})
	@ApiOperation(value="地区编码是否存在")
	public Result checkAreaCode(String areaCode,String id){
		Boolean flag = getAreaCode(id,areaCode);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "");	
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "地区编码已经存在");	
		}
		
	}
	public Boolean getAreaCode(String id ,String areaCode){
		Boolean flag = true;
		Wrapper wrapper = Condition.create().eq("area_code", areaCode);
		if(StringUtils.isNotEmpty(id)){
			wrapper.ne("id", id);
		}
		int count = sysAreaService.selectCount(wrapper);
		if(count>0){
			flag = false;
		}
		return flag;
	}
	
}
