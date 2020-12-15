/**
 * @Title: KbDirectoryController.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:27:29
 */
package com.metaShare.modules.kb.controller;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.kb.dao.KbDirectoryDao;
import com.metaShare.modules.kb.entity.KbContent;
import com.metaShare.modules.kb.entity.KbDirectory;
import com.metaShare.modules.kb.service.KbContentService;
import com.metaShare.modules.kb.service.KbDirectoryService;
import com.metaShare.modules.sys.entity.SysDicinfo;
import com.metaShare.modules.sys.entity.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: KbDirectoryController
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-17 11:27:29 
*/
@Controller
@CrossOrigin
@Api(tags = "知识库目录")
@RequestMapping(value = "/api/kb/dir")
public class KbDirectoryController extends BaseController {
	@Autowired
	private KbDirectoryService kbDirectoryService;
	@Autowired
	private KbDirectoryDao KbDirectoryDao;
	@Autowired
	KbContentService  kbContentService;
	 /*@RequestMapping(value="treeData" , method={RequestMethod.POST})
	 @ApiOperation("树结构数据")
	 @ResponseBody
	 public Result treeData() {
			List<KbDirectory> normalList = new ArrayList<>();
			KbDirectory data = recursiveTree("0",null);
			normalList.add(data);
			return Result.resultInfo(ResultCode.SUCCESS,normalList);
		}
		public KbDirectory recursiveTree(String id,KbDirectory kbDirectory) {
			// 根据cid获取节点对象
			KbDirectory node = kbDirectoryService.selectById(id);
			if(node== null){
				node = new KbDirectory();
				node.setId(1);
				node.setKbDirCode("000000000000");
				node.setKbDirGbcode("-1");
				node.setKbDirName("知识库目录");
			}
			// 查询cid下的所有子节点
			List<KbDirectory> childTreeNodes = KbDirectoryDao.finddirList(node.getKbDirCode(),"");
			// 遍历子节点
			for (KbDirectory child : childTreeNodes) {
				KbDirectory n = recursiveTree(child.getId()+"",node); // 递归
				node.getChildren().add(n);
			}
	//
			return node;
		}*/


    @RequestMapping(value="treeData" , method={RequestMethod.POST})
    @ApiOperation("树结构数据")
    @ResponseBody
    public Result treeData() {
        List<KbDirectory> normalList = new ArrayList<>();
        KbDirectory data = recursiveTree("0");
        normalList.add(data);
        return Result.resultInfo(ResultCode.SUCCESS,normalList);
    }
    public KbDirectory recursiveTree(String id) {
        // 根据cid获取节点对象
        KbDirectory node = kbDirectoryService.selectById(id);
        if(node== null){
            node = new KbDirectory();
            node.setId(1);
            node.setKbDirCode("000000000000");
            node.setKbDirGbcode("-1");
            node.setKbDirName("知识库目录");
        }
        // 查询cid下的所有子节点
        List<KbDirectory> childTreeNodes = KbDirectoryDao.finddirList(node.getKbDirCode(),"");
        // 遍历子节点
        for (KbDirectory child : childTreeNodes) {
            KbDirectory n = recursiveTree(child.getId()+""); // 递归
            node.getChildren().add(n);
        }
        //
        return node;
    }

		@ApiOperation(value="列表数据")
		@ResponseBody
		@ApiImplicitParams({
			 @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int"),
			 @ApiImplicitParam(name = "pageSize", value = "每页大小", required = true, dataType = "int"),
			 @ApiImplicitParam(name = "KbDirGbcode", value = "目录父节点编码", required = true, dataType = "String"),
			 @ApiImplicitParam(name = "kbDirName", value = "目录名称（查询条件）", required = true, dataType = "String")
		})
		@RequestMapping(value="list" , method={RequestMethod.POST})
		public Result list(Integer pageNum, Integer pageSize, String KbDirGbcode,String kbDirName) {
			PageDTO<KbDirectory> pageDTO = null;
			List<KbDirectory> ListDic = KbDirectoryDao.finddirList(KbDirGbcode,kbDirName);
			pageDTO = new PageTool<KbDirectory>().getPage(ListDic, pageSize, pageNum);
			int total=ListDic.size();
			return Result.resultInfo(ResultCode.SUCCESS, total,pageDTO.getData());
		}


		@ApiOperation("目录保存")
		@RequestMapping(value="/save",method={RequestMethod.POST})
		@ApiImplicitParams({
			 @ApiImplicitParam(name = "KbDirName", value = "目录名称", required = true, dataType = "String"),
			 @ApiImplicitParam(name = "KbDirGbcode", value = "目录父节点编码", required = true, dataType = "String")
		})
		@ResponseBody
		public Result sava(KbDirectory kbDirectory){

			Wrapper<KbDirectory> wrapper = Condition.create();
			wrapper.eq("kb_dir_code", kbDirectory.getKbDirGbcode());
			List<KbDirectory> list = kbDirectoryService.selectList(wrapper);
			if(list.size()>0){
				KbDirectory gbKbDirectory=list.get(0);
				if(gbKbDirectory != null){
					kbDirectory.setKbDirClevel(gbKbDirectory.getKbDirClevel()+1);
		        }else{
		        	kbDirectory.setKbDirClevel(1);
		        }
			}else{
				kbDirectory.setKbDirClevel(1);
			}

			kbDirectory.setCreateTime(DateUtil.toStr(new Date()));
			kbDirectory.setKbDirCode(createDirCode(kbDirectory.getKbDirClevel(),kbDirectory.getKbDirGbcode()));
			kbDirectoryService.insert(kbDirectory);
			String operator = getUserInfo().getName();
			String info = "新增了" + kbDirectory.getKbDirName() + "知识库目录";
			saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		}
		@ApiOperation("目录修改")
		@ResponseBody
		@RequestMapping(value="/update" , method={RequestMethod.POST})
		@ApiImplicitParams({
			 @ApiImplicitParam(name = "KbDirName", value = "目录名称", required = true, dataType = "int"),
			 @ApiImplicitParam(name = "KbDirName", value = "目录名称", required = true, dataType = "String"),
			 @ApiImplicitParam(name = "kbDirCode", value = "目录编码", required = true, dataType = "String"),
			 @ApiImplicitParam(name = "KbDirGbcode", value = "目录父节点编码", required = true, dataType = "String"),
		})
		public Result updata(KbDirectory kbDirectory){
            kbDirectoryService.updateById(kbDirectory);
			String operator = getUserInfo().getName();
			String info = "修改了" + kbDirectory.getKbDirName() + "知识库目录";
			saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
		}
		@ApiOperation("删除")
		@ResponseBody
		@RequestMapping(value="/del" ,method={RequestMethod.POST})
		@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="主键" ,required=true ,dataType="String")
		})
		public Result del(String id){


			Wrapper<KbContent> wrapper = Condition.create();
			wrapper.eq("kb_dir_pk", id);
			List<KbContent> list = kbContentService.selectList(wrapper);
			if(list.size()>0){
				return Result.resultInfo(ResultCode.FAILURE,"该目录下存在知识点，不能删除");
			}

			KbDirectory kbDirectory = kbDirectoryService.selectById(id);
			if(kbDirectory!=null){
				List<KbDirectory> childTreeNodes = KbDirectoryDao.finddirList(kbDirectory.getKbDirCode(),"");
				if(childTreeNodes.size()>0){
					return Result.resultInfo(ResultCode.FAILURE,"该目录下存在下级目录，不能删除");
				}
				kbDirectoryService.deleteById(Integer.valueOf(id));
				String operator = getUserInfo().getName();
				String info = "删除了" + kbDirectory.getKbDirName() + "知识库目录";
				saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
				return Result.resultInfo(ResultCode.SUCCESS,"删除成功");
			}else{
				return Result.resultInfo(ResultCode.FAILURE,"参数错误");
			}


		}
	    /**
	     * 检查同目录是否存在同名信息
	     */
		@ApiOperation("检查同目录是否存在同名信息")
		@ResponseBody
		@RequestMapping(value="/checkDirName" ,method={RequestMethod.POST})
		@ApiImplicitParams({
			@ApiImplicitParam(name="id",value="主键" ,required=true ,dataType="String"),
			@ApiImplicitParam(name="kbDirGbcode",value="父节点编码" ,required=true ,dataType="String"),
			@ApiImplicitParam(name="KbDirName",value="目录名称" ,required=true ,dataType="String")
		})
	    public Result checkDirName(String id ,String kbDirGbcode,String kbDirName){
	    	Wrapper<KbDirectory> wrapper = Condition.create().eq("kb_dir_gbcode", kbDirGbcode);
	    	wrapper.eq("kb_dir_name", kbDirName);
	    	if(!StrUtils.isEmpty(id)){
	    		wrapper.ne("id", id);
	    	}
			List<KbDirectory> list = kbDirectoryService.selectList(wrapper);
			if (list.size()<1) {
				return Result.resultInfo(ResultCode.SUCCESS, "");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, "目录已经存在");
			}
	    }

		  /**
	     * 创建知识库目录编号
	     * @param parent_code 父编号
	     * @return
	     */
	    protected String createDirCode(int level, String parent_code){
	        StringBuffer dir = new StringBuffer();
	        dir.append(parent_code.substring(0, level*3));

	        String num = "";
	        String kbDirectory = kbDirectoryService.getMaxDirCodeByParentCode(parent_code);
	        String maxDirCode="";
	        if(kbDirectory!= null){
	        	maxDirCode=kbDirectory;
	        	if (!StrUtils.isEmpty(maxDirCode)){
		            int endIndex = level*3+3;
		            if (endIndex > 12)
		                endIndex = 12;
		            String dirCode = maxDirCode.substring(level*3, endIndex);
	                num = String.format("%03d",Integer.parseInt(dirCode)+1);
		        }
	        }else{
	        	num = "001001";
			}

	        if (StrUtils.isEmpty(num)){
	            num = "001";
	        }
	        dir.append(num);
	        while(dir.length() < 12) {
	            dir.append("0");
	        }
	        return dir.toString();
	    }
	/**
	 * 一级目录
	 */
	@ApiOperation("一级目录")
	@ResponseBody
	@RequestMapping(value="/getKbdirOne" ,method={RequestMethod.POST})
	public Result getKbdirOne(){
		List<KbDirectory> childTreeNodes = KbDirectoryDao.getKbdirOne();
		return Result.resultInfo(ResultCode.SUCCESS, childTreeNodes);
	}
	/**
	 * 下一级目录
	 */
	@ApiOperation("下一级目录")
	@ResponseBody
	@RequestMapping(value="/getKbdirTwoOrThree" ,method={RequestMethod.POST})
	public Result getKbdirTwoOrThree(String KbDirGbcode){
		List<KbDirectory> childTreeNodes = KbDirectoryDao.getKbdirTwo(KbDirGbcode);
		return Result.resultInfo(ResultCode.SUCCESS, childTreeNodes);
	}


}
