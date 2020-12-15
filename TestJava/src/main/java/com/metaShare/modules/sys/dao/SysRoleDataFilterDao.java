package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRoleDataFilter;
import org.apache.ibatis.annotations.Mapper;

public interface SysRoleDataFilterDao extends BaseMapper<SysRoleDataFilter> {

    void deleteByRoleId(String id);
}
