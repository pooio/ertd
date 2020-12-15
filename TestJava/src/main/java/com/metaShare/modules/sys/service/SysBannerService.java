package com.metaShare.modules.sys.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.metaShare.modules.sys.dao.SysBannerDao;
import com.metaShare.modules.sys.entity.SysBanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SysBannerService extends ServiceImpl<SysBannerDao, SysBanner> {

    @Autowired
    private  SysBannerDao sysBannerDao;
    public List<SysBanner> getList(){
        return  sysBannerDao.getList();
    }

}
