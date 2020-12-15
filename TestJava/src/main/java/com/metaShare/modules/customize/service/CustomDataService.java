/**
 * 
 */
package com.metaShare.modules.customize.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.customize.dao.CustomDataDao;
import com.metaShare.modules.customize.dao.CustomFormDao;
import com.metaShare.modules.customize.entity.CustomData;
import com.metaShare.modules.customize.entity.CustomForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomDataService extends ServiceImpl<CustomDataDao, CustomData>{

    @Autowired
    private CustomDataDao customDataDao;

    public CustomData getDataJson(String businessKey) {
        return customDataDao.getDataJson(businessKey);
    }

    public CustomData getDataJsonById(String id) {
        return customDataDao.getDataJsonById(id);
    }
}
