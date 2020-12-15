/**
 * 
 */
package com.metaShare.modules.customize.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.customize.dao.CustomDataDao;
import com.metaShare.modules.customize.dao.CustomRelationalDao;
import com.metaShare.modules.customize.entity.CustomData;
import com.metaShare.modules.customize.entity.CustomForm;
import com.metaShare.modules.customize.entity.CustomRelational;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomRelationalService extends ServiceImpl<CustomRelationalDao, CustomRelational>{

    @Autowired
    private CustomRelationalDao customRelationalDao;

    public void deleteFromChildrenId(String id) {
        customRelationalDao.deleteFromChildrenId(id);
    }

    public CustomRelational selectByChildrenId(String id) {
        return customRelationalDao.selectByChildrenId(id);
    }

    public List<CustomForm> getChildrenCustomTable(String formId) {
        return customRelationalDao.getChildrenCustomTable(formId);
    }
}
