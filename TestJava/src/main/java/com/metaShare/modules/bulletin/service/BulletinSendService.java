/**  
 * @Title: bulletinSendService.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 01:17:00 
 */ 
package com.metaShare.modules.bulletin.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.bulletin.dao.BulletinSendDao;
import com.metaShare.modules.bulletin.entity.BulletinSend;

/**  
 * @ClassName: bulletinSendService
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 01:17:00 
*/
@Service
@Transactional
public class BulletinSendService extends ServiceImpl<BulletinSendDao, BulletinSend> {

}
