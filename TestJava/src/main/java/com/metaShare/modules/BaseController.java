package com.metaShare.modules;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.metaShare.modules.sys.entity.*;
import com.metaShare.modules.sys.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.baomidou.mybatisplus.plugins.Page;
import com.metaShare.common.constant.SystemConstant;
import com.metaShare.common.editor.DateEditor;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.common.utils.DateTimeUtils;
import com.metaShare.common.utils.DateUtil;
import com.metaShare.common.utils.JwtUtil;
import com.metaShare.common.utils.ShiroUtils;
import com.metaShare.common.utils.StatusEnum;
import com.metaShare.common.utils.StringUtils;

/**
 * Controller公共组件
 */
public abstract class BaseController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private PoSysInfoService poSysInfoService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleUserService sysRoleUserService;
	@Autowired
	private SysRoleOrgService sysRoleOrgService;
	@Autowired
	private SysOrganizationService sysOrganizationService;

	public HttpServletRequest getRequest() {
		return request;
	}

	public static String getSessionId() {
		return (String) ShiroUtils.getSession().getId();
	}

	protected String getUserId() {
 		String token = request.getHeader("Authorization");
		String Userid="";
		try {
			Userid = JwtUtil.getUserId(token);
		} catch (Exception e) {
			// TODO: handle exception
			Userid= request.getHeader("USERID");

		}
		return Userid;
		// return getUser().getId();
	}

	protected String getnowTime() {
		return DateTimeUtils.getNowDate(DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
		// return getUser().getId();
	}

	protected SysUser getUserInfo()  {
		return sysUserService.selectById(getUserId());
	}

	/**
	 * 根据登录名获取用户信息
	 * @param loginName
	 * @return
	 * @throws Exception 
	 */
	protected SysUser getUserInfo(String loginName) throws Exception  {
		if(StringUtils.isNotEmpty(loginName)){
			return sysUserService.findByLoginName(loginName);
		}
		return sysUserService.selectById(getUserId());
	}
	
	protected String getUserName() {
		String token = request.getHeader("Authorization");
		String UserName = JwtUtil.getUserName(token);
		return UserName;
		// return getUser().getId();
	}

	protected String getUserId(HttpServletRequest request) {
		String userId = (String) request.getAttribute("userId");
		if (StringUtils.isEmpty(userId)) {
			userId = "544fb71be58c42fd9479c15f4d0c455e";
		}
		return userId;
	}

	protected <T> Page<T> getPage() {
		int current = 0;
		int rows = 10;
		if (request.getParameter(SystemConstant.PAGE_NO) != null) {
			current = Integer.parseInt(request.getParameter(SystemConstant.PAGE_NO));
		}
		if (request.getParameter(SystemConstant.PAGE_SIZE) != null) {
			rows = Integer.parseInt(request.getParameter(SystemConstant.PAGE_SIZE));
		}
		Page<T> page = new Page<>(current, rows);
		return page;
	}

	/**
	 * 初始化数据绑定 1. 将所有传递进来的String进行HTML编码，防止XSS攻击 2. 将字段中Date类型转换为String类型
	 *
	 * @param binder
	 *            the binder
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new DateEditor());
	}
	protected void saveOperatorInfo(String info,int type,String userName){
		SysInfo poSysInfo = new SysInfo();
		poSysInfo.setCreateTime(DateUtil.getDate(DateUtil.timeStampPattern));
		poSysInfo.setInfo(info);
		poSysInfo.setOperator(userName);
		poSysInfo.setModule(StatusEnum.findEnumDescByValue(StatusEnum.LogInfoType, type));
		if(StringUtils.isNotEmpty(userName)){
			poSysInfoService.insert(poSysInfo);
		}
	}
	public static String payRecordNo() {
		try {
			String timeStr = DateUtil.toStr(new Date(), DateUtil.timeStrPattern);
			int radomStr = (int)(Math.random()*1000);
			return timeStr+radomStr;
		} catch (Exception e) {
			System.out.println("生成流水号出错====" + e.toString());
		}
		return "";
	}


	/**
	 * 输出结果包括三种  1: 输出空字符串"" 表示看不到任何数据  2: 输出-1.表示看到全部数据 3：输出相应字符串，可看到对应数据
	 * @return
	 */
	public String getDataPermission(){
		//获取当前登录人的角色Id
		SysUser sysUser = getUserInfo();
		List<String> roleIds = sysRoleUserService.findRoleIdsByUser(sysUser.getId());
		String permissionStr = "";
		for(String roleId : roleIds){
			SysRole sysRole = sysRoleService.selectById(roleId);
			if(sysRole.getRoleType() == 1){
				//表示为自定义数据，去roleOrg找到对应数据权限
				Wrapper roleOrapper = Condition.create().eq("role_id", roleId);
				List<SysRoleOrg> sysRoleOrgList = sysRoleOrgService.selectList(roleOrapper);
				for(SysRoleOrg sysRoleOrg : sysRoleOrgList){
					permissionStr += sysRoleOrg.getOrgId() + ",";
				}
			}else if(sysRole.getRoleType() == 2){
				//表示可以看全部数据
				permissionStr += "-1,";
			}else if(sysRole.getRoleType() == 3){
				//只看本部门数据
				permissionStr += sysUser.getOrgId() + ",";
			}else if(sysRole.getRoleType() == 4){
				//看本部门以及下级数据
				SysOrganization sysOrganization = sysOrganizationService.selectById(sysUser.getOrgId());
				List<SysOrganization> sysOrganizationList = sysOrganizationService.getChildrenListOnOrgCode(sysOrganization.getOrgCode());
				for(SysOrganization chiledrenOrg : sysOrganizationList){
					permissionStr += chiledrenOrg.getId() + ",";
				}
			}
		}
		if(permissionStr.length() > 0){
			permissionStr = permissionStr.substring(0,permissionStr.length() - 1);
		}

		if(permissionStr.contains("-1")){
			permissionStr = "-1";
		}
		return permissionStr;
	}

}
