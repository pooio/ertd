package com.metaShare.modules.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.metaShare.modules.sys.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.StringUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.service.SysMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "api/sys/message")
@Api(tags = "系统消息")
public class SysMessageController extends BaseController {
	@Autowired
	protected SysMessageService messageService;

	/**
	 * 查询个人消息
	 * 
	 * @param
	 * @param
	 * @return
	 * @return PageDTO<Message>
	 * @author: zhaojie/zh_jie@163.com
	 * @date: 2016年1月27日 下午3:28:57
	 */
	@RequestMapping(value = "/personList" , method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "个人消息查询")
	public Result personList(int pageSize, int pageNum, HttpServletRequest req,int status,String subject,String content,String messageType) {
		List<SysMessage> listSysMessage = null ;
		Result result = null;
		int total=0;
		PageDTO<SysMessage> pageDTO = null;
		try {
			if(status == 2){
				listSysMessage = messageService.queryAllList(getUserId());
				total = listSysMessage.size();
				pageDTO = new PageTool<SysMessage>().getPage(listSysMessage, pageSize, pageNum);
				result = Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());

			}else{
				listSysMessage = messageService.queryDataListByConditions(status,subject,content,getUserId(),messageType);
				total = listSysMessage.size();
				pageDTO = new PageTool<SysMessage>().getPage(listSysMessage, pageSize, pageNum);
				result = Result.resultInfo(ResultCode.SUCCESS,total,pageDTO.getData());
			}
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			result = Result.resultInfo(ResultCode.SESSION_EXPIRED,"session过期");
			e.printStackTrace();
		}catch (Exception e) {
			result = Result.resultInfo(ResultCode.FAILURE,null);
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 签收消息
	 * 
	 * @param id
	 * @param
	 * @return
	 * @return Map<String,Object>
	 * @author: zhaojie/zh_jie@163.com
	 * @version: 2016年2月15日 上午9:23:53
	 */
	@ResponseBody
	@RequestMapping(value = "sign", method= RequestMethod.POST)
	@ApiOperation(value = "消息签收")
	public Map<String, Object> saveUser(@RequestParam(value = "id") String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		messageService.updateStatus(id, 1L);
		map.put("msg", "签收成功");
		return map;
	}

	// 保存对象
	@RequestMapping(value = "/save", method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "消息保存")
	public Result save(SysMessage sysMessage) {
		messageService.insert(sysMessage);
		return Result.resultInfo(ResultCode.SUCCESS, "成功");
	}

//	// 修改对象
//	@RequestMapping(value = "/update")
//	@ResponseBody
//	public Result update(SysMessage sysMessage) {
//		messageService.updateAllColumnById(sysMessage);
//		return Result.resultInfo(ResultCode.SUCCESS, "成功");
//	}

	// 删除对象
	@RequestMapping(value = "/deleted", method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "消息删除")
	@ApiImplicitParams({
    @ApiImplicitParam(name = "id", value = "消息ID", required = true, dataType = "String") })
	public Result deleted(String id) {
		if(StringUtils.isEmpty(id)){
			SysUser sysUser = getUserInfo();
			messageService.deleteAllMessageByUserId(sysUser.getId());
		}else{
			SysMessage sysMessage = messageService.selectById(id);
			sysMessage.setDeleted(true);
			messageService.updateAllColumnById(sysMessage);
		}
		return Result.resultInfo(ResultCode.SUCCESS, "成功");
	}
//
	/**
	 * 返回单个实体的json数据
	 */
	@ResponseBody
	@RequestMapping(value = "getInfo")
	public Result getInfo(@RequestParam(value = "id") String id) {
		return Result.resultInfo(ResultCode.SUCCESS, messageService.selectById(id));
	}
//
//	/**
//	 * 分页查询
//	 * @param status 状态
//	 * @param subject 主题
//	 * @param content 内容
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value = "list")
//	public Result listData(int pageSize, int pageNumber,String status,String subject,String content) {
//		PageDTO<SysMessage> pageDTO = null;
//		int statu;
//		try {
//			if (StringUtils.isNotEmpty(status)) {
//				statu = Integer.parseInt(status);
//			}else{
//				statu = 100;
//			}
//			List<SysMessage> list = messageService.queryDataListByConditions(statu,subject,content,null);;
//			pageDTO = new PageTool<SysMessage>().getPage(list, pageSize, pageNumber);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return Result.resultInfo(ResultCode.SUCCESS, pageDTO);
//}

	@RequestMapping(value = "/getMessageNumber", method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "消息数量")
	public Result getMessageNumber(){
		int readed = messageService.getReadedNumber(getUserId());

		int unreaded = messageService.getUnReaderNumber(getUserId());

		Map<String,Integer> map = new HashMap<>();
		map.put("messageNum", readed);
		map.put("unreadedNum", unreaded);

		return Result.resultInfo(ResultCode.SUCCESS,map);
	}

	@RequestMapping(value = "/readMessage", method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "消息读取")
	@ApiImplicitParams({
    @ApiImplicitParam(name = "id", value = "消息ID,id为空代表全部读取", required = true, dataType = "int") })
	public Result readMessage(String id){

		if(StringUtils.isEmpty(id)){
			messageService.updateMessageStatus(getUserId());

		}else{
			messageService.updateMessageStatusOnId(getUserId(),id);
		}
		return Result.resultInfo(ResultCode.SUCCESS,"成功");
	}


	//清空消息列表
	@RequestMapping(value = "/emptyMessage", method= RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "消息清空")
	public Result emptyMessage(){

		messageService.emptyMessage(getUserId());

		return Result.resultInfo(ResultCode.SUCCESS,"成功");
	}

//	@RequestMapping(value = "/externalGetMessageList", method= RequestMethod.POST)
//	@ResponseBody
//	public Result externalGetMessageList(@RequestParam(value = "userId") String userId,
//										 @RequestParam(value = "loginName") String loginName){
//
//		if(StringUtils.isEmpty(userId) && StringUtils.isEmpty(loginName)){
//			return Result.resultInfo(ResultCode.FAILURE,"用户为空，查询失败");
//		}
//		List<SysMessage> dataList = messageService.getMessageList(userId,loginName);
//
//		return Result.resultInfo(ResultCode.SUCCESS,dataList.size(),dataList);
//	}

}
