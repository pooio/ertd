package com.metaShare.modules.generate.service;


public interface GoodsService extends IService<com.metaShare.modules.generate.entity.Goods> {
	Result selectList(int pageNum,int pageSize);
	
	Result getGoodsTypeEnumList();
	Result getStatusEnumList();
}