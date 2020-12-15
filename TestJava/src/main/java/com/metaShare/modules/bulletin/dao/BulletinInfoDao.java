/**  
 * @Title: BulletinInfoDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:50:50 
 */
package com.metaShare.modules.bulletin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.hibernate.annotations.Parent;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.bulletin.entity.BulletinInfo;
import com.metaShare.modules.bulletin.entity.BulletinReadItem;
import com.metaShare.modules.bulletin.entity.BulletinSend;
import com.metaShare.modules.sys.entity.SysUser;
import com.sun.javafx.collections.MappingChange.Map;

/**
 * @ClassName: BulletinInfoDao
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:50:50
 */
public interface BulletinInfoDao extends BaseMapper<BulletinInfo> {

	public List<BulletinInfo> getList(@Param("bulletinTitle") String bulletinTitle);
	public BulletinInfo getBulletinInfo(@Param("bulletinPk") String bulletinPk);

	public List<BulletinInfo> getUserBulletinInfoList(@Param("userPk") String userPk,
			@Param("bulletinTitle") String bulletinTitle, @Param("closeDate") String closeDate);

	public List<Map> getBulletinReadItemList(@Param("bulletinPk") String bulletinPk);

	public List<BulletinSend> getBuletinSendByBulletinId(@Param("bulletinPk") String bulletinPk);
	public List<Map> getBulletinSendList(@Param("bulletinPk") String bulletinPk,@Param("bulletinIsread") int bulletinIsread);
    public List<BulletinReadItem> getBulletinReadItemByBulletinPk(@Param("bulletinPk") String bulletinPk);
    
    public List<Map> getAllUserBulletinList2(@Param("bulletinPk") String bulletinPk);
    public List<Map>getAllUserBulletinList(@Param("bulletinPk") String bulletinPk,@Param("bulletinIsread") int bulletinIsread);

    public BulletinReadItem getBulletinReadItemByUserId(@Param("userId") String userId,@Param("bulletinPk") int bulletinPk,@Param("sendPk") int sendPk);

	BulletinInfo getBulletinData(@Param("id") String id);
}
