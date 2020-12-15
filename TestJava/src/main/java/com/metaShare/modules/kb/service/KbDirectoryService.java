/**  
 * @Title: KbDirectoryService.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:31:00 
 */
package com.metaShare.modules.kb.service;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.kb.dao.KbDirectoryDao;
import com.metaShare.modules.kb.entity.KbDirectory;

/**
 * @ClassName: KbDirectoryService
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:31:00
 */
@Service
@Transactional
public class KbDirectoryService extends ServiceImpl<KbDirectoryDao, KbDirectory> {

	@Autowired
	private KbDirectoryDao kbDirectoryDao;

	public String getMaxDirCodeByParentCode(String parentCode) {
		return kbDirectoryDao.getMaxDirCodeByParentCode(parentCode);
	}
	public KbDirectory getDirectoryByCodeAndFlag(String dirCode) {
		return kbDirectoryDao.getDirectoryByCodeAndFlag(dirCode);
	}
}
