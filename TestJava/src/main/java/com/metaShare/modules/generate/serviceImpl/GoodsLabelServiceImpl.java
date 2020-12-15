package com.metaShare.modules.generate.serviceImpl;
import com.baomidou.mybatisplus.mapper.Condition;import com.baomidou.mybatisplus.mapper.Wrapper;import com.baomidou.mybatisplus.service.impl.ServiceImpl;import com.metaShare.modules.sys.dao.SysDictDao;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.common.tool.pageTool.PageDTO;import com.metaShare.common.tool.pageTool.PageTool;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.DateUtil;import com.metaShare.common.utils.StringUtils;import org.apache.poi.ss.formula.functions.T;import org.aspectj.weaver.ast.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;import java.io.Serializable;import java.util.ArrayList;import java.util.List;

@Service
public class GoodsLabelServiceImpl extends ServiceImpl<com.metaShare.modules.generate.dao.GoodsLabelDao,com.metaShare.modules.generate.entity.GoodsLabel> implements com.metaShare.modules.generate.service.GoodsLabelService {
	
	@Autowired
	private SysDictDao sysDictDao;
	
	@Override
	@Transactional
	public boolean insert(com.metaShare.modules.generate.entity.GoodsLabel entity) {
		try {
		    super.insert(entity);
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	@Transactional
	public boolean updateById(com.metaShare.modules.generate.entity.GoodsLabel entity) {
		try {
		    super.updateById(entity);
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	@Transactional
	public boolean deleteById(Serializable id) {
		try {
		    super.deleteById(id);
	        return true;        } catch (Exception e) {            e.printStackTrace();            return false;        }    }
	
	@Override
	public com.metaShare.modules.generate.entity.GoodsLabel selectById(Serializable id) {
		com.metaShare.modules.generate.entity.GoodsLabel entity = super.selectById(id);
		try {
	        } catch (Exception e) {            e.printStackTrace();        }
	return entity;
	    }
	@Override
	public Result selectList(int pageNum,int pageSize) {
		try {
		   List<com.metaShare.modules.generate.entity.GoodsLabel> listInfo = this.selectList(null);
		    int total = listInfo.size();
		    PageDTO pageDTO = new PageTool<com.metaShare.modules.generate.entity.GoodsLabel>().getPage(listInfo, pageSize, pageNum);
		    return Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
		} catch (Exception e) {
		    e.printStackTrace();
		    return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
		}
	}
}
