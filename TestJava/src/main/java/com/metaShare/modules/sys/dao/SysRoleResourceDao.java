package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRoleResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
public interface SysRoleResourceDao extends BaseMapper<SysRoleResource> {

    void deleteOnRoleIdAndResArr(@Param("roleId") String roleId,@Param("allBtnId") String[] allBtnId);

    void deleteByRoleId(String id);
}
