package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.metaShare.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.service.SysOrganizationService;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("api/sys/org")
@Api(tags = "组织机构管理")
public class SysOrganizationController extends BaseController {
	@Autowired
	private SysOrganizationService organizationService;
	@Autowired
	private SysUserService userService;
	
	@ResponseBody
	@RequestMapping(value = "treeData",method = {RequestMethod.POST})
	@ApiOperation(value = "获取组织机构树形结构")
	public Result treeData() {
		//获取了1级节点，根据父子关系获得关联
		SysOrganization sysOrganization = organizationService.selectTopResource();
		List<SysOrganization> secondResources = organizationService.selectChildrenResources(sysOrganization.getId(),"");
		List<SysOrganization> childrenList = organizationService.getChildrenList(secondResources,"");
		sysOrganization.setChildren(childrenList);
		List<SysOrganization> normalList = new ArrayList<>();
		normalList.add(sysOrganization);
		return Result.resultInfo(ResultCode.SUCCESS, normalList);
	}

	@ResponseBody
	@RequestMapping(value = "treeDataList",method = {RequestMethod.POST})
	@ApiOperation(value = "获取组织机构树形结构")
	public Result treeDataList() {
		//获取了1级节点，根据父子关系获得关联
		String dataType="all";
		SysOrganization sysOrganization = organizationService.selectTopResource();
		List<SysOrganization> secondResources = organizationService.selectChildrenResources(sysOrganization.getId(),dataType);
		List<SysOrganization> childrenList = organizationService.getChildrenList(secondResources,dataType);
		sysOrganization.setChildren(childrenList);
		List<SysOrganization> normalList = new ArrayList<>();
		normalList.add(sysOrganization);
		return Result.resultInfo(ResultCode.SUCCESS, normalList);
	}
	
	/**
	 * 修改备注信息
	 * 
	 * @param id
	 * @param remark
	 */
	@RequestMapping(value = "updateReamrk",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "组织机构ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "remark",value = "备注",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "修改部门备注信息")
	public Result updateRemark(String id, String remark) {
		int flag = organizationService.updateRemark(id, remark);

		if(flag == 1){
			return Result.resultInfo(ResultCode.SUCCESS,"修改备注信息成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE,"修改备注信息失败");
		}
	}

	/**
	 * 同一部门下组织机构名称是否存在相同
	 * 
	 * @param parentId
	 * @param orgName
	 * @param orgId
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkOrgName",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "parentId",value = "部门ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "orgName",value = "组织机构名称",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "orgId",value = "组织机构ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "id",value = "ID",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "判断同一部门下组织机构名称是否存在")
	public Result checkOrgName(String parentId,String orgName, String orgId, String id) throws Exception {
		boolean flag = organizationService.checkOrgName(parentId, orgName, orgId, id);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS,"不存在相同组织机构名称");
		}else{
			return Result.resultInfo(ResultCode.FAILURE,"存在相同机构名称");
		}
	}

	/**
	 * 判断同一部门下组织机构编码是否存在
	 * 
	 * @param parentId
	 * @param code
	 * @param orgId
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkOrgCode",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "parentId",value = "部门ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "code",value = "组织机构编码",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "orgId",value = "组织机构ID",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "id",value = "ID",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "判断同一部门下组织机构编码是否存在")
	public Result checkOrgCode(String parentId,String code, String orgId, String id) throws Exception {
		boolean flag = organizationService.checkOrgCode(parentId, code, orgId, id);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS,"不存在相同组织机构名称");
		}else{
			return Result.resultInfo(ResultCode.FAILURE,"机构编码重复");
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value = "del",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "组织机构ID", required = true, dataType = "String", paramType = "query")
	})
	@ApiOperation(value = "删除组织机构")
	public Result delete(String id) {

		Wrapper wrapper = Condition.create().eq("deleted", false);
		wrapper.eq("org_id", id);
		int count = userService.selectCount(wrapper);
		if (count > 0) {
			return Result.resultInfo(ResultCode.VAIL_ERROR, "部门下有人员,不能删除");
		}
		SysOrganization peerOrg = organizationService.selectByParentId(id);
		if(peerOrg != null){
			return Result.resultInfo(ResultCode.FAILURE, "部门下有子部门,不能删除");
		}
		SysOrganization dict = organizationService.selectById(id);
		dict.setDeleted(true);
		boolean flag = organizationService.updateAllColumnById(dict);
		if(flag){
			String operator = getUserInfo().getName();
			String info = "删除了" + dict.getOrgName() + "部门";
		    saveOperatorInfo(info, StatusEnum.LogInfoType4.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		}else {
			return Result.resultInfo(ResultCode.SUCCESS, "删除失败");
		}

	}


	@RequestMapping(value = "/save",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orgName", value = "组织名称", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "排序", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "父节点ID", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "code", value = "部门编码", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "orgShort", value = "部门简写(生成计划编号使用)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "orgOrder", value = "部门顺序(生成计划编号使用)", required = false, dataType = "String", paramType = "query")
	})
	@ApiOperation(value = "新增组织机构")
	public Result save(SysOrganization sysOrganization) {
		//获取同级组织机构最小orgCode
		//获取父节点组织机构编码
		SysOrganization parentOrg = organizationService.selectById(sysOrganization.getParentId());
		SysOrganization peerOrg = organizationService.selectByParentId(sysOrganization.getParentId());
		if(peerOrg == null){
			sysOrganization.setOrgCode(parentOrg.getOrgCode() + ".001");
		}else{
			int lastIndex = peerOrg.getOrgCode().lastIndexOf(".") + 1;
			String orgCode = String.valueOf(Integer.parseInt(peerOrg.getOrgCode().substring(lastIndex)) + 1);
			sysOrganization.setOrgCode(parentOrg.getOrgCode() + "." + (orgCode.length() == 1 ? "00" : orgCode.length() == 2 ? "0" : "") + orgCode);
		}
		sysOrganization.setCreateTime(new Date());
		boolean flag = organizationService.insert(sysOrganization);
		
		String operator = getUserInfo().getName();
		String info = "新增了" + sysOrganization.getOrgName() + "部门";
		saveOperatorInfo(info, StatusEnum.LogInfoType4.getIntValue(), operator);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		}else{
			return Result.resultInfo(ResultCode.SUCCESS, "新增失败");
		}

	}

	// 修改对象
	@RequestMapping(value = "/update",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orgName", value = "组织机构ID", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "sort", value = "排序", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "remark", value = "备注", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "父节点ID", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "code", value = "部门编码", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "orgShort", value = "部门简写(生成计划编号使用)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "orgOrder", value = "部门顺序(生成计划编号使用)", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "组织机构ID", required = true, dataType = "String", paramType = "query")
	})
	@ApiOperation(value = "编辑组织机构")
	public Result update(SysOrganization sysOrganization) {
		//如果修改了排序字段，则更新对应的时间，实现正确的列表显示
		SysOrganization existOrg = organizationService.selectById(sysOrganization.getId());
		if(!existOrg.getSort().equals(sysOrganization.getSort())){
			sysOrganization.setCreateTime(new Date());
		}
		boolean flag = organizationService.updateById(sysOrganization);
		String operator = getUserInfo().getName();
		String info = "修改了" + sysOrganization.getOrgName() + "部门";
		saveOperatorInfo(info, StatusEnum.LogInfoType4.getIntValue(), operator);
		if(flag){
			return Result.resultInfo(ResultCode.SUCCESS, "编辑成功");
		}else{
			return Result.resultInfo(ResultCode.SUCCESS, "编辑失败");
		}

	}


	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "data",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "组织机构ID", required = true, dataType = "String", paramType = "query")
	})
	@ApiOperation(value = "获取组织机构信息")
	public Result viewData(String id) {
		return Result.resultInfo(ResultCode.SUCCESS, organizationService.selectById(id));
	}


    /**
     * 返回单个实体的json数据
     */
    @ResponseBody
    @RequestMapping(value = "getRootOrg",method = {RequestMethod.POST})
    @ApiOperation(value = "返回根节点信息")
    public Result getRootOrg() {
        String id="1";
        return Result.resultInfo(ResultCode.SUCCESS, organizationService.selectById(id));
    }



    // 分页查询
	@ResponseBody
	@RequestMapping(value = "list",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页展示数量", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "机构父id", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "orgName", value = "机构名称或编码", required = true, dataType = "int", paramType = "query")
	})
	@ApiOperation(value = "获取组织机构列表")
	public Result list(int pageSize, int pageNum,String parentId,String orgName) {
		PageDTO<SysOrganization> pageDTO = null;
		int total=0;
		try {
			Wrapper wrapper = Condition.create().eq("deleted", false);
			if(!StrUtils.isEmpty(orgName)){
				wrapper.like("ORG_NAME", orgName);
			}
			wrapper.eq("PARENT_ID", parentId).orderBy("sort",true).orderBy("create_time",true);
			List<SysOrganization> list = organizationService.selectList(wrapper);
			total=list.size();
			pageDTO = new PageTool().getPage(list, pageSize, pageNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//移动端数据

		return Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
	}




	//  移动端组织机构
	@ResponseBody
	@RequestMapping(value = "applist",method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页展示数量", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "机构父id", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "orgName", value = "机构名称或编码", required = true, dataType = "int", paramType = "query")
	})
	@ApiOperation(value = "获取组织机构列表")
	public Result applist(String parentId,String orgName) {
		Wrapper wrapper = Condition.create().eq("deleted", false);
		if(!StrUtils.isEmpty(orgName)){
			wrapper.like("ORG_NAME", orgName);
		}
		wrapper.eq("PARENT_ID", parentId).orderBy("sort",true).orderBy("create_time",true);
		List<SysOrganization> list = organizationService.selectList(wrapper);

		List<SysOrganization> list0 = new ArrayList<>();
		List<SysOrganization> list2 = new ArrayList<>();


		for (SysOrganization sysOrganization : list) {
			//获取组织机构下的用户
		    int userSize = userService.selectUserCount(sysOrganization.getOrgCode());
			sysOrganization.setUserSize(userSize);
			Wrapper orgWra = Condition.create().eq("deleted", false);
			orgWra.eq("PARENT_ID",sysOrganization.getId());
			int orgSize = organizationService.selectCount(orgWra);
            if(orgSize==0){
				sysOrganization.setOrgType("0");
				//list0.add(sysOrganization);
			}else{
				sysOrganization.setOrgType("1");
				//list2.add(sysOrganization);
			}
		}
		//list0.addAll(list2);
		return Result.resultInfo(ResultCode.SUCCESS,list);
	}

	//  移动端组织机构
	@ResponseBody
	@RequestMapping(value = "orgTitle",method = {RequestMethod.POST})

	@ApiOperation(value = "获取组织机构列表")
	public Result orgTitle(String id) {
		SysOrganization sysOrganization = organizationService.selectById(id);
		List<SysOrganization>  list =  new ArrayList();
		if(sysOrganization!= null){
         String orgCode = sysOrganization.getOrgCode();
         if(!StringUtils.isEmpty(orgCode)){
			 String [] orgCodes = orgCode.split("\\.");
			 String orgcode="";
			 for (String code : orgCodes) {
				 if(!StringUtils.isEmpty(orgcode)){
					 orgcode+=".";
				 }
				 orgcode+=code;
				 Wrapper wrapper = Condition.create().eq("deleted", false);
				 wrapper.eq("ORG_CODE",orgcode);
				 SysOrganization org = organizationService.selectOne(wrapper);
				 if(org!=null){
					 list.add(org);
				 }
			 }
		 }

		}


		return Result.resultInfo(ResultCode.SUCCESS,list);
	}


	@ResponseBody
	@RequestMapping(value ="/setInUse",method={RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="数据id",required=true,dataType="int"),
			@ApiImplicitParam(name="inUse",value="是否可用 1可用，0不可用",required=true,dataType="int")
	})
	@ApiOperation(value="禁用/启用")
	public Result setInUse(String id, int inUse  ) {
		Wrapper wrapper = Condition.create().eq("deleted", false);
		if(inUse==0){
			wrapper.eq("org_id", id);
			int count = userService.selectCount(wrapper);
			if (count > 0) {
				return Result.resultInfo(ResultCode.VAIL_ERROR, "部门下有人员,不能禁用");
			}
		}
		SysOrganization org = organizationService.selectById(id);
		if(org!= null){
			org.setInUse(inUse);
			 String resultStr="禁用";
			 if(inUse==1){
				 resultStr = "启用";
			 }
			boolean result = organizationService.updateById(org);
			if (result) {
				return Result.resultInfo(ResultCode.SUCCESS, resultStr+"成功");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, resultStr+ "失败");
			}
		}else {
			return  Result.resultInfo(ResultCode.FAILURE,"系统错误");
		}
	}

	/**
	 * 查询部门加用户的树形数据
	 *
	 * @param
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value = "treeUserData")
	public Result treeUserData() {
		return Result.resultInfo(ResultCode.SUCCESS, organizationService.treeUserData());
	}*/

}
