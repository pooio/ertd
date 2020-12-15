package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.dao.SysDictDao;
import com.metaShare.modules.sys.entity.SysDicinfo;
import com.metaShare.modules.sys.service.SDicinfoService;

import ch.qos.logback.classic.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@CrossOrigin
@Api(tags = "数据字典")
@RequestMapping("/api/sys/dictInfo")
public class SysDicinfoController extends BaseController {
	Logger logger = (Logger) LoggerFactory.getLogger(SysDicinfoController.class);
	@Autowired
	private SysDictDao sysDictDao;
	@Autowired
	private SDicinfoService sDicinfoService;
	@RequestMapping(value = "/treeData", method = {RequestMethod.POST})
    @ApiOperation(value="数据树节点数据")
	@ResponseBody
	public Result treeData() {
		List<SysDicinfo> normalList = new ArrayList<>();
		SysDicinfo data = recursiveTree(0);
		normalList.add(data);
		return Result.resultInfo(ResultCode.SUCCESS,normalList);
	}

	public SysDicinfo recursiveTree(int id) {
		// 根据cid获取节点对象
		SysDicinfo node = sDicinfoService.selectById(id);
		if(node == null ){
			node = new SysDicinfo();
			node.setId(0);
			node.setDicName("字典类型");
		}
		// 查询cid下的所有子节点
		List<SysDicinfo> childTreeNodes = sysDictDao.findDicinfoList(id,"","1");
		// 遍历子节点
		for (SysDicinfo child : childTreeNodes) {
			node.getChildren().add(child);
		}
//
		return node;
	}

	@ResponseBody
	@RequestMapping(value = "/list", method = {RequestMethod.POST})
	@ApiImplicitParams({
    @ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "int"),
    @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "int"),
    @ApiImplicitParam(name = "parentId", value = "父节点id", required = true, dataType = "int")})
    
	@ApiOperation(value="列表数据")
	public Result list(Integer pageNum, Integer pageSize, Integer parentId,String dicName) {
		PageDTO<SysDicinfo> pageDTO = null;
		List<SysDicinfo> ListDic = sysDictDao.findDicinfoList(parentId,dicName,"");
		pageDTO = new PageTool<SysDicinfo>().getPage(ListDic, pageSize, pageNum);
		int total=ListDic.size();
		return Result.resultInfo(ResultCode.SUCCESS, total,pageDTO.getData());
	}

	
	@RequestMapping(value ="/save",method={RequestMethod.POST})
	@ApiImplicitParams({
    @ApiImplicitParam(name="dicName",value="字典名称",required=true,dataType="String"),
    @ApiImplicitParam(name="dicCode",value="字典编码",required=true,dataType="String"), 
    @ApiImplicitParam(name="dicDesc",value="字典描述",required=false,dataType="String"), 
    @ApiImplicitParam(name="parentId",value="父节点ID",required=true,dataType="int"),	
    @ApiImplicitParam(name="sortNo",value="顺序",required=true,dataType="int"),	
	})
	@ResponseBody
	@ApiOperation(value="新增")
	public Result save(SysDicinfo  dicinfo) {
		dicinfo.setCreateTime(new Date());
		boolean result = sDicinfoService.insert(dicinfo);
		String operator = getUserInfo().getName();
		String info = "新增了" + dicinfo.getDicName()+ "数据字典";
		saveOperatorInfo(info, StatusEnum.LogInfoType5.getIntValue(), operator);
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
    @ApiImplicitParam(name="dicName",value="字典名称",required=true,dataType="String"),
    @ApiImplicitParam(name="dicCode",value="字典编码",required=true,dataType="String"), 
    @ApiImplicitParam(name="dicDesc",value="字典描述",required=false,dataType="String"), 
    @ApiImplicitParam(name="parentId",value="父节点ID",required=true,dataType="int"),	
    @ApiImplicitParam(name="sortNo",value="顺序",required=true,dataType="int")
	})
	@ApiOperation(value="修改")
	public Result updata(SysDicinfo dicinfo) {
		SysDicinfo existDic = sDicinfoService.selectById(dicinfo.getId());
		if(existDic.getSortNo() != dicinfo.getSortNo()){
			dicinfo.setCreateTime(new Date());
		}
		boolean result = sDicinfoService.updateAllColumnById(dicinfo);
		
		String operator = getUserInfo().getName();
		String info = "修改了" + dicinfo.getDicName() + "数据字典";
		saveOperatorInfo(info, StatusEnum.LogInfoType5.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		}
	}

	@ResponseBody
	@RequestMapping(value ="/setInUse",method={RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="数据id",required=true,dataType="int"),
			@ApiImplicitParam(name="inUse",value="是否可用 1可用，0不可用",required=true,dataType="int")
	})
	@ApiOperation(value="禁用/启用")
	public Result setInUse(int id, int inUse  ) {
		SysDicinfo sDicinfo = sDicinfoService.selectById(id);
		if(sDicinfo!= null){
			sDicinfo.setInUse(inUse);
			 boolean result = sDicinfoService.updateById(sDicinfo);
			 String resultStr="禁用";
			 if(inUse==1){
				 resultStr = "启用";
			 }
			if (result) {
				return Result.resultInfo(ResultCode.SUCCESS, resultStr+"成功");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, resultStr+ "失败");
			}
		}else {
			return  Result.resultInfo(ResultCode.FAILURE,"系统错误");
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
			SysDicinfo sysDict = sDicinfoService.selectById(string);
			if(sysDict!=null){
				sysDict.setDeleted(true);
				result = sDicinfoService.updateAllColumnById(sysDict);
				
				String operator = getUserInfo().getName();
				String info = "删除了" + sysDict.getDicName() + "数据字典";
				saveOperatorInfo(info, StatusEnum.LogInfoType5.getIntValue(), operator);
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
	@RequestMapping(value ="/checkDicinfoName",method={RequestMethod.POST})
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="id",required=false,dataType="int"),
		@ApiImplicitParam(name="dicName",value="字典名称",required=true,dataType="String"),
		@ApiImplicitParam(name="parentId",value="父节点ID",required=true,dataType="int")	
	})
	@ApiOperation(value="验证字典名称是否存在")
	public Result checkDicinfoName(int parentId ,String dicName,int id){
		SysDicinfo oldDicinfo = getDicinfoByDicinfoName(parentId,dicName);
		boolean result = (oldDicinfo == null || oldDicinfo.getId()==id);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, result);	
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "字典名称已经存在");	
		}
		
	}
	public SysDicinfo getDicinfoByDicinfoName(int parentId,String dicName){
		List<SysDicinfo> list =sysDictDao.getDicinfoByDicinfoName(parentId,dicName);
		SysDicinfo sDicinfo= null;
		if(list.size()>0){
			sDicinfo = list.get(0);
		}
		return sDicinfo;
	}
	@ResponseBody
	@RequestMapping(value="getDictList" ,method={RequestMethod.POST})
	@ApiImplicitParams({
		@ApiImplicitParam(name="dicCode",value="字典名称",required=true,dataType="String") 
	})
	@ApiOperation(value="根据字典编码查询字典数据")
	public Result getDictList(String dicCode){
		List<SysDicinfo> dicList = sDicinfoService.getDictList(dicCode);
		return Result.resultInfo(ResultCode.SUCCESS, dicList);
	}   
	
	
	
	@ResponseBody
	@RequestMapping(value ="/checkDicinfoCode",method={RequestMethod.POST})
	@ApiImplicitParams({
		@ApiImplicitParam(name="id",value="id",required=true,dataType="int"),
		@ApiImplicitParam(name="dicCode",value="字典名称",required=true,dataType="String"),
		@ApiImplicitParam(name="parentId",value="父节点ID",required=true,dataType="int")	
	})
	@ApiOperation(value="验证字典编码是否存在")
	public Result checkDicinfoCode(int parentId ,String dicCode,int id){
		SysDicinfo oldDicinfo = getDicinfoByDicinfoCode(parentId,dicCode);
		boolean result = (oldDicinfo == null || oldDicinfo.getId()==id);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, result);	
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "字典编码已经存在");	
		}
		
	}
	public SysDicinfo getDicinfoByDicinfoCode(int parentId,String dicCode){
		List<SysDicinfo> list =sysDictDao.getDicinfoByDicinfoCode(parentId,dicCode);
		SysDicinfo sDicinfo= null;
		if(list.size()>0){
			sDicinfo = list.get(0);
		}
		return sDicinfo;
	}
	
}