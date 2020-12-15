/**  
 * @Title: SysAreaDao.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-13 10:05:11 
 */ 
package com.metaShare.modules.customize.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.customize.entity.CustomForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomFormJsonTableDao extends BaseMapper<CustomForm> {

    void createCustomTable(@Param("tableName") String tableName,@Param("array") String[] array);

    void deleteTable(@Param("formTableName") String formTableName);

    void saveData(@Param("tableName") String dataTableName,@Param("dataMap") Map<String, Object> dataMap);

    void deleteData(@Param("tableName") String dataTableName,@Param("id") String id);

    List<Map<String, Object>> getDataList(@Param("tableName") String dataTableName,@Param("pageNumber")int pageNumber,@Param("pageSize")int pageSize,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("dataId")String dataId,@Param("sql")String sql);

    int getDataTotal(@Param("tableName") String dataTableName,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("dataId")String dataId,@Param("sql")String sql);

    int judgeFieldExist(@Param("dataTableName") String dataTableName,@Param("type") String type);

    void insertFieldToTable(@Param("dataTableName") String dataTableName,@Param("type") String type,@Param("key") String key);

    void updateProcessIdOnTable(@Param("tableName") String tableName,@Param("businessKey") String businessKey,@Param("processInstanceId") String processInstanceId);

    void updataData(@Param("tableName") String dataTableName,@Param("dataMap") Map<String, Object> dataMap,@Param("dataId") String dataId);

    List<Map> getAllDataListOnTable(@Param("tableName") String dataTableName);

    Map<String, String> getUserInfo(@Param("dataTableName") String dataTableName,@Param("businessKey") String businessKey);

    Map<String, Object> getDataById(@Param("dataTableName") String dataTableName,@Param("dataId") String dataId);

    List<Map<String,String>> getCustomDirData(@Param("dataTableName") String dataTableName,@Param("column") String column);
}
