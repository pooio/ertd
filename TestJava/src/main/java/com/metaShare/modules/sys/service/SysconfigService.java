package com.metaShare.modules.sys.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysConfigDao;
import com.metaShare.modules.sys.entity.SysConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysconfigService extends ServiceImpl<SysConfigDao, SysConfig> {

    @Autowired
    private  SysConfigDao sysConfigDao;
     public List<SysConfig> getList(String configType,String allData){
       return  sysConfigDao.getList(configType,allData);
    }
    public SysConfig getConfigData(String configType){
        return  sysConfigDao.getConfigData(configType);
    }
    public SysConfig getData(int configId){
        return  sysConfigDao.getData(configId);
    }
}
