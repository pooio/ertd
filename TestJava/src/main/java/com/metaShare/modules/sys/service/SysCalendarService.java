package com.metaShare.modules.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysCalendarDao;
import com.metaShare.modules.sys.entity.SysCalendar;
@Service
public class SysCalendarService extends ServiceImpl<SysCalendarDao,SysCalendar> {

	@Autowired
	private SysCalendarDao sysCalendarDao;
	public List<Map> getList(String userId, String year){
		return sysCalendarDao.getList(userId,year);
	}
}
