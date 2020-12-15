/**  
 * @Title: BulletinReadItemService.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-19 12:19:01 
 */ 
package com.metaShare.modules.bulletin.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bulletin.dao.BulletinReadItemDao;
import com.metaShare.modules.bulletin.entity.BulletinReadItem;

/**  
 * @ClassName: BulletinReadItemService
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-19 12:19:01 
*/
@Service
@Transactional
public class BulletinReadItemService extends ServiceImpl<BulletinReadItemDao, BulletinReadItem> {

    @Autowired
    private BulletinReadItemDao bulletinReadItemDao;

    public void deleteBybulletinPk(String bulletinId) {
        bulletinReadItemDao.deleteBybulletinPk(bulletinId);
    }
}
