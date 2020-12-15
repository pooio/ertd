package com.metaShare.modules.sys.service;

import com.metaShare.modules.sys.dao.SysDictDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.sys.entity.SysDicinfo;
import com.metaShare.modules.sys.entity.SysDict;

@Service
public class SysDictService extends ServiceImpl<SysDictDao,SysDicinfo> {
	@Autowired
	SysDictDao sysDictDao;
	
	/**
	 * 判断code是否存在
	 * @param dict
	 * @return
	 */
	public boolean isExistDict(SysDict dict) {
		Wrapper wrapper = Condition.create()
				.eq("dict_code", dict.getDictCode())
				.eq("deleted", false);
		if (StringUtils.isNotEmpty(dict.getId())) {
			wrapper.ne("id", dict.getId());
		}
		int count = this.selectCount(wrapper);
		if(count>0){
			return true;
		}
		return false;
	}

	public int findByDictTypeId(String string) {
		// TODO Auto-generated method stub
		return 0;
	}
	
//	/**
//	 * 查询列表
//	 * @param dictTypeId
//	 * @param dictName
//	 * @param dictCode
//	 * @return
//	 */
//	public List<DictAndDictTypeVo> findList(String dictTypeId, String dictName, String dictCode) {
//		return sysDictDao.findList(dictTypeId,dictName,dictCode);
//	}
//	
//	public List<SysDict> findByCode(String dictCode){
//		Wrapper<SysDict> wrapper = Condition.create()
//				.eq("deleted", false)
//				.eq("dict_code", dictCode);
//		List<SysDict> list =  sysDictDao.selectList(wrapper);
//		return list;
//	}
//	
//	public SysDict findOneByCode(String dictCode){
//		Wrapper<SysDict> wrapper = Condition.create()
//				.eq("deleted", false)
//				.eq("dict_code", dictCode);
//		List<SysDict> list =  sysDictDao.selectList(wrapper);
//		if (list.size()>0) {
//			return list.get(0);
//		}
//		return null;
//	}
}
