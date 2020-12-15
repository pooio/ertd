package com.metaShare.modules.core.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.core.dao.SysFileAttachDao;
import com.metaShare.modules.core.entity.SysFileAttach;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SysFileAttachService extends ServiceImpl<SysFileAttachDao, SysFileAttach> {
}
