/**  
 * @Title: BulletinInfoController.java
 * @Description: TODO(描述)
 * @author eric.xi
 * @date 2020-02-11 12:57:03 
 */
package com.metaShare.modules.bulletin.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.metaShare.modules.sys.entity.SysDicinfo;
import com.metaShare.modules.sys.service.SDicinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.common.tool.pageTool.PageDTO;
import com.metaShare.common.tool.pageTool.PageTool;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.bulletin.entity.BulletinInfo;
import com.metaShare.modules.bulletin.entity.BulletinReadItem;
import com.metaShare.modules.bulletin.entity.BulletinSend;
import com.metaShare.modules.bulletin.service.BulletinInfoService;
import com.metaShare.modules.bulletin.service.BulletinReadItemService;
import com.metaShare.modules.bulletin.service.BulletinSendService;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.entity.SysOrganization;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysOrganizationService;
import com.metaShare.modules.sys.service.SysUserService;
import com.metaShare.modules.sys.service.SysconfigService;
import com.sun.javafx.collections.MappingChange.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: BulletinInfoController
 * @Description: 公告管理
 * @author eric.xi
 * @date 2020-02-11 12:57:03
 */
@Controller
@CrossOrigin
@Api(tags = "公告管理")
@RequestMapping(value = "/api/bulletinInfo")
public class BulletinInfoController extends BaseController {
	@Autowired
	BulletinInfoService bulletinInfoService;
	@Autowired
	SysUserService sysUserService;

	@Autowired
	SysOrganizationService sysOrganizationService;

	@Autowired
	BulletinSendService bulletinSendService;
	@Autowired
	BulletinReadItemService bulletinReadItemService;
	@Autowired
	SysFileAttachService sysFileAttachService;
	@Autowired
	SysconfigService sysconfigService;
	@Autowired
	private SDicinfoService sDicinfoService;
	// @Autowired
	// private SysUserController sysUserController;
	@ResponseBody
	@ApiOperation(value = "公告列表数据")
	@RequestMapping(value = "/getList", method = { RequestMethod.POST })
	public Result getList(int pageSize, int pageNum, String bulletinTitle) {
		List<BulletinInfo> list = bulletinInfoService.getList(bulletinTitle);
		int totel = list.size();
		PageDTO<BulletinInfo> pageDTO = null;
		pageDTO = new PageTool<BulletinInfo>().getPage(list, pageSize, pageNum);
		SysConfig sysConfig6 = sysconfigService.getConfigData(StatusEnum.SysConfigType6.getStrValue());

		for (BulletinInfo bulletinInfo : pageDTO.getData()) {
			List<String> listreceive = new ArrayList<String>();
			String[] receive = bulletinInfo.getReceiveInfo().split(",");
			listreceive = Arrays.asList(receive);
			bulletinInfo.setReceiveInfos(listreceive);
			SysFileAttach sysFileAttach = sysFileAttachService.selectById(bulletinInfo.getFileId());
			if (sysFileAttach != null) {
				String Urlpath = "";
				if (sysConfig6 != null) {
					Urlpath = sysConfig6.getConfigContent();
				}
				bulletinInfo.setUrl(Urlpath + sysFileAttach.getFilePath() + sysFileAttach.getFileName());
				bulletinInfo.setFileName(sysFileAttach.getOldFileName());
			}

		}
		return Result.resultInfo(ResultCode.SUCCESS, totel, pageDTO.getData());
	}

	@ResponseBody
	@ApiOperation(value = "个人公告")
	@RequestMapping(value = "/getUserBulletinInfoList", method = { RequestMethod.POST })
	public Result getUserBulletinInfoList(int pageSize, int pageNum, String bulletinTitle) {
		String userId = getUserId();
		//String userId = "1911779690d943d1989d99998a24e016";
		String closeDate = DateUtil.getDate(DateUtil.datePattern);
		List<BulletinInfo> list = bulletinInfoService.getUserBulletinInfoList(userId, bulletinTitle, closeDate);
		int totel = list.size();
		PageDTO<BulletinInfo> pageDTO = null;
		pageDTO = new PageTool<BulletinInfo>().getPage(list, pageSize, pageNum);
		SysConfig sysConfig5 = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());

		for (BulletinInfo bulletinInfo : pageDTO.getData()) {
			List<String> listreceive = new ArrayList<String>();
			String[] receive = bulletinInfo.getReceiveInfo().split(",");
			listreceive = Arrays.asList(receive);
			bulletinInfo.setReceiveInfos(listreceive);
			SysFileAttach sysFileAttach = sysFileAttachService.selectById(bulletinInfo.getFileId());
			if (sysFileAttach != null) {
				String Urlpath = "";
				if (sysConfig5 != null) {
					Urlpath = sysConfig5.getConfigContent();
				}
				bulletinInfo.setUrl(Urlpath + sysFileAttach.getFilePath() + sysFileAttach.getFileName());
				bulletinInfo.setFileName(sysFileAttach.getOldFileName());
			}

		}
		return Result.resultInfo(ResultCode.SUCCESS, totel, pageDTO.getData());
	}



	@ResponseBody
	@ApiOperation(value = "编辑详情")
	@RequestMapping(value = "/getBulletinInfo", method = { RequestMethod.POST })
	public Result getBulletinInfo(String bulletinPk) {
		String closeDate = DateUtil.getDate(DateUtil.datePattern);
		BulletinInfo bulletinInfo = bulletinInfoService.selectById(bulletinPk);
		SysConfig sysConfig5 = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());
		if(bulletinInfo!= null){
			List<String> listreceive = new ArrayList<String>();
			String[] receive = bulletinInfo.getReceiveInfo().split(",");
			for (String s : receive) {
				Wrapper wrapper = Condition.create().eq("deleted", false);
				wrapper.eq("STATUS",1);
				wrapper.eq("ID",s);
				List<SysUser> userList = sysUserService.selectList(wrapper);
				if(userList.size()>0){
					listreceive.add(s);
				}
			}
				SysDicinfo sysDict = sDicinfoService.selectById(bulletinInfo.getBulletinType());
                if(sysDict!= null){
                  if(sysDict.getDeleted()||sysDict.getInUse()==0){
					  bulletinInfo.setBulletinType(0);
				  }
				}


			bulletinInfo.setReceiveInfos(listreceive);
			SysFileAttach sysFileAttach = sysFileAttachService.selectById(bulletinInfo.getFileId());
			if (sysFileAttach != null) {
				String Urlpath = "";
				if (sysConfig5 != null) {
					Urlpath = sysConfig5.getConfigContent();
				}
				bulletinInfo.setUrl(Urlpath + sysFileAttach.getFilePath() + sysFileAttach.getFileName());
				bulletinInfo.setFileName(sysFileAttach.getOldFileName());
			}
			
			//listreceive = Arrays.asList(receive);
		}
//		for (BulletinInfo bulletinInfo : pageDTO.getData()) {
//			List<String> listreceive = new ArrayList<String>();
//			String[] receive = bulletinInfo.getReceiveInfo().split(",");
//			listreceive = Arrays.asList(receive);
//			bulletinInfo.setReceiveInfos(listreceive);
//			SysFileAttach sysFileAttach = sysFileAttachService.selectById(bulletinInfo.getFileId());
//			if (sysFileAttach != null) {
//				String Urlpath = "";
//				if (sysConfig5 != null) {
//					Urlpath = sysConfig5.getConfigContent();
//				}
//				bulletinInfo.setUrl(Urlpath + sysFileAttach.getFilePath() + sysFileAttach.getFileName());
//				bulletinInfo.setFileName(sysFileAttach.getOldFileName());
//			}
//
//		}
		return Result.resultInfo(ResultCode.SUCCESS, bulletinInfo);
	}



	@ResponseBody
	@ApiOperation(value = "公告保存")
	@RequestMapping(value = "/save", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "bulletinTitle", value = "标题", required = true, dataType = "String"),
			@ApiImplicitParam(name = "bulletinNote", value = "内容", required = true, dataType = "String"),
			@ApiImplicitParam(name = "bulletinType", value = "公告类型", required = true, dataType = "int"),
			@ApiImplicitParam(name = "closeDate", value = "截至日期", required = true, dataType = "int"),
			@ApiImplicitParam(name = "fileId", value = "附件ID", required = false, dataType = "int"),
			@ApiImplicitParam(name = "bulletinMode", value = "公告方式(0:全体,1:部门,2:个人)", required = true, dataType = "int"),
			@ApiImplicitParam(name = "receiveInfo", value = "接收对象（多个对象用\",\"隔开）", required = true, dataType = "int"), })
	public Result save(BulletinInfo bulletinInfo) {
		boolean result = false;
		HashMap<String, String> map = new HashMap<String, String>();
		SysUser user = getUserInfo();
		bulletinInfo.setCreateTime(DateUtil.toStr(new Date(), DateUtil.timeStampPattern));
		bulletinInfo.setUserPk(user.getId());
		bulletinInfo.setCreateName(user.getName());
		bulletinInfo.setReadNo(0);
		bulletinInfo.setState(1);
		String orgId = bulletinInfo.getReceiveInfo();// 接收对象
		int bulletinMode = bulletinInfo.getBulletinMode();// 公告方式
		if (bulletinMode == 0) {// 0:全体 1：部门 2：个人
			long userCount = sysUserService.getCount();
			bulletinInfo.setReceiveNo(Integer.parseInt(String.valueOf(userCount)));
			result = bulletinInfoService.insert(bulletinInfo);
			if (result) {
				result = this.saveBulletinSend("-1", bulletinMode, bulletinInfo);
			}
		} else if (bulletinMode == 1) {

			String[] orgArr = orgId.split(",");
			for (String string : orgArr) {
				// 判断节点是否有子节点
				Wrapper<SysOrganization> wrapper = Condition.create().eq("parent_id", string);
				List<SysOrganization> listorg = sysOrganizationService.selectList(wrapper);
				map.put(string, string);
				if (listorg.size() > 0) {
					for (SysOrganization sysOrganization : listorg) {
						map.put(sysOrganization.getId(), sysOrganization.getId());
					}
				}
			}
			bulletinInfo.setReceiveNo(map.size());
			result = bulletinInfoService.insert(bulletinInfo);
			int receiveNo = 0;// 接收人数
			for (String key : map.keySet()) {
				List<SysUser> userList = sysUserService.getUserByOrgId(key);
				receiveNo += userList.size();
				for (int j = 0; j < userList.size(); j++) {
					SysUser sysUser = userList.get(j);
					result = this.saveBulletinSend(sysUser.getId(), bulletinMode, bulletinInfo);
				}
			}
			bulletinInfo.setReceiveNo(receiveNo);
			result = bulletinInfoService.updateById(bulletinInfo);
		} else {
			String[] orgArr = orgId.split(",");
			bulletinInfo.setReceiveNo(orgArr.length);
			result = bulletinInfoService.insert(bulletinInfo);
			for (int i = 0; i < orgArr.length; i++) {
				SysUser sysuser = sysUserService.selectById(orgArr[i]);
				if (sysuser != null) {
					result = this.saveBulletinSend(sysuser.getId(), bulletinMode, bulletinInfo);
				}
			}
			
		}
		
		String operator = getUserInfo().getName();
		String info = "新增了" + bulletinInfo.getBulletinTitle()+ "公告";
		saveOperatorInfo(info, StatusEnum.LogInfoType9.getIntValue(), operator);
		
		return Result.resultInfo(ResultCode.SUCCESS, "新增成功");
	}

	/**
	 * 
	 * @Title: saveBulletinSend
	 * @Description: TODO(描述)
	 * @param user_pk
	 * @param bulletinMode
	 * @param bulletinInfo
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:48:31
	 */
	public boolean saveBulletinSend(String user_pk, Object bulletinMode, BulletinInfo bulletinInfo) {
		BulletinSend bulletinSend = new BulletinSend();
		bulletinSend.setUserPk(user_pk);
		bulletinSend.setCreateTime(DateUtil.getDate(DateUtil.timeDatePattern));
		bulletinSend.setBulletinIsread(0);
		bulletinSend.setBulletinType(Integer.parseInt(String.valueOf(bulletinMode)));
		if (bulletinInfo != null) {
			bulletinSend.setBulletinPk(bulletinInfo.getBulletinPk());
			bulletinSend.setBulletinTitle(bulletinInfo.getBulletinTitle());
			bulletinSend.setState(bulletinInfo.getState());
			bulletinSend.setCreatePk(bulletinInfo.getUserPk());
			bulletinSend.setCreateName(bulletinInfo.getCreateName());
		}
		boolean result = bulletinSendService.insert(bulletinSend);
		return result;
	}

	//
	public boolean delBulletinSend(int bulletinPk) {
		Wrapper<BulletinSend> wrapper = Condition.create().eq("bulletin_pk", bulletinPk);
		return bulletinSendService.delete(wrapper);
	}

	/**
	 * 
	 * @Title: doUpdate
	 * @Description: TODO(描述)
	 * @param bulletinInfo
	 * @param userIds
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:48:25
	 */
	@ResponseBody
	@ApiOperation(value = "公告修改")
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public Result doUpdate(BulletinInfo bulletinInfo, String userIds) {
		SysUser user = getUserInfo();
		boolean result = false;

		if (bulletinInfo == null) {
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		} else {
			bulletinInfo.setUserPk(user.getId());
			bulletinInfo.setCreateName(user.getName());
			bulletinInfo.setReadNo(0);
			bulletinInfo.setState(1);

			delBulletinSend(bulletinInfo.getBulletinPk());
			String orgId = bulletinInfo.getReceiveInfo();// 接收对象
			int bulletinMode = bulletinInfo.getBulletinMode();// 公告方式
			if (bulletinMode == 0) {// 0:全体 1：部门 2：个人
				long userCount = sysUserService.getCount();
				bulletinInfo.setReceiveNo(Integer.parseInt(String.valueOf(userCount)));
				result = bulletinInfoService.updateById(bulletinInfo);
				if (result) {
					result = this.saveBulletinSend("-1", bulletinMode, bulletinInfo);
				}
			} else if (bulletinMode == 1) {
				String[] orgArr = orgId.split(",");
				bulletinInfo.setReceiveNo(orgArr.length);
				int receiveNo = 0;// 接收人数
				for (int i = 0; i < orgArr.length; i++) {
					List<SysUser> userList = sysUserService.getUserByOrgId(orgId);
					receiveNo += userList.size();
					for (int j = 0; j < userList.size(); j++) {
						SysUser sysUser = userList.get(j);
						result = this.saveBulletinSend(sysUser.getId(), bulletinMode, bulletinInfo);
					}
				}
				bulletinInfo.setReceiveNo(receiveNo);
				result = bulletinInfoService.updateById(bulletinInfo);
			} else {
				String[] orgArr = orgId.split(",");
				bulletinInfo.setReceiveNo(orgArr.length);
				result = bulletinInfoService.updateById(bulletinInfo);
				for (int i = 0; i < orgArr.length; i++) {
					String[] idArr = orgArr[i].split("-");
					if (idArr.length > 1) {
						SysUser sysuser = sysUserService.selectById(idArr[0]);
						if (user != null) {
							if (result) {
								result = this.saveBulletinSend(sysuser.getId(), bulletinMode, bulletinInfo);
							}
						}
					}
				}
			}
			String operator = getUserInfo().getName();
			String info = "编辑了" + bulletinInfo.getBulletinTitle()+ "公告";
			saveOperatorInfo(info, StatusEnum.LogInfoType9.getIntValue(), operator);
			if (result) {
				return Result.resultInfo(ResultCode.SUCCESS, "公告信息修改成功");
			} else {
				Result.resultInfo(ResultCode.FAILURE, "修改失败");
			}
			
			
			return Result.resultInfo(ResultCode.FAILURE, "修改失败");
		}
	}

	/**
	 * 
	 * @Title: doDel
	 * @Description: TODO(描述)
	 * @param bulletinIds
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:48:19
	 */
	@ResponseBody
	@ApiOperation(value = "公告删除")
	@RequestMapping(value = "/doDel", method = { RequestMethod.POST })
	public Result doDel(String bulletinIds) {
		String[] infoIdArray = bulletinIds.split(",");
		Result result = null;
		String bulletinTitles = "";
		for (String infoId : infoIdArray) {
			BulletinInfo bulletinInfo = bulletinInfoService.selectById(infoId);
			// BulletinInfo bulletinInfo =
			// bulletinInfoService.selectOne(wrapper);
			if (bulletinInfo != null) {
				if ("".equals(bulletinTitles)) {
					bulletinTitles += bulletinInfo.getBulletinTitle();
				} else {
					bulletinTitles += "," + bulletinInfo.getBulletinTitle();
				}
				bulletinInfoService.deleteById(Integer.valueOf(infoId));
				Wrapper<BulletinSend> wrapper1 = Condition.create().eq("BULLETIN_PK", infoId);
				bulletinSendService.delete(wrapper1);
				result = Result.resultInfo(ResultCode.SUCCESS, "删除成功");
			}
		}
		String operator = getUserInfo().getName();
		String info = "删除了" + bulletinTitles+ "公告";
		saveOperatorInfo(info, StatusEnum.LogInfoType9.getIntValue(), operator);
		return result;
	}

	/**
	 * 
	 * @Title: doInUse
	 * @Description: TODO(描述)
	 * @param bulletinId
	 * @param inUse
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:48:10
	 */
	@ApiOperation(value = "禁用/启用")
	@ResponseBody
	@RequestMapping(value = "/setInUse", method = { RequestMethod.POST })
	public Result doInUse(String bulletinId, int inUse) {
		String[] infoIdArray = bulletinId.split(",");
		Result result = null;
		for (String infoId : infoIdArray) {
			// Wrapper<BulletinInfo> wrapper =
			// Condition.create().eq("bulletin_pk", infoId);
			BulletinInfo bulletinInfo = bulletinInfoService.selectById(infoId);
			if (!"".equals(inUse)) {
				bulletinInfo.setState(inUse);
				// boolean iResult = bulletinInfoService.update(bulletinInfo,
				// wrapper);
				boolean iResult = bulletinInfoService.updateById(bulletinInfo);
				if (iResult) {
					Wrapper<BulletinSend> wrapper1 = Condition.create().eq("BULLETIN_PK", bulletinInfo.getBulletinPk());
					List<BulletinSend> BulletinSendList = bulletinSendService.selectList(wrapper1);
					for (BulletinSend bulletinSend : BulletinSendList) {
						bulletinSend.setState(inUse);
						// iResult = bulletinSendService.update(bulletinSend,
						// wrapper1);
						iResult = bulletinSendService.updateById(bulletinSend);
					}
				}
				String info = "启用";
				if(inUse == 0){
					info = "禁用";
				}
				if (iResult) {
					result = Result.resultInfo(ResultCode.SUCCESS, info + "成功");
				} else {
					result = Result.resultInfo(ResultCode.FAILURE, info + "失败");
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @Title: toPublish
	 * @Description: 发布
	 * @param bulletinId
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:47:46
	 */
	@ApiOperation(value = "发布")
	@ResponseBody
	@RequestMapping(value = "/toPublish", method = { RequestMethod.POST })
	public Result toPublish(String bulletinId) {
		// Wrapper<BulletinInfo> wrapper = Condition.create().eq("bulletin_pk",
		// bulletinId);
		BulletinInfo bulletinInfo = bulletinInfoService.selectById(bulletinId);
		boolean result = false;
		if (bulletinInfo != null) {
			bulletinInfo.setPublishState(1);
			bulletinInfo.setCreateTime(DateUtil.getCurrentTime());
			result = bulletinInfoService.updateById(bulletinInfo);
		}

		String operator = getUserInfo().getName();
		String info = "发布了" + bulletinInfo.getBulletinTitle()+ "公告";
		saveOperatorInfo(info, StatusEnum.LogInfoType9.getIntValue(), operator);
		if (result) {
			return Result.resultInfo(ResultCode.SUCCESS, "发布成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "发布失败");
		}
	}


	/**
	 *
	 * @Title: toUnPublish
	 * @Description: 发布
	 * @param bulletinId
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:47:46
	 */
	@ApiOperation(value = "取消发布")
	@ResponseBody
	@RequestMapping(value = "/toUnPublish", method = { RequestMethod.POST })
	public Result toUnPublish(String bulletinId) {
		// Wrapper<BulletinInfo> wrapper = Condition.create().eq("bulletin_pk",
		// bulletinId);
		BulletinInfo bulletinInfo = bulletinInfoService.selectById(bulletinId);
		boolean result = false;
		if (bulletinInfo != null) {
			bulletinInfo.setPublishState(0);
			result = bulletinInfoService.updateById(bulletinInfo);
		}
		String operator = getUserInfo().getName();
		String info = "取消发布了" + bulletinInfo.getBulletinTitle()+ "公告";
		saveOperatorInfo(info, StatusEnum.LogInfoType9.getIntValue(), operator);
		if (result) {

			//将该公告之前的已读消息人员列表清空
			bulletinReadItemService.deleteBybulletinPk(bulletinId);
			bulletinInfo.setReadNo(0);
			bulletinInfo.setState(1);
			SysUser user = getUserInfo();
			String orgId = bulletinInfo.getReceiveInfo();
			int bulletinMode = bulletinInfo.getBulletinMode();// 公告方式
			if (bulletinMode == 0) {// 0:全体 1：部门 2：个人
				long userCount = sysUserService.getCount();
				bulletinInfo.setReceiveNo(Integer.parseInt(String.valueOf(userCount)));
				bulletinInfoService.updateById(bulletinInfo);
				/*if (result) {
					result = this.saveBulletinSend("-1", bulletinMode, bulletinInfo);
				}*/
			} else if (bulletinMode == 1) {
				String[] orgArr = orgId.split(",");
				bulletinInfo.setReceiveNo(orgArr.length);
				int receiveNo = 0;// 接收人数
				for (int i = 0; i < orgArr.length; i++) {
					List<SysUser> userList = sysUserService.getUserByOrgId(orgId);
					receiveNo += userList.size();
					/*for (int j = 0; j < userList.size(); j++) {
						SysUser sysUser = userList.get(j);
						result = this.saveBulletinSend(sysUser.getId(), bulletinMode, bulletinInfo);
					}*/
				}
				bulletinInfo.setReceiveNo(receiveNo);
				bulletinInfoService.updateById(bulletinInfo);
			} else {
				String[] orgArr = orgId.split(",");
				bulletinInfo.setReceiveNo(orgArr.length);
				bulletinInfoService.updateById(bulletinInfo);
				/*for (int i = 0; i < orgArr.length; i++) {
					String[] idArr = orgArr[i].split("-");
					if (idArr.length > 1) {
						SysUser sysuser = sysUserService.selectById(idArr[0]);
						if (user != null) {
							if (result) {
								result = this.saveBulletinSend(sysuser.getId(), bulletinMode, bulletinInfo);
							}
						}
					}
				}*/
			}

			return Result.resultInfo(ResultCode.SUCCESS, "取消发布成功");
		} else {
			return Result.resultInfo(ResultCode.FAILURE, "取消发布失败");
		}
	}

	/**
	 * 
	 * @Title: checkBulletinTitleIsUnique
	 * @Description: TODO(描述)
	 * @param bulletinId
	 * @param bulletinTitle
	 * @return
	 * @author eric.xi
	 * @date 2020-02-12 10:47:19
	 */
	@ApiOperation(value = "标题重复验证")
	@ResponseBody
	@RequestMapping(value = "/checkBulletinTitle", method = { RequestMethod.POST })
	public Result checkBulletinTitleIsUnique(String bulletinId, String bulletinTitle) {
		Wrapper<BulletinInfo> wrapper = Condition.create().eq("bulletin_title", bulletinTitle);
		if (!StrUtils.isEmpty(bulletinId)) {
			wrapper.ne("bulletin_pk", bulletinId);
		}
		List<BulletinInfo> BulletinInfoList = bulletinInfoService.selectList(wrapper);
		if (BulletinInfoList.size() > 0) {
			return Result.resultInfo(ResultCode.FAILURE, "标题已经存在");
		}
		return Result.resultInfo(ResultCode.SUCCESS, "");
	}

	@ApiOperation(value = "已读/未读列表")
	@ResponseBody
	@RequestMapping(value = "/getSendList", method = { RequestMethod.POST })
	@ApiImplicitParams({ @ApiImplicitParam(name = "bulletinPk", value = "公告主键", required = true, dataType = "String"),
			@ApiImplicitParam(name = "isRead", value = "R:已读 ， U:未读", required = true, dataType = "String"), })
	public Result getSendList(String bulletinPk, String isRead) {
		List<Map> list;
		int read = 0;
		if ("R".equals(isRead)) {
			read = 1;
			list = bulletinInfoService.getBulletinReadItemList(bulletinPk);
		} else {
			// //判断公告类型是否是全体
			List<BulletinSend> bulletinSends = bulletinInfoService.getBuletinSendByBulletinId(bulletinPk);
			BulletinSend bulletinSend = bulletinSends.get(0);
			if (bulletinSend.getUserPk().equals("-1")) {
				list = bulletinInfoService.getBulletinSendList(bulletinPk, read, 1);
			} else {
				list = bulletinInfoService.getBulletinSendList(bulletinPk, read, 0);
			}
		}
		return Result.resultInfo(ResultCode.SUCCESS, list);

	}

	@ApiOperation(value = "公告读取")
	@ResponseBody
	@RequestMapping(value = "/toBulletinInfo", method = { RequestMethod.POST })
	public void toBulletinInfo(String sendPk) {
		BulletinSend bulletinSend = bulletinSendService.selectById(sendPk);
		String userId = getUserId();
		if (bulletinSend != null) {
			// //判断该公告是否已读
			// BulletinReadItem readItem =
			// bulletinInfoService.getBulletinReadItemByUserId(userId,
			// bulletinSend.getBulletinPk(), bulletinSend.getSendPk());
			// if(readItem!=null){
			// BulletinInfo bulletinInfo =
			// bulletinService.getBulletinInfoById(bulletinSend.getBulletinPk());
			// if(bulletinInfo!=null &&
			// StrKit.notNull(bulletinInfo.getFileId())){
			// //获取附件信息
			// FileAttach fileAttach =
			// FileAttach.dao.findById(bulletinInfo.getFileId());
			// setAttr("fileAttach", fileAttach);
			// }
			// setAttr("bulletinInfo", bulletinInfo);
			// render("/web/bulletin/bulletin_info.jsp");
			// }else{
			// boolean result = true;
			// BulletinInfo bulletinInfo =
			// bulletinService.getBulletinInfoById(bulletinSend.getBulletinPk());
			// if(bulletinInfo!=null){
			// bulletinInfo.setReadNo(bulletinInfo.getReadNo()+1);
			// result = bulletinInfo.update();
			//
			// if(StrKit.notNull(bulletinInfo.getFileId())){
			// //获取附件信息
			// FileAttach fileAttach =
			// FileAttach.dao.findById(bulletinInfo.getFileId());
			// setAttr("fileAttach", fileAttach);
			// }
			// }else{
			// bulletinInfo = new BulletinInfo();
			// bulletinInfo.setBulletinTitle("公告");
			// bulletinInfo.setBulletinNote("公告不存在或者被删除！");
			// }
			// setAttr("bulletinInfo", bulletinInfo);
			//

			BulletinInfo bulletinInfo = bulletinInfoService.selectById(bulletinSend.getBulletinPk());
			if (bulletinInfo != null) {
				BulletinReadItem readItem = bulletinInfoService.getBulletinReadItemByUserId(userId,
						bulletinSend.getBulletinPk(), bulletinSend.getSendPk());
				if (readItem == null) {
					bulletinInfo.setReadNo(bulletinInfo.getReadNo() + 1);
					bulletinInfoService.updateById(bulletinInfo);
					
					/** 向公告已读表添加数据 **/
					BulletinReadItem bulletinReadItem = new BulletinReadItem();
					bulletinReadItem.setBulletinPk(bulletinSend.getBulletinPk());
					bulletinReadItem.setSendPk(bulletinSend.getSendPk());
					bulletinReadItem.setState(bulletinSend.getState());
					bulletinReadItem.setUserPk(userId);
					bulletinReadItem.setCreateTime(DateUtil.toStr(new Date(), DateUtil.timeStampPattern));
					bulletinReadItem.setCreateName(bulletinSend.getCreateName());
					bulletinReadItemService.insert(bulletinReadItem);
				}

				//
				if (!bulletinSend.getUserPk().equals("-1")) {
					bulletinSend.setBulletinIsread(1);
					bulletinSendService.updateById(bulletinSend);
				}
				
			}
		}
	}

	@ApiOperation(value = "公告详情")
	@ResponseBody
	@RequestMapping(value = "/getBulletinData", method = { RequestMethod.POST })
	public Result getBulletinData(@RequestParam(value = "id")String id){
		BulletinInfo bulletinInfo = bulletinInfoService.getBulletinData(id);
		return Result.resultInfo(ResultCode.SUCCESS,bulletinInfo);
	}

}
