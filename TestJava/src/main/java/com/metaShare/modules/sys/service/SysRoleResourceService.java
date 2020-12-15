package com.metaShare.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRoleResourceDao;
import com.metaShare.modules.sys.entity.SysRoleResource;

@Service
public class SysRoleResourceService extends ServiceImpl<SysRoleResourceDao,SysRoleResource> {

    @Autowired
    private SysRoleResourceDao sysRoleResourceDao;

    public void deleteOnRoleIdAndResArr(String roleId, String[] allBtnId) {
        sysRoleResourceDao.deleteOnRoleIdAndResArr(roleId,allBtnId);
    }

    public void deleteByRoleId(String id) {
        sysRoleResourceDao.deleteByRoleId(id);
    }
}
