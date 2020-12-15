/**
 * 
 */
package com.metaShare.modules.customize.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.customize.dao.CustomFormDao;
import com.metaShare.modules.customize.dao.CustomFormJsonTableDao;
import com.metaShare.modules.customize.entity.CustomForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomFormJsonTableService extends ServiceImpl<CustomFormJsonTableDao, CustomForm>{

    @Autowired
    private CustomFormJsonTableDao customFormJsonTableDao;

    public void createCustomTable(String tableName, String[] tableFields) {
        customFormJsonTableDao.createCustomTable(tableName,tableFields);
    }

    public void deleteTable(String formTableName) {
        customFormJsonTableDao.deleteTable(formTableName);
    }

    public void saveData(String dataTableName, Map<String, Object> dataMap) {
        customFormJsonTableDao.saveData(dataTableName,dataMap);
    }

    public void deleteData(String dataTableName, String id) {
        customFormJsonTableDao.deleteData(dataTableName,id);
    }

    public List<Map<String, Object>> getDataList(String dataTableName,int pageNum,int pageSize,String startTime,String endTime,String dataId,String sql) {
        return customFormJsonTableDao.getDataList(dataTableName,pageNum,pageSize,startTime,endTime,dataId,sql);
    }

    public int getDataTotal(String dataTableName,String startTime,String endTime,String dataId,String sql) {
        return customFormJsonTableDao.getDataTotal(dataTableName,startTime,endTime,dataId,sql);
    }

    public int judgeFieldExist(String dataTableName, String type) {
        return customFormJsonTableDao.judgeFieldExist(dataTableName,type);
    }

    public void insertFieldToTable(String dataTableName, String type, String key) {
        customFormJsonTableDao.insertFieldToTable(dataTableName,type,key);
    }

    public void updateProcessIdOnTable(String tableName, String businessKey, String processInstanceId) {
        customFormJsonTableDao.updateProcessIdOnTable(tableName,businessKey,processInstanceId);
    }

    public void updateData(String dataTableName, Map<String, Object> dataMap, String dataId) {
        customFormJsonTableDao.updataData(dataTableName,dataMap,dataId);
    }

    public List<Map> getAllDataListOnTable(String dataTableName) {
        return customFormJsonTableDao.getAllDataListOnTable(dataTableName);
    }

    public Map<String, String> getUserInfo(String dataTableName, String businessKey) {
        return customFormJsonTableDao.getUserInfo(dataTableName,businessKey);
    }

    public Map<String, Object> getDataById(String dataTableName, String dataId) {
        return customFormJsonTableDao.getDataById(dataTableName,dataId);
    }

    public List<Map<String,String>> getCustomDirData(String dataTableName, String column) {
        return customFormJsonTableDao.getCustomDirData(dataTableName,column);
    }
}
