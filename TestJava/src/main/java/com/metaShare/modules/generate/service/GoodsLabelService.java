package com.metaShare.modules.generate.service;


public interface GoodsLabelService extends IService<com.metaShare.modules.generate.entity.GoodsLabel> {
	Result selectList(int pageNum,int pageSize);
	
}