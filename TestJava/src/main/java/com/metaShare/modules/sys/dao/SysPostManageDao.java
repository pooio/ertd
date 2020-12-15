package com.metaShare.modules.sys.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysPostManage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPostManageDao extends BaseMapper<SysPostManage> {

	List<SysPostManage> getPostCode(@Param("postCode") String postCode);
	List<SysPostManage> getPostName(@Param("post") String post);
	List<SysPostManage> findPostManageList(@Param("postName") String postName);
	List<SysPostManage> findPostManageAllList();
}
