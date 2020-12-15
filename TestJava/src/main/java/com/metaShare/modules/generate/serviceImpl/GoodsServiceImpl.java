package com.metaShare.modules.generate.serviceImpl;
import com.baomidou.mybatisplus.mapper.Condition;import com.baomidou.mybatisplus.mapper.Wrapper;import com.baomidou.mybatisplus.service.impl.ServiceImpl;import com.metaShare.modules.sys.dao.SysDictDao;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.common.tool.pageTool.PageDTO;import com.metaShare.common.tool.pageTool.PageTool;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.DateUtil;import com.metaShare.common.utils.StringUtils;import org.apache.poi.ss.formula.functions.T;import org.aspectj.weaver.ast.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;import java.io.Serializable;import java.util.ArrayList;import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<com.metaShare.modules.generate.dao.GoodsDao,com.metaShare.modules.generate.entity.Goods> implements com.metaShare.modules.generate.service.GoodsService {
	
	@Autowired
	private SysDictDao sysDictDao;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsDataService  DatasService;
	
	@Override
	@Transactional
	public boolean insert(com.metaShare.modules.generate.entity.Goods entity) {
		try {
		    super.insert(entity);
		    if(entity.getDatas() != null) {
		        entity.getDatas().setGoodsId(entity.getId()); 
		        }
		         DatasService.insert(entity.getDatas());
		
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	@Transactional
	public boolean updateById(com.metaShare.modules.generate.entity.Goods entity) {
		try {
		    super.updateById(entity);
		    if(entity.getDatas() != null) {
		        if(StringUtils.isEmpty(entity.getDatas().getId())){
		            DatasService.insert(entity.getDatas());
		        } else {
		            DatasService.updateById(entity.getDatas());
		        }
		    }
		
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	@Transactional
	public boolean deleteById(Serializable id) {
		try {
		    super.deleteById(id);
		    Wrapper DatasWrapper = Condition.create().eq("Goods_id", id);
		    DatasService.delete(DatasWrapper);
		
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	public com.metaShare.modules.generate.entity.Goods selectById(Serializable id) {
		com.metaShare.modules.generate.entity.Goods entity = super.selectById(id);
		try {
		    Wrapper DatasWrapper = Condition.create().eq("Goods_id", id).eq("deleted",false);
		    com.metaShare.modules.generate.entity.GoodsData Datas = DatasService.selectOne(DatasWrapper);
		    entity.setDatas(Datas);
		
	        } catch (Exception e) {            e.printStackTrace();        }
	return entity;
	    }
	@Override
	public Result selectList(int pageNum,int pageSize) {
		try {
		   List<com.metaShare.modules.generate.entity.Goods> listInfo = this.selectList(null);
		    int total = listInfo.size();
		    PageDTO pageDTO = new PageTool<com.metaShare.modules.generate.entity.Goods>().getPage(listInfo, pageSize, pageNum);
		    return Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
		} catch (Exception e) {
		    e.printStackTrace();
		    return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
		}
	}
	@Override    public Result getGoodsTypeEnumList() {        try {            List enumList = com.metaShare.modules.generate.entity.GoodsType.findEnumList();            return Result.resultInfo(ResultCode.SUCCESS,enumList);        } catch (Exception e) {            e.printStackTrace();            return Result.resultInfo(ResultCode.FAILURE,"获取枚举列表失败");        }    }
	@Override    public Result getStatusEnumList() {        try {            List enumList = com.metaShare.modules.generate.entity.GoodsStatus.findEnumList();            return Result.resultInfo(ResultCode.SUCCESS,enumList);        } catch (Exception e) {            e.printStackTrace();            return Result.resultInfo(ResultCode.FAILURE,"获取枚举列表失败");        }    }
}
