package com.metaShare.modules.generate.controller;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.*;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.StringUtils;import com.metaShare.modules.BaseController;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.modules.sys.service.SysDictService;import java.util.List;
@Controller
@CrossOrigin
@RequestMapping(value="/api/generate/GoodsLabels")
public class GoodsLabelsController extends BaseController {
	@Autowired
	private com.metaShare.modules.generate.service.GoodsLabelsService GoodsLabelsService;
	
	@Autowired
	SysDictService sysDictService;
	
		@ResponseBody
		@RequestMapping(value = "/save", method = { RequestMethod.POST })
		public Result save(@RequestBody com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels){
			try {
	            GoodsLabels = this.appendRefInfo(GoodsLabels);
				boolean flag =   GoodsLabelsService.insert(GoodsLabels);
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
		public Result update(@RequestBody  com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels){
			try {
	            GoodsLabels = this.appendRefInfo(GoodsLabels);
	            boolean flag =   GoodsLabelsService.updateById(GoodsLabels);
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
		public Result del(@RequestBody com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels) {
			try {
				boolean flag =   GoodsLabelsService.deleteById(GoodsLabels.getId());
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
		public Result getInfo(@RequestBody com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels) {
			try {
				GoodsLabels =  GoodsLabelsService.selectById(GoodsLabels.getId());
				return Result.resultInfo(ResultCode.SUCCESS,GoodsLabels);
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取数据失败");
	       }
		}
	
		@ResponseBody
		@RequestMapping(value = "/list", method = { RequestMethod.POST })
		public Result list(@RequestBody com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels) {
			try {
				return  GoodsLabelsService.selectList(GoodsLabels.getPageNum(),GoodsLabels.getPageSize());
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取列表失败");
	        }
		}
	
	    @ResponseBody
	    @RequestMapping(value = "/allList", method = { RequestMethod.POST })
	    public Result allList() {
	        try {
				List<com.metaShare.modules.generate.entity.GoodsLabels> GoodsLabelsList =    GoodsLabelsService.selectList(null);
				return Result.resultInfo(ResultCode.SUCCESS,GoodsLabelsList);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
	        }
	    }
	
		@RequestMapping(value = "/getLabelTypeEnumList")	@ResponseBody	public Result getLabelTypeEnumList(){		try {			return GoodsLabelsService.getLabelTypeEnumList();		} catch (Exception e) {			e.printStackTrace();			return Result.resultInfo(ResultCode.FAILURE,"获取枚举列表失败");		}	}
		private com.metaShare.modules.generate.entity.GoodsLabels appendRefInfo(com.metaShare.modules.generate.entity.GoodsLabels GoodsLabels){
	        try {
	                if(GoodsLabels!=null) {
	                }
	                return GoodsLabels; 
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw e;
	        }
	    }
	
}
