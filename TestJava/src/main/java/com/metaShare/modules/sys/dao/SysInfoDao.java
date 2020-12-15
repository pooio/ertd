package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysInfo;


public interface SysInfoDao extends BaseMapper<SysInfo> {


    List<SysInfo> getSysInfoList(@Param("userName") String userName,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("module") String module);

}
