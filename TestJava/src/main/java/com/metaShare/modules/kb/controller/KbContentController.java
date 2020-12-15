package com.metaShare.modules.kb.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.metaShare.common.utils.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.kb.entity.KbContent;
import com.metaShare.modules.kb.entity.KbDirectory;
import com.metaShare.modules.kb.service.KbContentService;
import com.metaShare.modules.kb.service.KbDirectoryService;
import com.metaShare.modules.sys.entity.SysDicinfo;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysDictService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Created by pc on 2020/2/18.
 */
@Controller
@CrossOrigin
@Api(tags = "知识库")
@RequestMapping(value = "/api/kb/content")
public class KbContentController extends BaseController {

	@Autowired
	private KbContentService kbContentService;

	@Autowired
	private KbDirectoryService kbDirectoryService;

	@Autowired
	
	SysDictService sysDictService;
	@ApiOperation("知识库列表")
	@ResponseBody
	@RequestMapping(value = "/getList", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true, dataType = "int"),
			@ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "int"),
			@ApiImplicitParam(name = "kbName", value = "标题", required = true, dataType = "String"),
			@ApiImplicitParam(name = "kbDirPk", value = "目录主键", required = true, dataType = "String") })
	public Result getList(int pageSize, int pageNum, String kbDirPk,String kbName) {
//		Wrapper<KbContent> wrapper = Condition.create();
//		wrapper.eq("kb_dir_pk", kbDirPk);
//		List<KbContent> list = kbContentService.selectList(wrapper);

	    List<KbContent> list = kbContentService.getList(kbDirPk,kbName);
		PageDTO<KbContent> pageDTO = null;
		int total = list.size();
		pageDTO = new PageTool<KbContent>().getPage(list, pageSize, pageNum);
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	@ApiOperation("知识库内容保存")
	@ResponseBody
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "kbDirCode", value = "目录编码", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbName", value = "知识库标题", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbTypePk", value = "知识库类型", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbLinkCode", value = "关联知识点", required = false, dataType = "String"),
		@ApiImplicitParam(name = "kbContent", value = "知识库内容", required = true, dataType = "String"),
		@ApiImplicitParam(name = "keyWord", value = "关键字", required = true, dataType = "String") })
	
	public Result save(KbContent kbContent) {
		KbDirectory directory = kbDirectoryService.getDirectoryByCodeAndFlag(kbContent.getKbDirCode());
		if (directory == null) {
			return Result.resultInfo(ResultCode.FAILURE, "知识点目录不能为空");
		}
		
		SysDicinfo sysDicinfo = sysDictService.selectById(kbContent.getKbTypePk());
		if(sysDicinfo!= null){
			kbContent.setKbTypeName(sysDicinfo.getDicName());
		}
		
		kbContent.setKbDirPk(directory.getId() + "");
		kbContent.setKbDirName(directory.getKbDirName());
		SysUser user = getUserInfo();
		kbContent.setCreateId(user.getId());
		kbContent.setCreateName(user.getName());
		kbContent.setCreateTime(DateUtil.toStr(new Date(),DateUtil.timeStampPattern));
		kbContent.setKbViewCount(0);
		kbContent.setApprovalStatus(0);
		kbContent.setKbCount(0);
		// 创建知识点编码
		String kbCode = "";
		kbCode = createKbCode();
		kbContent.setKbCode(kbCode);
		boolean result = kbContentService.insert(kbContent);
		if (result) {
			String operator = getUserInfo().getName();
			String info = "新增了" + kbContent.getKbName() + "知识库内容";
			saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "新增失败");
		}
	}

	/**
	 * 创建知识库内容编号
	 * 
	 * @return
	 */
	protected String createKbCode() {
		int countNum = kbContentService.getKbContentCountNum();
		String kbCode = String.format("%011d", countNum + 1);
		return kbCode;
	}
	@ApiOperation("修改")
	@ResponseBody
	@RequestMapping(value = "/doUpdate", method = { RequestMethod.POST })
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "dirPk", value = "id", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbDirCode", value = "目录编码", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbName", value = "知识库标题", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbTypePk", value = "知识库内容", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbLinkCode", value = "关联知识点", required = true, dataType = "String"),
		@ApiImplicitParam(name = "kbContent", value = "知识库内容", required = true, dataType = "String"),
		@ApiImplicitParam(name = "keyWord", value = "关键字", required = true, dataType = "String") })
	
	
	public Result doUpdate(KbContent kbContent) {
		if (kbContent == null) {
			return Result.resultInfo(ResultCode.FAILURE, "用户参数错误");
		} else {
			SysUser user = getUserInfo();
			KbDirectory directory = kbDirectoryService.getDirectoryByCodeAndFlag(kbContent.getKbDirCode());
			if (directory == null) {
				return Result.resultInfo(ResultCode.FAILURE, "知识点目录不能为空");
			}
			
			SysDicinfo sysDicinfo = sysDictService.selectById(kbContent.getKbTypePk());
			if(sysDicinfo!= null){
				kbContent.setKbTypeName(sysDicinfo.getDicName());
			}
			
			kbContent.setUpdateName(user.getName());
			kbContent.setUpdateUserId(user.getId());
			kbContent.setUpdateTime(DateUtil.toStr(new Date()));
			boolean iResult = kbContentService.updateById(kbContent);
			if (iResult) {
				String operator = getUserInfo().getName();
				String info = "修改了" + kbContent.getKbName() + "知识库内容";
				saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
				return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
			} else {
				return Result.resultInfo(ResultCode.FAILURE, "修改失败");
			}
		}
	}
	@ApiOperation("删除")
	@ResponseBody
	@RequestMapping(value = "/doDel", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "dirPk", value = "id", required = true, dataType = "String") })
	public Result doDel(String dirPk) {
		SysUser user = getUserInfo();
		String[] dirArray = dirPk.split(",");
		KbContent kbContent = kbContentService.selectById(dirPk);
		boolean result = false;
		for (String dirId : dirArray) {
			// KbContent kbContent = KbContent.dao.findById(dirId);
			// if(kbContent!=null){
			// if("".equals(kbNames)){
			// kbNames+=kbContent.getKbName();
			// }else{
			// kbNames+=","+kbContent.getKbName();
			// }
			// }
			result = kbContentService.deleteById(Integer.valueOf(dirId));
		}
		if (result) {
			String operator = getUserInfo().getName();
			String info = "删除了" + kbContent.getKbName() + "知识库内容";
			saveOperatorInfo(info, StatusEnum.LogInfoType13.getIntValue(), operator);
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
	}
	@ApiOperation("搜索")
	@ResponseBody
	@RequestMapping(value = "/doQuery", method = { RequestMethod.POST })
	@ApiImplicitParams({ 
	@ApiImplicitParam(name = "type", value = "1 标题 2  关键字 3  全文检索", required = true, dataType = "String"),
	@ApiImplicitParam(name = "word", value = "内容", required = true, dataType = "String"),
	@ApiImplicitParam(name = "pageSize", value = "每页条数", required = true, dataType = "Integer"),
	@ApiImplicitParam(name = "pageNum", value = "页数", required = true, dataType = "Integer"),
	@ApiImplicitParam(name = "kbDirPk", value = "目录主键", required = true, dataType = "String")
	
	})
	public Result doQuery(String type, String word,Integer pageSize, Integer pageNum, String kbDirPk) {
		// String type = getPara(0);
		// String word = getPara(1);
		// try {
		// if(StrKit.notBlank(word)){
		// word = URLDecoder.decode(word,"UTF-8");
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// if(StrKit.notBlank(word)){
		// KbSearchRecord searchRecord = new KbSearchRecord();
		// searchRecord.setKeyWord(word);
		// searchRecord.setUserPk(getCurrentAccoutId());
		// searchRecord.setCreateTime(DateKit.getNowTime());
		// searchRecord.save();
		// }
		//
		List<KbContent> contentList = kbContentService.getContentListByKeyWord(type, word,kbDirPk);

		String kbPk = "";
		for (KbContent content : contentList) {
			kbPk += content.getKbPk();
			kbContentService.updateQueryNumber(kbPk);
		}
		

		// String kbPk = "";
		// for(KbContent content : contentList){
		// kbPk +=content.getKbPk()+",";
		// }
		// if(StrKit.notBlank(kbPk)){
		// kbPk = kbPk.substring(0, kbPk.length()-1);
		// contentService.updateQueryNumber(kbPk);
		// }
		//
		// setAttr("contentList", contentList);
		// setAttr("query_type", type);
		// setAttr("key_word", word);
		// render("/web/kbcontent/kbcontent_query.jsp");
		
		PageDTO<KbContent> pageDTO = null;
		int total = contentList.size();
		pageDTO = new PageTool<KbContent>().getPage(contentList, pageSize, pageNum);
		return Result.resultInfo(ResultCode.SUCCESS, total, pageDTO.getData());
	}

	/**
	 * 查看知识库内容详情
	 */
	@ApiOperation("查看知识库内容详情")
	@ResponseBody
	@RequestMapping(value = "/showInfo", method = { RequestMethod.POST })
	@ApiImplicitParam(name = "kbPk", value = "id", required = true, dataType = "String")
	public Result showInfo(String kbPk) {
		KbContent kbContent = kbContentService.selectById(kbPk);
		kbContent.setKbViewCount(kbContent.getKbViewCount() + 1);
		kbContentService.updateById(kbContent);
		return Result.resultInfo(ResultCode.SUCCESS, kbContent);
	}
	/**
	 * 获取关联知识点信息
	 */
	@ApiOperation("关联知识点")
	@ResponseBody
	@RequestMapping(value = "/getLinkContent", method = { RequestMethod.POST })
	@ApiImplicitParam(name = "linkCode", value = "关联知识点", required = true, dataType = "String")
	public Result getLinkContent(String linkCode){
		if(!StrUtils.isEmpty(linkCode)) {
			List<KbContent> contentList = kbContentService.getLinkContentList(linkCode);
			return Result.resultInfo(ResultCode.SUCCESS, contentList);
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "参数错误");
		}
	}

	@RequestMapping(value = "/getKbContent",method = {RequestMethod.POST})
	@ResponseBody
	@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String")
	@ApiOperation("关联知识点")
	public Result getKbContent(@RequestParam(value = "id")String id){
		return Result.resultInfo(ResultCode.SUCCESS,kbContentService.selectById(id));
	}
}
