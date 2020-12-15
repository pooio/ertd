/**
 * 
 */
package com.metaShare.modules.sys.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.metaShare.common.utils.StrUtils;
import com.metaShare.common.utils.StringUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.ShiroUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysCalendar;
import com.metaShare.modules.sys.entity.SysEmail;
import com.metaShare.modules.sys.entity.SysMessage;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysCalendarService;
import com.metaShare.modules.sys.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author pc
 *
 */
@Controller
@CrossOrigin
@Api(tags = "日历管理")
@RequestMapping(value = "/api/sys/calendar")
public class SysCalendarController extends BaseController {
	@Autowired
	private SysCalendarService sysCalendarService;

	@ResponseBody
	@ApiOperation(value = "日历数据")
	@RequestMapping(value = "/list", method = { RequestMethod.POST })
	public Result getList() {
		String userId = getUserId();
//		Session session = ShiroUtils.getSession();
//		Object object = session.getAttribute(userId);
//		Object object1 = session.getAttribute("1333e0663a3f40ac8ec69bb40151174a");
		String ddd =DateUtil.getDate(DateUtil.timeDatePattern);
		String year = DateUtil.getDate("yyyy");
		List<Map> list = sysCalendarService.getList(userId,year);
		List<Map<String, Object>> list1 =  new ArrayList<Map<String, Object>>();
		for (Map map : list) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("months",map.get("months").toString().split(",") );
			map1.put("days",map.get("days").toString().split(",") );
			map1.put("timetitle",map.get("time")+":"+map.get("title"));
			map1.put("title",map.get("title"));
			map1.put("content",map.get("content"));
			if(String.valueOf(map.get("level")).equals("109")){
				map1.put("dicName","低");
			}else if(String.valueOf(map.get("level")).equals("110")){
				map1.put("dicName","中");
			}else if(String.valueOf(map.get("level")).equals("111")){
				map1.put("dicName","高");
			}
			map1.put("level",map.get("level"));
			map1.put("id",map.get("id"));
			map1.put("date", map.get("dateTime"));
			list1.add(map1);
		}
		return Result.resultInfo(ResultCode.SUCCESS, list1);
	}

	@ResponseBody
	@ApiOperation(value = "日历数据")
	@RequestMapping(value = "/datalist", method = { RequestMethod.POST })
	public Result datalist(String data) {
		String userId = getUserId();
		if(StrUtils.isEmpty(data)){
			data = DateUtil.getDate(DateUtil.datePattern);
		}
		List<Map> list = sysCalendarService.getList(userId,data);
		List<Map<String, Object>> list1 =  new ArrayList<Map<String, Object>>();
		for (Map map : list) {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("months",map.get("months").toString().split(",") );
			map1.put("days",map.get("days").toString().split(",") );
			map1.put("timetitle",map.get("time")+":"+map.get("title"));
			map1.put("title",map.get("title"));
			map1.put("content",map.get("content"));
			if(String.valueOf(map.get("level")).equals("109")){
				map1.put("dicName","低");
			}else if(String.valueOf(map.get("level")).equals("110")){
				map1.put("dicName","中");
			}else if(String.valueOf(map.get("level")).equals("111")){
				map1.put("dicName","高");
			}
			map1.put("level",map.get("level"));
			map1.put("id",map.get("id"));
			map1.put("date", map.get("dateTime"));
			list1.add(map1);
		}
		return Result.resultInfo(ResultCode.SUCCESS, list1);
	}

	@ResponseBody
	@ApiOperation(value = "新增")
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "date", value = "日期", required = true, dataType = "String"),
		@ApiImplicitParam(name = "title", value = "标题", required = true, dataType = "String"),
		@ApiImplicitParam(name = "level", value = "级别 ： 1 低 2 中  3 高", required = true, dataType = "int"),
		@ApiImplicitParam(name = "content", value = "内容", required = true, dataType = "String") })
	public Result save(SysCalendar sysCalendar) {
		sysCalendar.setCreateTime(DateUtil.getDate(DateUtil.timeDatePattern));
		sysCalendar.setUserId(getUserId());
		boolean result = sysCalendarService.insert(sysCalendar);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "新增失败");
		}
		
	}

	@ResponseBody
	@ApiOperation(value = "获取详情")
	@RequestMapping(value = "/getInfo", method = { RequestMethod.POST })
	@ApiImplicitParams(@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"))
	public Result getInfo(SysCalendar sysCalendar) {
		sysCalendar = sysCalendarService.selectById(sysCalendar.getId());
		return Result.resultInfo(ResultCode.SUCCESS, sysCalendar);
	}


	@ResponseBody
	@ApiOperation(value = "修改")
	@RequestMapping(value = "/updata", method = { RequestMethod.POST })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "String"),
		@ApiImplicitParam(name = "date", value = "日期", required = true, dataType = "String"),
		@ApiImplicitParam(name = "title", value = "标题", required = true, dataType = "String"),
		@ApiImplicitParam(name = "level", value = "级别 ： 1 低 2 中  3 高", required = true, dataType = "int"),
		@ApiImplicitParam(name = "content", value = "内容", required = true, dataType = "String") })
	public Result updata(SysCalendar sysCalendar) {
		sysCalendar.setCreateTime(DateUtil.getDate(DateUtil.timeDatePattern));
		sysCalendar.setUserId(getUserId());
		if(StringUtils.isEmpty(sysCalendar.getContent())){
			sysCalendar.setContent("");
		}
		boolean result = sysCalendarService.updateById(sysCalendar);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, "修改成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		}
		
	}
	
	@ResponseBody
	@ApiOperation(value = "删除")
	@RequestMapping(value = "/del", method = { RequestMethod.POST })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "数据主键", required = true, dataType = "String") })
	public Result del(String id) {
		
		SysCalendar sysCalendar = sysCalendarService.selectById(id);
		if(sysCalendar!= null){
			sysCalendar.setDeleted(true);
		}
		boolean result = sysCalendarService.updateById(sysCalendar);
		if(result){
			return Result.resultInfo(ResultCode.SUCCESS, "删除成功");
		}else{
			return Result.resultInfo(ResultCode.FAILURE, "删除失败");
		}
		
	}
	
	@Autowired
	private SysEmailController sysEmailController;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysMessageController sysMessageController;
	public void SendCalendarInfo(){
		//获取到日历信息
		String date= DateUtil.getDate(DateUtil.datePattern);
		Wrapper wrapper = Condition.create().like("date", date);
		List<SysCalendar>  list = sysCalendarService.selectList(wrapper);
		for (SysCalendar sysCalendar : list) {
			//根据日志创建人获取用户信息
//			//发送邮件
			SysUser sysuser= sysUserService.selectById(sysCalendar.getUserId());
			if(sysuser!= null){
				SysEmail sysEmain = new SysEmail();
				sysEmain.setReceiver(sysuser.getEmail());
				sysEmain.setState(0);
				sysEmain.setSubject(sysCalendar.getTitle());
				sysEmain.setText(sysCalendar.getContent());
				sysEmain.setType(1);
				sysEmain.setCreateTime(DateUtil.toStr(new Date()));
				sysEmailController.sendMail(sysEmain,"系统用户");
			}
			//消息推送
			SysMessage sysMessage = new SysMessage();
			sysMessage.setSubject(sysCalendar.getTitle());
			sysMessage.setContent(sysCalendar.getContent());
			sysMessage.setReceiver(sysCalendar.getUserId());
			sysMessage.setSender("1");
			sysMessage.setCreateTime(new Date());
			sysMessage.setMessageType(1);
			sysMessageController.save(sysMessage);
		}
	}
	
}
