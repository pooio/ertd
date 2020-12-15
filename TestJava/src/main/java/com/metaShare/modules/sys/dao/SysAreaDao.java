/**  
 * @Title: SysAreaDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:11 
 */ 
package com.metaShare.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysArea;

/**  
 * @ClassName: SysAreaDao
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:11 
*/
public interface SysAreaDao extends BaseMapper<SysArea> {
	
	List<SysArea> findareaList(@Param("parentCode") String parentCode,@Param("areaName") String areaName);
	
}
