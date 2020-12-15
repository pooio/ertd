package com.metaShare.modules.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysCalendar;
/**
 * 
 * @ClassName: SysCalendarDao
 * @Description: TODO(描述)
 * @author author
 * @date 2020-02-11 11:54:43
 */
public interface SysCalendarDao extends BaseMapper<SysCalendar> {
/**
 * 
 * @Title: getList
 * @Description: TODO(描述)
 * @return
 * @author eric.xi
 * @date 2020-02-11 11:56:01
 */
	List<Map> getList(@Param("userId") String userId,  @Param("date")  String date);
}
