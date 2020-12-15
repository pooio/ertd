package com.metaShare.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysInfoDao;
import com.metaShare.modules.sys.entity.SysInfo;


@Service
@Transactional
public class PoSysInfoService extends ServiceImpl<SysInfoDao, SysInfo> {


    @Autowired
    private SysInfoDao sysInfoDao;

    public List<SysInfo> querySysInfoList(String userName, String startTime, String endTime, String module) {

        return sysInfoDao.getSysInfoList(userName,startTime,endTime,module);
    }
}
