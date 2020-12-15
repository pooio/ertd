package com.metaShare.modules.generate.controller;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.*;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.StringUtils;import com.metaShare.modules.BaseController;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.modules.sys.service.SysDictService;import java.util.List;
@Controller
@CrossOrigin
@RequestMapping(value="/api/generate/Goods")
public class GoodsController extends BaseController {
	@Autowired
	private com.metaShare.modules.generate.service.GoodsService GoodsService;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsDataService  DatasService;
	
	@Autowired
	SysDictService sysDictService;
	
		@ResponseBody
		@RequestMapping(value = "/save", method = { RequestMethod.POST })
		public Result save(@RequestBody com.metaShare.modules.generate.entity.Goods Goods){
			try {
	            Goods = this.appendRefInfo(Goods);
				boolean flag =   GoodsService.insert(Goods);
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
		public Result update(@RequestBody  com.metaShare.modules.generate.entity.Goods Goods){
			try {
	            Goods = this.appendRefInfo(Goods);
	            boolean flag =   GoodsService.updateById(Goods);
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
		public Result del(@RequestBody com.metaShare.modules.generate.entity.Goods Goods) {
			try {
				boolean flag =   GoodsService.deleteById(Goods.getId());
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
		public Result getInfo(@RequestBody com.metaShare.modules.generate.entity.Goods Goods) {
			try {
				Goods =  GoodsService.selectById(Goods.getId());
				return Result.resultInfo(ResultCode.SUCCESS,Goods);
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取数据失败");
	       }
		}
	
		@ResponseBody
		@RequestMapping(value = "/list", method = { RequestMethod.POST })
		public Result list(@RequestBody com.metaShare.modules.generate.entity.Goods Goods) {
			try {
				return  GoodsService.selectList(Goods.getPageNum(),Goods.getPageSize());
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取列表失败");
	        }
		}
	
	    @ResponseBody
	    @RequestMapping(value = "/allList", method = { RequestMethod.POST })
	    public Result allList() {
	        try {
				List<com.metaShare.modules.generate.entity.Goods> GoodsList =    GoodsService.selectList(null);
				return Result.resultInfo(ResultCode.SUCCESS,GoodsList);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
	        }
	    }
	
		@RequestMapping(value = "/getGoodsTypeEnumList")	@ResponseBody	public Result getGoodsTypeEnumList(){		try {			return GoodsService.getGoodsTypeEnumList();		} catch (Exception e) {			e.printStackTrace();			return Result.resultInfo(ResultCode.FAILURE,"获取枚举列表失败");		}	}
		@RequestMapping(value = "/getStatusEnumList")	@ResponseBody	public Result getStatusEnumList(){		try {			return GoodsService.getStatusEnumList();		} catch (Exception e) {			e.printStackTrace();			return Result.resultInfo(ResultCode.FAILURE,"获取枚举列表失败");		}	}
		private com.metaShare.modules.generate.entity.Goods appendRefInfo(com.metaShare.modules.generate.entity.Goods Goods){
	        try {
	                if(Goods!=null) {
	
	                }
	                return Goods; 
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw e;
	        }
	    }
	
}
