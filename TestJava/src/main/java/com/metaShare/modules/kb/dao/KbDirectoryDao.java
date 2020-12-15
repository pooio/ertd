/**  
 * @Title: KbDirectoryDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:26:50 
 */ 
package com.metaShare.modules.kb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.kb.entity.KbDirectory;

/**  
 * @ClassName: KbDirectoryDao
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:26:50 
*/
public interface KbDirectoryDao extends BaseMapper<KbDirectory> {

	public List<KbDirectory> finddirList(@Param("KbDirGbcode") String KbDirGbcode,@Param("kbDirName") String kbDirName);
	
	public String getMaxDirCodeByParentCode(@Param("KbDirGbcode") String KbDirGbcode);

	public KbDirectory getDirectoryByCodeAndFlag(@Param("kbDirCode") String kbDirCode);

	public List<KbDirectory> getKbdirOne();

	public List<KbDirectory> getKbdirTwo(@Param("KbDirGbcode") String KbDirGbcode);

}
