package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysConfig;
public interface SysConfigDao extends BaseMapper<SysConfig> {
    List<SysConfig> getList(@Param("configType") String configType,@Param("allData") String allData);
    SysConfig getConfigData(@Param("configType") String configType);
    SysConfig getData(@Param("configId") int configId);
    
}
