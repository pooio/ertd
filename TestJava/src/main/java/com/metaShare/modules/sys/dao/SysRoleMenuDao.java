package com.metaShare.modules.sys.dao;

import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

public interface SysRoleMenuDao extends BaseMapper<SysRoleMenu> {

	void save(Map map);

	void remove(Integer roleId);
}
