package com.metaShare.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysRoleUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
public interface SysRoleUserDao extends BaseMapper<SysRoleUser> {

	List<String> findByUserId(String userId);

	void deleteByRoleId(String id);

    SysRoleUser getByRoleIdAndUserId(@Param("roleId") String roleId,@Param("userId") String userId);
}
