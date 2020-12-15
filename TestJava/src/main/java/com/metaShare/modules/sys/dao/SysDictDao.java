package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysDicinfo;
public interface SysDictDao extends BaseMapper<SysDicinfo> {
	List<SysDicinfo> findDicinfoList(@Param("parentId") int parentId,@Param("dicName") String dicName,@Param("inUse") String inUse);
	List<SysDicinfo> getDicinfoByDicinfoName(@Param("parentId") int parentId, @Param("dicName") String dicName);
	List<SysDicinfo> getDicinfoByDicinfoCode(@Param("parentId") int parentId, @Param("dicCode") String dicCode);
	List<SysDicinfo> getDictList( @Param("dicCode") String dicCode);
}
