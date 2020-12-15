package com.metaShare.modules.sys.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.metaShare.modules.sys.dao.SysDictDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.sys.dao.SysOrganizationDao;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.vo.UserOrgVo;

@Service
public class SysOrganizationService extends ServiceImpl<SysOrganizationDao, SysOrganization> {
	@Autowired
	private SysOrganizationDao organizationDao;
	@Autowired
	private SysDictDao dictDao;
	@Autowired
	private SysPostManageService postManageService;

	public List<SysOrganization> treeData() {
		Wrapper wrapper = Condition.create().eq("deleted", false);
		List<String> list = new ArrayList<String>();
		list.add("treeLevel");
		list.add("sort");
		list.add("id");
		wrapper.orderBy(true, list, true);
		return organizationDao.selectList(wrapper);
	}

	public int updateRemark(String id, String remark) {
		 return  organizationDao.updateRemark(id, remark);
	}

	/**
	 * 根据组织机构id获取组织机构信息及其所有下级组织机构信息
	 * 
	 * @param ids
	 * @return
	 * @return List<Organization>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年5月13日 下午3:42:41
	 */
	public List<SysOrganization> findOrgAndChildren(String[] ids) {
		List<SysOrganization> all = treeData();
		List<SysOrganization> list = Lists.newArrayList();
		for (String id : ids) {
			for (SysOrganization org : all) {
				if (org.getId().equals(id)) {
					list.add(org);
					break;
				}
			}
		}

		List<SysOrganization> retList = Lists.newArrayList();
		retList.addAll(list);
		for (SysOrganization organization : list) {
			getChildren(all, retList, organization);
		}

		return retList;
	}

	private void getChildren(List<SysOrganization> all, List<SysOrganization> childrenList, SysOrganization parent) {
		for (SysOrganization org : all) {
			if (parent.getId().equals(org.getParentId())) {
				childrenList.add(org);
				getChildren(all, childrenList, org);
			}
		}
	}

	public Date getLastUpdateTime() {
		return organizationDao.getLastUpdateTime();
	}

	public List<SysOrganization> listData() {
		return organizationDao.listData();
	}

	/**
	 * 同一部门下组织机构名称是否存在相同
	 * 
	 * @param parentId
	 * @param orgName
	 * @param orgId
	 * @param id
	 * @return
	 */
	public Boolean checkOrgName(String parentId, String orgName, String orgId, String id) {
		Boolean flag = true;
		if (StringUtils.isEmpty(orgName)) {
			return flag;
		}
		Wrapper wrapper = Condition.create().eq("deleted", false).eq("org_name", orgName).eq("parent_id", parentId);
		if (StringUtils.isNotEmpty(id)) {
			wrapper.ne("id", id);
		}
		int count = this.selectCount(wrapper);
		if (count > 0) {
			flag = false;
		}
		return flag;
	}
	public Boolean checkOrgCode(String parentId, String code, String orgId, String id) {
		Boolean flag = true;
		if (StringUtils.isEmpty(code)) {
			return flag;
		}
		Wrapper wrapper = Condition.create().eq("deleted", false).eq("code", code).eq("parent_id", parentId);
		if (StringUtils.isNotEmpty(id)) {
			wrapper.ne("id", id);
		}
		int count = this.selectCount(wrapper);
		if (count > 0) {
			flag = false;
		}
		return flag;
	}
	

	/**
	 * 查询部门加用户的树形数据
	 * 
	 * @param user
	 * @return
	 */
	public List<UserOrgVo> treeUserData() {
		return organizationDao.treeUserData();
	}

	// 转换实体类
	public static <T> List<T> castEntity(List<Object[]> list, Class<T> clazz) throws Exception {
		List<T> returnList = new ArrayList<T>();
		Object[] co = list.get(0);
		Class[] c2 = new Class[co.length];

		// 确定构造方法
		for (int i = 0; i < co.length; i++) {
			c2[i] = co[i].getClass();
		}

		for (Object[] o : list) {
			Constructor<T> constructor = clazz.getConstructor(c2);
			returnList.add(constructor.newInstance(o));
		}

		return returnList;
	}

	/**
	 * 获取指定code 的部门集合
	 */
	public List<SysOrganization> getlistDataByCode(String code) {
		Wrapper wrapper = Condition.create().eq("code", code);
		return organizationDao.selectList(wrapper);
	}
	/**
	 * 获取指定code 的部门集合
	 */
	public List<SysOrganization> applist(String parentId,String orgName) {
		return organizationDao.selectApplist(parentId,orgName);
	}

	/**
	 * 通过部门id和code 来判断当前部门
	 */
	public boolean isCodeOrg(String id, String code) {
		boolean flag = false;
		List<SysOrganization> list = getlistDataByCode(code);
		for (SysOrganization entity : list) {
			if (entity.getId().equals(id)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 根据ID查询部门信息
	 * 
	 * @param list
	 * @return
	 */
	public List<SysOrganization> findOrgById(List<String> list) {
		Wrapper wrapper = Condition.create().in("id", list);
		return organizationDao.selectList(wrapper);
	}

	public List<SysOrganization> findByLeave(int i) {
		Wrapper wrapper = Condition.create()
				.eq("deleted", false)
				.eq("treeLevel",i);
		return organizationDao.selectList(wrapper);
	}

	public void deleted(String id) {
		organizationDao.updateDeletedById(id);
	}

	//获取1级组织机构
	public SysOrganization selectTopResource() {
		return organizationDao.selectTopResource();
	}

	public List<SysOrganization> selectChildrenResources(String id,String dataType) {
		return organizationDao.selectChildrenResources(id,dataType);
	}


	public List<SysOrganization> getChildrenList(List<SysOrganization> childrenList,String dataType){
		List<SysOrganization> newList = new ArrayList<>();
		for(int i = 0; i < childrenList.size(); i++){
			SysOrganization childrenRes = childrenList.get(i);
			List<SysOrganization> nextList = selectChildrenResources(childrenRes.getId(),dataType);
			if(nextList.size() > 0){
				//说明还有子集
				List<SysOrganization> list = getChildrenList(nextList,dataType);
				childrenRes.setChildren(list);
				newList.add(childrenRes);
			}else{
				//说明无子集
				newList.add(childrenRes);
			}

		}
		return newList;
	}


	public SysOrganization selectByParentId(String parentId) {
		return organizationDao.selectByParentId(parentId);
	}

	public List<SysOrganization> getChildrenListOnOrgCode(String orgCode) {
		return organizationDao.getChildrenListOnOrgCode(orgCode);
	}
}
