package com.metaShare.modules.generate.service;


public interface GoodsLabelsService extends IService<com.metaShare.modules.generate.entity.GoodsLabels> {
	Result selectList(int pageNum,int pageSize);
	
	Result getLabelTypeEnumList();
}