package com.metaShare.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

public interface SysMenuDao extends BaseMapper<SysMenu> {

	List<SysMenu> listUserMenus(Integer userId);

	List<String> listUserPerms(Integer userId);
}
