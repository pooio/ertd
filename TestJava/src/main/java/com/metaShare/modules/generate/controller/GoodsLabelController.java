package com.metaShare.modules.generate.controller;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.*;import com.metaShare.common.tool.state.Result;import com.metaShare.common.tool.state.ResultCode;import com.metaShare.common.utils.StringUtils;import com.metaShare.modules.BaseController;import com.metaShare.modules.sys.entity.SysDicinfo;import com.metaShare.modules.sys.service.SysDictService;import java.util.List;
@Controller
@CrossOrigin
@RequestMapping(value="/api/generate/GoodsLabel")
public class GoodsLabelController extends BaseController {
	@Autowired
	private com.metaShare.modules.generate.service.GoodsLabelService GoodsLabelService;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsService  GoodsService;
	
	@Autowired
	private com.metaShare.modules.generate.service.GoodsLabelsService  LabelsService;
	
	@Autowired
	SysDictService sysDictService;
	
		@ResponseBody
		@RequestMapping(value = "/save", method = { RequestMethod.POST })
		public Result save(@RequestBody com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel){
			try {
	            GoodsLabel = this.appendRefInfo(GoodsLabel);
				boolean flag =   GoodsLabelService.insert(GoodsLabel);
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
		public Result update(@RequestBody  com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel){
			try {
	            GoodsLabel = this.appendRefInfo(GoodsLabel);
	            boolean flag =   GoodsLabelService.updateById(GoodsLabel);
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
		public Result del(@RequestBody com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel) {
			try {
				boolean flag =   GoodsLabelService.deleteById(GoodsLabel.getId());
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
		public Result getInfo(@RequestBody com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel) {
			try {
				GoodsLabel =  GoodsLabelService.selectById(GoodsLabel.getId());
				return Result.resultInfo(ResultCode.SUCCESS,GoodsLabel);
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取数据失败");
	       }
		}
	
		@ResponseBody
		@RequestMapping(value = "/list", method = { RequestMethod.POST })
		public Result list(@RequestBody com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel) {
			try {
				return  GoodsLabelService.selectList(GoodsLabel.getPageNum(),GoodsLabel.getPageSize());
	       } catch (Exception e) {
	           e.printStackTrace();
	           return Result.resultInfo(ResultCode.FAILURE,"获取列表失败");
	        }
		}
	
	    @ResponseBody
	    @RequestMapping(value = "/allList", method = { RequestMethod.POST })
	    public Result allList() {
	        try {
				List<com.metaShare.modules.generate.entity.GoodsLabel> GoodsLabelList =    GoodsLabelService.selectList(null);
				return Result.resultInfo(ResultCode.SUCCESS,GoodsLabelList);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Result.resultInfo(ResultCode.FAILURE,"获取列表数据失败");
	        }
	    }
	
		private com.metaShare.modules.generate.entity.GoodsLabel appendRefInfo(com.metaShare.modules.generate.entity.GoodsLabel GoodsLabel){
	        try {
	                if(GoodsLabel!=null) {
						if (StringUtils.isNotEmpty(GoodsLabel.getGoodsId())) {						 com.metaShare.modules.generate.entity.Goods Goods =GoodsService.selectById(GoodsLabel.getGoodsId());						if (Goods != null) {							GoodsLabel.setGoodsName(Goods.getName());						}					}					
						if (StringUtils.isNotEmpty(GoodsLabel.getLabelsId())) {						 com.metaShare.modules.generate.entity.GoodsLabels Labels =LabelsService.selectById(GoodsLabel.getLabelsId());						if (Labels != null) {							GoodsLabel.setLabelsName(Labels.getName());						}					}					
	                }
	                return GoodsLabel; 
	        } catch (Exception e) {
	                e.printStackTrace();
	                throw e;
	        }
	    }
	
}
