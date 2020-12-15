/**
 * 
 */
package com.metaShare.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysDictDao;
import com.metaShare.modules.sys.entity.SysDicinfo;

/**
 * @author eric.xi
 *
 */
@Service
public class SDicinfoService extends ServiceImpl<SysDictDao,SysDicinfo>{
	
	@Autowired
	private SysDictDao sysDictDao;
	public List<SysDicinfo> getDictList(String dicCode){
		 List<SysDicinfo> dicList =sysDictDao.getDictList(dicCode);
		 return dicList;
	}
}
