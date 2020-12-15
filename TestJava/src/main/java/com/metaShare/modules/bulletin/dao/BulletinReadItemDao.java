/**  
 * @Title: BulletinReadItemDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:51:30 
 */ 
package com.metaShare.modules.bulletin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.bulletin.entity.BulletinReadItem;
import org.apache.ibatis.annotations.Mapper;

/**  
 * @ClassName: BulletinReadItemDao
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:51:30 
*/
public interface BulletinReadItemDao extends BaseMapper<BulletinReadItem> {

    void deleteBybulletinPk(String bulletinId);
}
