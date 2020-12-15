package com.metaShare.modules.sys.dao;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.vo.UserOrgVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
public interface SysOrganizationDao extends BaseMapper<SysOrganization> {

	/**
	 * 更新备注
	 * 
	 * @param id
	 * @param remark
	 * @return void
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年5月10日 下午3:49:36
	 */
	int updateRemark(@Param("id") String id,@Param("remark") String remark);

	Date getLastUpdateTime();

	/**
	 * 获取未删除的部门
	 */
	List<SysOrganization> listData();

	// 查询部门加用户
	List<UserOrgVo> treeUserData();

	// 逻辑删除
	void updateDeletedById(String id);

    SysOrganization selectTopResource();

	List<SysOrganization> selectChildrenResources(@Param("id") String id,@Param("dataType") String dataType);

    SysOrganization selectByParentId(String parentId);

    List<SysOrganization> getChildrenListOnOrgCode(String orgCode);
	List<SysOrganization> selectApplist(String parentId,String orgName);

}
