/**
 * 
 */
package com.metaShare.modules.customize.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.customize.dao.CustomFormDao;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.sys.dao.SysDictDao;
import com.metaShare.modules.sys.entity.SysDicinfo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomFormService extends ServiceImpl<CustomFormDao, CustomForm>{

    @Autowired
    private CustomFormDao customFormDao;

    public List<CustomForm> getList(String businessName, Integer type) {
        return customFormDao.getList(businessName,type);
    }

    public CustomForm selectByIdentification(String formIdentification) {
        return customFormDao.selectByIdentification(formIdentification);
    }

    public CustomForm selectByFormName(String formName) {
        return customFormDao.selectByFormName(formName);
    }

    public int selectByDefinitionKey(String key) {
        return customFormDao.selectByDefinitionKey(key);
    }

    public Map<String,String> getModelName(String processDefinitionKey) {
        return customFormDao.getModelName(processDefinitionKey);
    }

    public List<CustomForm> getCustomListName(String customName) {
        return customFormDao.getCustomListName(customName);
    }

    public void updateDefinitionKey(String processDefinitionKey, String id) {
        customFormDao.updateDefinitionKey(processDefinitionKey,id);
    }

    public int getBySpell(String spell) {
        return customFormDao.getBySpell(spell);
    }

    public void updateToDelete(String id) {
        customFormDao.updateToDelete(id);
    }

    public List<CustomForm> getIsPublishCustomList() {
        return customFormDao.getIsPublishCustomList();
    }

    public CustomForm getFormById(String id) {
        return customFormDao.getFormById(id);
    }
}
