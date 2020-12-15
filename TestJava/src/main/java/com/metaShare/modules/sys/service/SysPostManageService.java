package com.metaShare.modules.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.metaShare.modules.sys.dao.SysPostManageDao;
import com.metaShare.modules.sys.entity.SysPostManage;

@Service
public class SysPostManageService extends ServiceImpl<SysPostManageDao, SysPostManage> {
	@Autowired
	SysPostManageDao postManageDao;
	public List<SysPostManage> findPostManageList(String postName){
		return  postManageDao.findPostManageList(postName);
	}
	public List<SysPostManage> findPostManageAllList(){
		return  postManageDao.findPostManageAllList();
	}
	  
	public List<SysPostManage> getPostCode(String postCode){
		return  postManageDao.getPostCode(postCode);
	}
	public List<SysPostManage> getPostName(String post){
		return  postManageDao.getPostName(post);
	}

//	/**
//	 * 根据用户id获取用户职务和头衔信息
//	 *
//	 * @param userId
//	 * @return
//	 */
//	public List<SysPostManage> findByUser(String userId) {
//		List<SysPostManage> list = Lists.newArrayList();
//		Wrapper wrapper = Condition.create().eq("deleted", false).eq("user_id", userId);
//		list = postManageDao.selectList(wrapper);
//		return list;
//	}
//
//	/**
//	 * 根据组织机构id和职务编码查找职务信息
//	 *
//	 * @param orgId
//	 * @param post
//	 * @return
//	 * @return List<PostManage>
//	 */
//	public List<SysPostManage> findByOrgAndPost(String orgId, String post) {
//		List<SysPostManage> list = Lists.newArrayList();
//		Wrapper wrapper = Condition.create().eq("deleted", false).eq("post", post);
//		List<SysPostManage> allPost = postManageDao.selectList(wrapper);
//		;
//		SysOrganization org = organizationDao.selectById(orgId);
//
//		// 递归查找
//		while (list.size() == 0 && org != null) {
//			list = getPost(allPost, post, org.getId());
//			org = organizationDao.selectById(org.getParentId());
//		}
//		return list;
//	}
//
//	/**
//	 * 根据组织机构id和职务编码查找职务信息
//	 *
//	 * @param orgId
//	 * @param posts
//	 * @return
//	 */
//	public Map<String, List<SysPostManage>> findByOrgAndPost(String orgId, String[] posts) {
//		Map<String, List<SysPostManage>> map = new HashMap<String, List<SysPostManage>>();
//
//		Wrapper wrapper = Condition.create().eq("deleted", false).in("post", posts);
//
//		List<SysPostManage> allPost = postManageDao.selectList(wrapper);
//		SysOrganization org = organizationDao.selectById(orgId);
//
//		for (String post : posts) {
//			List<SysPostManage> list = Lists.newArrayList();
//			// 递归查找
//			while (list.size() == 0 && org != null) {
//				list = getPost(allPost, post, org.getId());
//				org = organizationDao.selectById(org.getParentId());
//			}
//			map.put(post, list);
//			org = organizationDao.selectById(orgId);
//		}
//		return map;
//	}
//
//	private List<SysPostManage> getPost(List<SysPostManage> list, String post, String orgId) {
//		List<SysPostManage> retList = Lists.newArrayList();
//		for (SysPostManage postMnage : list) {
//			if (postMnage.getOrgId() != null && orgId.equals(postMnage.getOrgId())
//					&& post.equals(postMnage.getPost())) {
//				retList.add(postMnage);
//			}
//		}
//		return retList;
//	}
//
//	/**
//	 * 根据组织机构id和职务编码随机查询一个职务信息。 如果查询的组织机构没有对应的职务信息，则会从组织机构为空的职务中随机选择一个
//	 *
//	 * @param orgId
//	 * @param post
//	 * @return
//	 */
//	public SysPostManage findOnePostForOrg(String orgId, String post) {
//		SysPostManage postManage = null;
//		List<SysPostManage> list = findByOrgAndPost(orgId, post);
//		if (list.size() == 0) {
//			Wrapper wrapper = Condition.create().eq("deleted", false).eq("post", post).isNull("orgId");
//			list = postManageDao.selectList(wrapper);
//		}
//		if (list.size() > 0) {
//			// 产生0-list.size-1范围的随机数
//			Long index = Math.round(Math.random() * (list.size() - 1));
//			// 根据随机数下标返回一个职务信息
//			postManage = list.get(new Long(index).intValue());
//		}
//		return postManage;
//	}
//
//	/**
//	 * 根据职务编码查找用户
//	 *
//	 * @param post
//	 * @return
//	 * @return List<PostManage>
//	 */
//	public List<SysPostManage> findByOrgAndPost(String post) {
//		List<SysPostManage> allPost = Lists.newArrayList();
//		Wrapper wrapper = Condition.create().eq("deleted", false).eq("post", post);
//		allPost = postManageDao.selectList(wrapper);
//		return allPost;
//	}
//
//	/**
//	 * 根据用户和职务查找所属部门
//	 *
//	 * @param post
//	 *            职务头衔
//	 * @param userId
//	 *            用户ID
//	 * @return List<PostManage>
//	 */
//	public List<SysPostManage> findByUserIdAndPost(String post, String userId) {
//		List<SysPostManage> allPost = Lists.newArrayList();
//		Wrapper wrapper = Condition.create().eq("deleted", false).eq("post", post).eq("userId", userId);
//		allPost = postManageDao.selectList(wrapper);
//		return allPost;
//	}
//
//	/**
//	 * 查看职务信息
//	 *
//	 * @param name
//	 * @param post
//	 * @return
//	 */
//	public List<PostManageAndUserVo> findPostManage(String name, String post) {
//		return postManageDao.findPostManage(name, post);
//	}
//
//	/**
//	 * 查看职务信息
//	 *
//	 * @param userId
//	 *            用户ID
//	 * @return
//	 */
//	public List<PostManageAndUserVo> findPostManageByUserId(String userId) {
//		return postManageDao.findPostManageByUserId(userId);
//	}
}
