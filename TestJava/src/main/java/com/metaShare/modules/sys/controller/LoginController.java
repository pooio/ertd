package com.metaShare.modules.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.metaShare.common.utils.*;
import com.metaShare.modules.core.entity.SysFileAttach;
import com.metaShare.modules.core.service.SysFileAttachService;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.entity.SysRole;
import com.metaShare.modules.sys.service.SysRoleService;
import com.metaShare.modules.sys.service.SysUserService;
import com.metaShare.modules.sys.service.SysconfigService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.metaShare.common.annotation.SysLog;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;
import com.metaShare.modules.BaseController;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.vo.UserVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.session.Session;

@Controller
@CrossOrigin
@Api(tags = "用户登录")
@RequestMapping(value = "/api")
public class LoginController extends BaseController {

	@Autowired
	private SysFileAttachService sysFileAttachService;
	@Autowired
	private SysconfigService sysconfigService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleService sysRoleService;


	@RequestMapping(value = "login", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value="登录")
	public Result login(String username, String password,HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			String token = "defalut";
			Subject subject = ShiroUtils.getSubject();
			UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
			subject.login(usernamePasswordToken);
			token = getToken();
			map.put("authCode", token);
			SysUser user = ShiroUtils.getUserEntity();
			if(user.getStatus()==0){
				return Result.resultInfo(ResultCode.VAIL_ERROR, "账号已被锁定,请联系管理员");
			}
			map.put("userInfo", getUserVo());
//			Session session = ShiroUtils.getSession();
//			session.setAttribute(getUserVo().getId(), getUserVo());
//			Object object = session.getAttribute(getUserVo().getId());
			map.put("sessionId", subject.getSession().getId());
			response.setHeader("Authorization", token);
		} catch (IncorrectCredentialsException e) {
			e.printStackTrace();
			return Result.resultInfo(ResultCode.VAIL_ERROR, "账号或密码不正确");
		} catch (LockedAccountException e) {
			e.printStackTrace();
			return Result.resultInfo(ResultCode.VAIL_ERROR, "账号已被锁定,请联系管理员");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return Result.resultInfo(ResultCode.VAIL_ERROR, "账户验证失败");
		}
		return Result.resultInfo(ResultCode.SUCCESS, map);
	}
	
	private UserVo getUserVo(){
		SysUser user = ShiroUtils.getUserEntity();
		UserVo userVo = new UserVo();
		if(null != user){
			userVo.setId(user.getId());
			userVo.setEmail(user.getEmail());
			userVo.setName(user.getName());
			userVo.setOrgName(user.getOrgName());
			userVo.setPhoneNumber(user.getPhoneNumber());
			Integer sex = user.getSex();
			String sexName = "";
			if(null != sex){
				if(sex == 0){
					sexName = "女";
				}else if (sex == 1) {
					sexName = "男";
				}
			}
			userVo.setSexName(sexName);
			userVo.setHeadImg(user.getHeadImg());
			userVo.setOrgId(user.getOrgId());
			userVo.setAge(user.getAge());
			List<SysRole> sysRoleList = sysRoleService.findByUser(user.getId());
			String roleName = "";
			for(SysRole sysRole : sysRoleList){
				roleName += sysRole.getRoleName() + "、";
			}
			userVo.setRoleName(roleName.substring(0,roleName.length()-1));
		}
		return userVo;
	}
	
	/**
	 * 获取token
	 * @return
	 */
	private String getToken() {
		SysUser user = ShiroUtils.getUserEntity();
		JSONObject json = new JSONObject();
		json.put("userId", user.getId());
		json.put("username", user.getLoginName());
		json.put("host", NetWorkUtil.getIpAddress(this.getRequest()));
		return JwtUtil.buildJWT(json.toJSONString());
	}

	@SysLog("退出系统")
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public void logout() {
		ShiroUtils.logout();
	}


	/**
	 * IP地址是否为空
	 * 
	 * @param ipAddress
	 * @return
	 */
	private Boolean ipIsEmpty(String ipAddress) {
		if (ipAddress == null || ipAddress.length() == 0 ||
				"unknown".equalsIgnoreCase(ipAddress)) {
			return true;
		}
		return false;
	}
}
