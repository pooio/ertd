/**  
 * @Title: SysAreaService.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:06:21 
 */ 
package com.metaShare.modules.sys.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysAreaDao;
import com.metaShare.modules.sys.entity.SysArea;

/**  
 * @ClassName: SysAreaService
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:06:21 
*/
@Service
@Transactional
public class SysAreaService extends ServiceImpl<SysAreaDao, SysArea> {

}
