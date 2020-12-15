package com.metaShare.modules.generate.service;
import com.baomidou.mybatisplus.service.IService;import com.metaShare.common.tool.state.Result;import java.util.List;

public interface GoodsDataService extends IService<com.metaShare.modules.generate.entity.GoodsData> {
	Result selectList(int pageNum,int pageSize);
	
}
