/**  
 * @Title: SysAreaDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:11 
 */ 
package com.metaShare.modules.customize.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.sys.entity.SysArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomFormDao extends BaseMapper<CustomForm> {

    List<CustomForm> getList(@Param("businessName") String businessName,@Param("type")Integer type);

    CustomForm selectByIdentification(@Param("formIdentification") String formIdentification);

    CustomForm selectByFormName(@Param("formName") String formName);

    int selectByDefinitionKey(@Param("key") String key);

    Map<String, String> getModelName(@Param("processDefinitionKey") String processDefinitionKey);

    List<CustomForm> getCustomListName(@Param("customName")String customName);

    void updateDefinitionKey(@Param("processDefinitionKey") String processDefinitionKey,@Param("id") String id);

    int getBySpell(@Param("spell") String spell);

    void updateToDelete(@Param("id") String id);

    List<CustomForm> getIsPublishCustomList();

    CustomForm getFormById(@Param("id") String id);
}
