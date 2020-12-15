package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRoleType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface SysRoleTypeDao extends BaseMapper<SysRoleType>{

    SysRoleType selectTopResource();

    List<SysRoleType> selectChildrenResources(@Param("id") String id);
}
