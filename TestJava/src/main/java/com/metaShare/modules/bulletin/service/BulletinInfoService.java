/**  
 * @Title: BulletinInfoService.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:59:54 
 */
package com.metaShare.modules.bulletin.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bulletin.dao.BulletinInfoDao;
import com.metaShare.modules.bulletin.entity.BulletinInfo;
import com.metaShare.modules.bulletin.entity.BulletinReadItem;
import com.metaShare.modules.bulletin.entity.BulletinSend;
import com.metaShare.modules.sys.entity.SysUser;
import com.sun.javafx.collections.MappingChange.Map;

/**
 * @ClassName: BulletinInfoService
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:59:54
 */
@Service
@Transactional
public class BulletinInfoService extends ServiceImpl<BulletinInfoDao, BulletinInfo> {
	@Autowired
	private BulletinInfoDao bulletinInfoDao;

	public List<BulletinInfo> getList(String bulletinTitle) {
		return bulletinInfoDao.getList(bulletinTitle);
	}
	public BulletinInfo getBulletinInfo(String bulletinPk) {
		return bulletinInfoDao.getBulletinInfo(bulletinPk);
	}

	public List<BulletinInfo> getUserBulletinInfoList(String userPk, String bulletinTitle, String closeDate) {
		return bulletinInfoDao.getUserBulletinInfoList(userPk, bulletinTitle, closeDate);
	}

	public List<Map> getBulletinReadItemList(String bulletinPk) {
		return bulletinInfoDao.getBulletinReadItemList(bulletinPk);
	}

	public List<BulletinSend> getBuletinSendByBulletinId(String bulletinPk) {
		return bulletinInfoDao.getBuletinSendByBulletinId(bulletinPk);
	}

	public List<Map> getBulletinSendList(String bulletinPk, int isRead, int isAllUser) {
		if (isAllUser == 1) {
			// 判读是否有已读信息
			List<BulletinReadItem> readItems = bulletinInfoDao.getBulletinReadItemByBulletinPk(bulletinPk);
			if (readItems.size() > 0) {
				List<Map> list = bulletinInfoDao.getAllUserBulletinList(bulletinPk, isRead);
				return list;
			} else {
				List<Map> list = bulletinInfoDao.getAllUserBulletinList2(bulletinPk);
				return list;
			}
		} else {
			return bulletinInfoDao.getBulletinSendList(bulletinPk, isRead);
		}
	}

	public BulletinReadItem getBulletinReadItemByUserId(String userId, int bulletinPk, int sendPk) {
		return bulletinInfoDao.getBulletinReadItemByUserId(userId, bulletinPk, sendPk);
	}

	public BulletinInfo getBulletinData(String id) {
		return bulletinInfoDao.getBulletinData(id);
	}
}
