package com.metaShare.modules.sys.controller;

import java.util.*;

import com.metaShare.modules.sys.entity.SysRoleUser;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysRoleUserService;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysResource;
import com.metaShare.modules.sys.service.SysResourceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 菜单管理
 */
@Controller
@RequestMapping("api/sys/menu")
@Api(tags = "菜单管理")
public class SysResourceController extends BaseController {
	@Autowired
	private SysResourceService resourceService;
	@Autowired
	private SysRoleUserService sysRoleUserService;

	/**
	 * 获取菜单树形结构信息
	 * roleId 角色Id，根据角色id来获取对应菜单
	 * type 类型，0为全部，用于菜单管理，1为筛选，用于左侧菜单
	 */
	@ResponseBody
	@RequestMapping(value = "/treeData", method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name",value = "菜单名称",required = true,dataType = "String"),
			@ApiImplicitParam(name = "type",value = "类型",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "获取菜单树形结构")
	public Result treeData(String type,String name) {
		//获取了1级菜单，根据父子关系获得关联
		SysResource sysResource = resourceService.selectTopResource();
		List<SysResource> secondResources = new ArrayList<>();
		List<SysResource> childrenList = new ArrayList<>();
		Result result = null;
        String userLoginName = getUserInfo().getLoginName();
        if(userLoginName.equals("admin")&&"1".equals(type)){
        	//获取子节点就去访问数据库
			/*secondResources = resourceService.selectChildrenResources(sysResource.getId(), null, name,"3");
			childrenList = getChildrenList(secondResources, null, null,type);
			if(childrenList!=null){
				sysResource.setChildren(childrenList);
			}
			result = Result.resultInfo(ResultCode.SUCCESS, childrenList);*/

			//一次性拿出所有菜单，循环判断上下级关系
			List<SysResource> rootList = resourceService.selectChildrenResources(null, null,name,"3");
			List<SysResource> list = new ArrayList<>();
			for (int i = 0; i < rootList.size(); i++) {
				SysResource typeEntity = rootList.get(i);
				if(typeEntity.getUrl().contains("/form-manage")){
					typeEntity.setFormType("/form-manage");
				}else{
					typeEntity.setFormType(typeEntity.getUrl());
				}
				if ("1".equals(typeEntity.getParentId())) {
					list.add(typeEntity);
				}
			}
			for (SysResource entity : list) {
				entity.setChildren(getChildNodes(entity.getId(), rootList));
			}
			return Result.resultInfo(ResultCode.SUCCESS,list);
		}else{
			if ("1".equals(type)) {
				//找出用户对应角色
				String userId = getUserId();
				if(StringUtils.isEmpty(userId)){
					return  Result.resultInfo(ResultCode.SESSION_EXPIRED, "请重新登录");
				}
				List<String> roleIdList = sysRoleUserService.findRoleIdsByUser(getUserId());

				String[] roleIds = new String[roleIdList.size()];
				roleIdList.toArray(roleIds);


				List<SysResource> rootList = resourceService.selectChildrenResources(null, roleIds,name,type);
				List<SysResource> list = new ArrayList<>();
				for (int i = 0; i < rootList.size(); i++) {
					SysResource typeEntity = rootList.get(i);
					if(typeEntity.getUrl().contains("/form-manage")){
						typeEntity.setFormType("/form-manage");
					}else{
						typeEntity.setFormType(typeEntity.getUrl());
					}
					if ("1".equals(typeEntity.getParentId())) {
						list.add(typeEntity);
					}
				}
				for (SysResource entity : list) {
					entity.setChildren(getChildNodes(entity.getId(), rootList));
				}
				return Result.resultInfo(ResultCode.SUCCESS,list);

				/*secondResources = resourceService.selectChildrenResources(sysResource.getId(), roleIds, name,type);
				childrenList = getChildrenList(secondResources, roleIds, name,type);
				result = Result.resultInfo(ResultCode.SUCCESS, childrenList);*/
			} else if ("2".equals(type)) {
				//说明在角色管理对菜单授权，需加一个父节点，方便进行全选动作，只提供给前台使用
				SysResource newResource = new SysResource();
				newResource.setName("总菜单");
				newResource.setId("123");
				String userId = getUserId();
				if(StringUtils.isEmpty(userId)){
					return  Result.resultInfo(ResultCode.SESSION_EXPIRED, "请重新登录");
				}
				List<String> roleIdList = sysRoleUserService.findRoleIdsByUser(getUserId());

				String[] roleIds = new String[roleIdList.size()];
				roleIdList.toArray(roleIds);
				List<SysResource> rootList = new ArrayList<>();
				/*if(userLoginName.equals("admin")){
					secondResources = resourceService.selectChildrenResources(sysResource.getId(), null, name,type);
				}else{
					secondResources = resourceService.selectChildrenResources(sysResource.getId(), roleIds, name,type);
				}*/

				if(userLoginName.equals("admin")){
					rootList = resourceService.selectChildrenResources(null, null, name,type);
				}else{
					rootList = resourceService.selectChildrenResources(null, roleIds, name,type);
				}
				List<SysResource> list = new ArrayList<>();
				for (int i = 0; i < rootList.size(); i++) {
					SysResource typeEntity = rootList.get(i);
					if(typeEntity.getUrl().contains("/form-manage")){
						typeEntity.setFormType("/form-manage");
					}else{
						typeEntity.setFormType(typeEntity.getUrl());
					}
					if ("1".equals(typeEntity.getParentId())) {
						list.add(typeEntity);
					}
				}
				for (SysResource entity : list) {
					entity.setChildren(getChildNodes(entity.getId(), rootList));
				}
				/*childrenList = getChildrenList(secondResources, roleIds, name,type);*/
				newResource.setChildren(list);
				List<SysResource> newListResource = new ArrayList<>();
				newListResource.add(newResource);
				sysResource.setChildren(newListResource);
				/*result = Result.resultInfo(ResultCode.SUCCESS, sysResource);*/
				return Result.resultInfo(ResultCode.SUCCESS,sysResource);

			}else{
				//说明为菜单管理处，不涉及角色id进行筛选

				List<SysResource> rootList = resourceService.selectChildrenResources(null, null,name,type);
				List<SysResource> list = new ArrayList<>();
				for (int i = 0; i < rootList.size(); i++) {
					SysResource typeEntity = rootList.get(i);
					if(typeEntity.getType() == 0){
						if(typeEntity.getUrl().contains("/form-manage")){
							typeEntity.setFormType("/form-manage");
						}else{
							typeEntity.setFormType(typeEntity.getUrl());
						}
					}
					if ("1".equals(typeEntity.getParentId())) {
						list.add(typeEntity);
					}
				}
				for (SysResource entity : list) {
					entity.setChildren(getChildNodes(entity.getId(), rootList));
				}
				sysResource.setChildren(list);
				return Result.resultInfo(ResultCode.SUCCESS,sysResource);

				/*secondResources = resourceService.selectChildrenResources(sysResource.getId(), null, name,type);
				childrenList = getChildrenList(secondResources, null, null,type);
				sysResource.setChildren(childrenList);
				result = Result.resultInfo(ResultCode.SUCCESS, sysResource);*/
			}
		}
	}


	public List<SysResource> getChildrenList(List<SysResource> childrenList,String[] roleIds,String name,String type){
		List<SysResource> newList = new ArrayList<>();
		for(int i = 0; i < childrenList.size(); i++){
			SysResource childrenRes = childrenList.get(i);
			List<SysResource> nextList = resourceService.selectChildrenResources(childrenRes.getId(),roleIds,name,type);
			if(nextList.size() > 0){
				//说明还有子集
				List<SysResource> list = getChildrenList(nextList,roleIds,name,type);
				if(list!= null){
					childrenRes.setChildren(list);
				}
				newList.add(childrenRes);
			}else{
				//说明无子集
				newList.add(childrenRes);
			}
		}
		return newList;
	}


	private List<SysResource> getChildNodes(String id, List<SysResource> rootList) {
		// The list of child nodes
		List<SysResource> childList = new ArrayList<>();
		// Fill the list of child'nodes which parent id equal params of id
		for (SysResource typeEntity : rootList) {
			if (StringUtils.isNotEmpty(typeEntity.getParentId())) {
				if (id.equals(typeEntity.getParentId())) {
					if(typeEntity.getType() == 0){
						if(typeEntity.getUrl().contains("/form-manage")){
							typeEntity.setFormType("/form-manage");
						}else{
							typeEntity.setFormType(typeEntity.getUrl());
						}
					}
					childList.add(typeEntity);
				}
			}
		}
		if (childList.size() == 0) {
			return null;
		}
		// Look up it's child node and fill
		for (SysResource entity : childList) {
			entity.setChildren(getChildNodes(entity.getId(), rootList));
		}
		return childList;
	}


	@ResponseBody
	@RequestMapping(value = "delete", method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "菜单Id",required = true,dataType = "String",paramType = "query"),
	})
	@ApiOperation(value = "删除菜单")
	public Result delete(String id) {
		Result result = null;
		Wrapper wrapper = Condition.create().eq("parent_id", id).eq("deleted", false);
		int count = resourceService.selectCount(wrapper);
		if (count > 0) {
			result = Result.resultInfo(ResultCode.VAIL_ERROR, "此节点下有子节点，不能删除");
		} else {
			SysResource resource = resourceService.selectById(id);
			resource.setDeleted(true);
			resourceService.updateAllColumnById(resource);
			result = Result.resultInfo(ResultCode.SUCCESS, "删除成功");
			String operator = getUserInfo().getName();
			String info = "删除了菜单：" + resource.getName();
			saveOperatorInfo(info, StatusEnum.LogInfoType3.getIntValue(), operator);
		}
		return result;
	}



	
	// 保存对象
	@RequestMapping(value = "/save", method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name",value = "菜单名称",required = true,dataType = "String"),
			@ApiImplicitParam(name = "url",value = "菜单路径",required = true,dataType = "String"),
			@ApiImplicitParam(name = "remark",value = "备注",required = true,dataType = "String"),
			@ApiImplicitParam(name = "sort",value = "排序",required = true,dataType = "int"),
			@ApiImplicitParam(name = "iconClass",value = "图案",required = true,dataType = "String"),
			@ApiImplicitParam(name = "isShow",value = "是否显示",required = true,dataType = "String"),
			@ApiImplicitParam(name = "parentId",value = "父Id",required = true,dataType = "String"),
			@ApiImplicitParam(name = "type",value = "菜单类型 0 菜单  1 按钮 ",required = true,dataType = "String"),
	})
	@ApiOperation(value = "新增菜单")
	public Result save(SysResource sysResource) {
		if (StringUtils.isNotEmpty(sysResource.getUrl())) {
			sysResource.setUrl(sysResource.getUrl().replace("amp;", ""));
		}
		sysResource.setCreateTime(new Date());
		boolean result = resourceService.insert(sysResource);
		String operator = getUserInfo().getName();
		String info = "新增了菜单：" + sysResource.getName();
		saveOperatorInfo(info, StatusEnum.LogInfoType3.getIntValue(), operator);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "新增失败");
		}
	}


	// 修改对象
	@RequestMapping(value = "/update", method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name",value = "菜单名称",required = true,dataType = "String"),
			@ApiImplicitParam(name = "url",value = "菜单路径",required = true,dataType = "String"),
			@ApiImplicitParam(name = "remark",value = "备注",required = false,dataType = "String"),
			@ApiImplicitParam(name = "sort",value = "排序",required = true,dataType = "int"),
			@ApiImplicitParam(name = "iconClass",value = "图案",required = true,dataType = "String"),
			@ApiImplicitParam(name = "isShow",value = "是否显示",required = true,dataType = "String"),
			@ApiImplicitParam(name = "parentId",value = "父Id",required = true,dataType = "String"),
			@ApiImplicitParam(name = "type",value = "菜单类型 0 菜单  1 按钮 ",required = true,dataType = "String"),
			@ApiImplicitParam(name = "id",value = "id",required = true,dataType = "String")
	})
	@ApiOperation(value = "编辑菜单")
	public Result update(SysResource sysResource) {
		//查询菜单是不是一级菜单
		/*SysResource oldsysResource = resourceService.selectById(sysResource.getId());
		if(oldsysResource!= null){
			String parentid = oldsysResource.getParentId();
			if("1".equals(parentid)){
				String newparentid=sysResource.getParentId();
				if(!parentid.equals(newparentid)){
					sysResource.setParentId("1");
				}
			}
		}*/
		if(StringUtils.isNotEmpty(sysResource.getParentId())){
            List<SysResource> children = resourceService.findChildren(sysResource.getId());
            if(children.size() > 0){
                return Result.resultInfo(ResultCode.FAILURE,"有子菜单的一级菜单不能放到其他一级菜单下");
            }
            if(sysResource.getId().equals(sysResource.getParentId())){
                return Result.resultInfo(ResultCode.FAILURE,"父菜单不能选择本身");
            }
        }
		if(StrUtils.isEmpty(sysResource.getParentId())){
			sysResource.setParentId("1");
		}
		if(!StrUtils.isEmpty(sysResource.getUrl())){
			sysResource.setUrl(sysResource.getUrl().replace(";amp", ""));
		}
		boolean result = resourceService.updateAllColumnById(sysResource);
		String operator = getUserInfo().getName();
		String info = "编辑了菜单：" + sysResource.getName();
		saveOperatorInfo(info, StatusEnum.LogInfoType3.getIntValue(), operator);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, "编辑成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "编辑失败");
		}
	}

	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "data", method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "菜单Id",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "获取菜单信息")
	public Result viewData(String id) {
		return Result.resultInfo(ResultCode.SUCCESS, resourceService.selectById(id));
	}

	/**
	 * 验证菜单名是否可用
	 * @param menuName 菜单名称
	 * @param menuId   菜单ID
	 * @param menuParentId   菜单父ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkMenuName", method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "menuName",value = "菜单名称",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "menuId",value = "数据id",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "menuParentId",value = "菜单父Id",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "type",value = "类型",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "验证菜单名是否可用")
	public Result checkMenuName(String menuName,String menuParentId,String menuId,String type){
		Boolean flag = resourceService
				.checkMenuName(menuName,menuId,menuParentId,type);
		if(!flag){
			return Result.resultInfo(ResultCode.FAILURE, flag);
		}
		return Result.resultInfo(ResultCode.SUCCESS, flag);
	}

	/**
	 * 验证菜单名是否可用
	 * @param menuId   菜单ID
	 * @param menuParentId   菜单父ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkUrl", method = {RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name = "url",value = "路由地址",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "menuId",value = "数据id",required = true,dataType = "String",paramType = "query"),
			@ApiImplicitParam(name = "menuParentId",value = "菜单父Id",required = true,dataType = "String",paramType = "query")
	})
	@ApiOperation(value = "路由地址是否可用")
	public Result checkUrl(String url,String menuParentId,String menuId){
		Boolean flag = resourceService
				.checkURL("/"+url,menuId,menuParentId);
		if(!flag){
			return Result.resultInfo(ResultCode.FAILURE, "路由地址重复");
		}
		return Result.resultInfo(ResultCode.SUCCESS, flag);
	}
	
	
	@ResponseBody
	@RequestMapping(value ="/setIsShow",method={RequestMethod.POST})
	@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="数据id",required=true,dataType="String"),
			@ApiImplicitParam(name="isShow",value="是否可用 1显示，0不显示",required=true,dataType="int")
	})
	@ApiOperation(value="显示/不显示")
	public Result setIsShow(String id, int isShow  ) {
		SysResource sysResource = resourceService.selectById(id);
		if(sysResource!= null){
			sysResource.setIsShow(isShow);
			 boolean result = resourceService.updateById(sysResource);
			 String resultStr="不显示";
			 if(isShow==1){
				 resultStr = "显示";
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
	@RequestMapping(value ="/getRoleBtn",method={RequestMethod.POST})
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "String") })
	@ApiOperation(value="获取角色按钮权限字符串")
	public Result getRoleBtn(String id) {
		List<SysResource> sysResourceList = resourceService.getRolesBtn(id);
		String resourceIds = "";
		for (SysResource sysResource : sysResourceList) {
			resourceIds += sysResource.getId() + ",";
		}
		if (sysResourceList.size() > 0) {
			resourceIds = resourceIds.substring(0, resourceIds.length() - 1);
		}
		return Result.resultInfo(ResultCode.SUCCESS, resourceIds);
	}


	@ResponseBody
	@RequestMapping(value ="/getUserRoleBtn",method={RequestMethod.POST})
	@ApiImplicitParams({})
	@ApiOperation(value="获取当前用户角色按钮权限字符串")
	public Result getUserRoleBtn() {
		//获取当前用户的角色
		String userLoginName = getUserInfo().getLoginName();
		List<String> roleIdList = sysRoleUserService.findRoleIdsByUser(getUserId());
		String[] roleIds = new String[roleIdList.size()];
		roleIdList.toArray(roleIds);
		if(userLoginName.equals("admin")){
			roleIds=null;
		}
		List<SysResource> sysResourceList = resourceService.selectRolesBtn(roleIds);
		String resourceIds = "";
		for (SysResource sysResource : sysResourceList) {
			resourceIds += sysResource.getBtnType() + ",";
		}
		if (sysResourceList.size() > 0) {
			resourceIds = resourceIds.substring(0, resourceIds.length() - 1);
		}
		return Result.resultInfo(ResultCode.SUCCESS, resourceIds);
	}


	/**
	 * 获取按钮树形结构信息(只获取到对应角色已授权菜单下的按钮)
	 * roleId 角色Id，根据角色id来获取对应菜单
	 */
	@ResponseBody
	@RequestMapping(value = "/btnTreeData", method = {RequestMethod.POST})
	@ApiImplicitParams({ @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "String")
	})
	@ApiOperation(value = "获取菜单树形结构")
	public Result btnTreeData(@RequestParam(value = "roleId")String roleId) {
		//获取了1级菜单，根据父子关系获得关联
		List<String> sysResourceIdList = resourceService.getResourceListByRoleId(roleId);
		return getBtnTreeResult(sysResourceIdList);
	}

	public List<SysResource> getBtnChildrenList(List<SysResource> childrenList,String[] resourceId){

		List<SysResource> newList = new ArrayList<>();
		for(int i = 0; i < childrenList.size(); i++){
			SysResource childrenRes = childrenList.get(i);
			List<SysResource> nextList = resourceService.selectChildrenResourcesAuthorized(childrenRes.getId(),resourceId);
			if(nextList.size() > 0){
				//说明还有子集
				List<SysResource> list = getBtnChildrenList(nextList,resourceId);
				if(list.size() > 0){
					childrenRes.setChildren(list);
					newList.add(childrenRes);
				}
			}else{
				//说明无子集
				if(childrenRes.getType() == 1){
					newList.add(childrenRes);
				}
			}
		}
		return newList;
	}

	@ResponseBody
	@RequestMapping(value = "/getBtnTree")
	public Result getBtnTree(String menuIds){
		//获取一级菜单列表，根据一级菜单列表分割menuIds,
		String[] menuId = menuIds.split(",");
		List<String> sysResourceIdList = Arrays.asList(menuId);
		return getBtnTreeResult(sysResourceIdList);
	}

	private Result getBtnTreeResult(List<String> sysResourceIdList) {

		//获取了1级菜单，根据父子关系获得关联
		SysResource sysResource = resourceService.selectTopResource();
		Result result = null;
		SysResource newResource = new SysResource();
		//总菜单无特殊用处，只用于前台全选、取消全选操作，不涉及数据库
		newResource.setName("总菜单");
		newResource.setId("123");
		//根据角色ID获取该角色已被授权的菜单，按钮只显示已被授权的菜单
		if(sysResourceIdList.size() > 0){
			String[] resourceId = sysResourceIdList.toArray(new String[sysResourceIdList.size()]);
			List<SysResource> secondResources = resourceService.selectChildrenResourcesAuthorized(sysResource.getId(),resourceId);
			List<SysResource> btnChildren = getBtnChildrenList(secondResources,resourceId);
			newResource.setChildren(btnChildren);
		}
		List<SysResource> newListResource = new ArrayList<>();
		newListResource.add(newResource);
		sysResource.setChildren(newListResource);
		result = Result.resultInfo(ResultCode.SUCCESS, sysResource);
		return result;

	}

	@RequestMapping(value = "test")
	@ResponseBody
	public Result test(){
		List<Map<String,Object>> dataList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		Map<String,Object> methMap = new HashMap<>();
		map.put("path","/system-user");
		map.put("component","() => import ( /* webpackChunkName: \"theme\" */ '@/views/system/user')");
		methMap.put("title","用户管理");
		methMap.put("requireAuth",true);
		map.put("meta",methMap);

		dataList.add(map);

		map.put("path","/system-menu");
		map.put("component","() => import ( /* webpackChunkName: \"theme\" */ '@/views/system/menu')");
		methMap.put("title","菜单管理");
		methMap.put("requireAuth",true);
		map.put("meta",methMap);
		dataList.add(map);
		return Result.resultInfo(ResultCode.SUCCESS,dataList);

	}

	// 判断是否是超级管理员
	/*private Boolean isAdmin(SysUser user) {
		Boolean flag = false;
		// 获得角色信息
		List<SysRole> roles = roleService.findByUser(user.getId());
		for (SysRole role : roles) {
			if (role.getId().equals("1")) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	private List<SysResource> menusFilter(List<SysResource> list, SysUser user) {
		List<SysResource> newList = new ArrayList<SysResource>();
		List<SysResource> userResources = userAuthService.findResourcesByUser(user);

		for (SysResource resource : list) {
			if (userResources.contains(resource)) {
				newList.add(resource);
			}
		}
		return newList;
	}*/

}
