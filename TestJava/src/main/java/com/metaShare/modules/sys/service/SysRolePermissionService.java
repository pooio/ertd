package com.metaShare.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRolePermissionDao;
import com.metaShare.modules.sys.entity.SysRolePermission;

@Service
public class SysRolePermissionService extends ServiceImpl<SysRolePermissionDao,SysRolePermission> {

    @Autowired
    private SysRolePermissionDao sysRolePermissionDao;

    public void deleteByRoleId(String id) {
        sysRolePermissionDao.deleteByRoleId(id);
    }
}
