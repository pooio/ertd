package com.metaShare.modules.generate.service;


public interface GoodsDataService extends IService<com.metaShare.modules.generate.entity.GoodsData> {
	Result selectList(int pageNum,int pageSize);
	
}