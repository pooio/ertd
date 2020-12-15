package com.metaShare.modules.bpm.identity;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.entity.SysRoleType;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysRoleService;
import com.metaShare.modules.sys.service.SysRoleTypeService;
import com.metaShare.modules.sys.service.SysUserService;

public class CustomGroupEntityManager extends GroupEntityManager {
	@Autowired
	private SysUserService userService;
	@Autowired
	private SysRoleService roleService;
	@Autowired
	private SysRoleTypeService roleTypeService;
	

	@Override
	public List<Group> findGroupsByUser(String loginName) {
		SysUser user = null;
		try {
			user = userService.findByLoginName(loginName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//获得角色信息
		List<SysRole> roles = roleService.findByUser(user.getId());
		return createGroups(roles);
	}
	
	private List<Group> createGroups(List<SysRole> roles){
		List<Group> groups = new ArrayList<Group>();
		for(SysRole role : roles){
			if(!role.getDeleted()){
				Group group = new GroupEntity();
				group.setId(role.getRoleCode());
				group.setName(role.getRoleName());
				group.setType(getTypeName(role));
				groups.add(group);
			}
		}
		return groups;
	}

	private String getTypeName(SysRole role) {
		SysRoleType sysRoleType = roleTypeService
				.selectById("");
//		SysRoleType sysRoleType = roleTypeService
//				.selectById(role.getRoleTypeId());
		return sysRoleType.getTypeName();
	}
}
