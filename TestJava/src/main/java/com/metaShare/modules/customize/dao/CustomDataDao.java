/**  
 * @Title: SysAreaDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:11 
 */ 
package com.metaShare.modules.customize.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.customize.entity.CustomData;
import com.metaShare.modules.customize.entity.CustomForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomDataDao extends BaseMapper<CustomData> {


    CustomData getDataJson(@Param("businessKey") String businessKey);

    CustomData getDataJsonById(@Param("id") String id);
}
