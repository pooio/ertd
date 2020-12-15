package com.metaShare.modules.generate.controller;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.*;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.StringUtils;import com.metaShare.modules.BaseController;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.modules.sys.service.SysDictService;import java.util.List;
@Controller
@CrossOrigin
@RequestMapping(value="/api/generate/GoodsData")
public class GoodsDataController extends BaseController {
	@Autowired
	private com.metaShare.modules.generate.service.GoodsDataService GoodsDataService;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsService  GoodsService;
	
	@Autowired
	SysDictService sysDictService;
	
		@ResponseBody
		@RequestMapping(value = "/save", method = { RequestMethod.POST })
		public Result save(@RequestBody com.metaShare.modules.generate.entity.GoodsData GoodsData){
			try {
	            GoodsData = this.appendRefInfo(GoodsData);
				boolean flag =   GoodsDataService.insert(GoodsData);
				if(flag){
					return Result.resultInfo(ResultCode.SUCCESS,"保存成功");
				}else{
					return Result.resultInfo(ResultCode.FAILURE,"保存失败");
				}
			} catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"保存失败");
	        }
	    }
	
		@ResponseBody
		@RequestMapping(value = "/update", method = { RequestMethod.POST })
		public Result update(@RequestBody  com.metaShare.modules.generate.entity.GoodsData GoodsData){
			try {
	            GoodsData = this.appendRefInfo(GoodsData);
	            boolean flag =   GoodsDataService.updateById(GoodsData);
	            if(flag){
	                return Result.resultInfo(ResultCode.SUCCESS,"编辑成功");
	            }else{
	                return Result.resultInfo(ResultCode.FAILURE,"编辑失败");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Result.resultInfo(ResultCode.FAILURE,"编辑失败");
	        }
	    }
	
		@ResponseBody
		@RequestMapping(value = "/del", method = { RequestMethod.POST })
		public Result del(@RequestBody com.metaShare.modules.generate.entity.GoodsData GoodsData) {
			try {
				boolean flag =   GoodsDataService.deleteById(GoodsData.getId());
				if(flag){
					return Result.resultInfo(ResultCode.SUCCESS,"删除成功");
				}else{
					return Result.resultInfo(ResultCode.FAILURE,"删除失败");
				}
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"删除失败");
	        }
		}
	
		@ResponseBody
		@RequestMapping(value = "/getInfo")
		public Result getInfo(@RequestBody com.metaShare.modules.generate.entity.GoodsData GoodsData) {
			try {
				GoodsData =  GoodsDataService.selectById(GoodsData.getId());
				return Result.resultInfo(ResultCode.SUCCESS,GoodsData);
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取数据失败");
	       }
		}
	
		@ResponseBody
		@RequestMapping(value = "/list", method = { RequestMethod.POST })
		public Result list(@RequestBody com.metaShare.modules.generate.entity.GoodsData GoodsData) {
			try {
				return  GoodsDataService.selectList(GoodsData.getPageNum(),GoodsData.getPageSize());
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取列表失败");
	        }
		}
	
	    @ResponseBody
	    @RequestMapping(value = "/allList", method = { RequestMethod.POST })
	    public Result allList() {
	        try {
				List<com.metaShare.modules.generate.entity.GoodsData> GoodsDataList =    GoodsDataService.selectList(null);
				return Result.resultInfo(ResultCode.SUCCESS,GoodsDataList);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
	        }
	    }
	
		private com.metaShare.modules.generate.entity.GoodsData appendRefInfo(com.metaShare.modules.generate.entity.GoodsData GoodsData){
	        try {
	                if(GoodsData!=null) {
						if (StringUtils.isNotEmpty(GoodsData.getGoodsId())) {						 com.metaShare.modules.generate.entity.Goods Goods =GoodsService.selectById(GoodsData.getGoodsId());						if (Goods != null) {							GoodsData.setGoodsName(Goods.getName());						}					}					
	                }
	                return GoodsData; 
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw e;
	        }
	    }
	
}
