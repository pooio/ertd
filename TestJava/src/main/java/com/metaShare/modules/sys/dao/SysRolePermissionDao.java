package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

public interface SysRolePermissionDao extends BaseMapper<SysRolePermission> {

    void deleteByRoleId(String id);
}
