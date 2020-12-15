package com.metaShare.modules.sys.service;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysRoleOrgDao;
import com.metaShare.modules.sys.entity.SysRoleOrg;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class SysRoleOrgService extends ServiceImpl<SysRoleOrgDao, SysRoleOrg>{

}
