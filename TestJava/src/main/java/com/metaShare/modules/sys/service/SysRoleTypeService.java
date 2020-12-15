package com.metaShare.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRoleTypeDao;
import com.metaShare.modules.sys.entity.SysRoleType;

@Service
public class SysRoleTypeService extends ServiceImpl<SysRoleTypeDao,SysRoleType>{
	private static Logger logger = LoggerFactory.getLogger(SysRoleTypeService.class);

	@Autowired
	private SysRoleTypeDao sysRoleTypeDao;

	public  SysRoleType selectTopResource() {
		return sysRoleTypeDao.selectTopResource();
	}

	public  List<SysRoleType> selectChildrenResources(String id) {
		return sysRoleTypeDao.selectChildrenResources(id);
	}

	public  List<SysRoleType> getChildrenList(List<SysRoleType> childrenList) {
		List<SysRoleType> newList = new ArrayList<>();
		for(int i = 0; i < childrenList.size(); i++){
			SysRoleType childrenRes = childrenList.get(i);
			List<SysRoleType> nextList = selectChildrenResources(childrenRes.getId());
			if(nextList.size() > 0){
				//说明还有子集
				List<SysRoleType> list = getChildrenList(nextList);
				childrenRes.setChildren(list);
				newList.add(childrenRes);
			}else{
				//说明无子集
				newList.add(childrenRes);
			}

		}
		return newList;
	}

}
