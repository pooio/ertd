package com.metaShare.modules.sys.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysEmail;
import org.apache.ibatis.annotations.Mapper;

public interface SysEmailDao extends BaseMapper<SysEmail> {
	 List<SysEmail> getEmailList();
}
