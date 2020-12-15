package com.metaShare.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRoleDataFilterDao;
import com.metaShare.modules.sys.entity.SysRoleDataFilter;

@Service
public class SysRoleDataFilterService extends ServiceImpl<SysRoleDataFilterDao,SysRoleDataFilter> {

    @Autowired
    private SysRoleDataFilterDao sysRoleDataFilterDao;

    public void deleteByRoleId(String id) {
        sysRoleDataFilterDao.deleteByRoleId(id);
    }
}
