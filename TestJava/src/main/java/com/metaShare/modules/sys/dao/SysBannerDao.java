package com.metaShare.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.metaShare.modules.sys.entity.SysBanner;

import java.util.List;


public interface SysBannerDao extends BaseMapper<SysBanner> {
      List<SysBanner> getList();
}
