package com.metaShare.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;


public class SessionHelper {
	
	private static ConcurrentMap<String, String> onlineUser = new ConcurrentHashMap<String,String>();
	private static final ThreadLocal<Map<Object, Object>> currentUser = new ThreadLocal<Map<Object, Object>>();
	
	public static void setUserSession(String username,String sessionid){
		onlineUser.put(username, sessionid);
		currentUser.get().put(username, sessionid);
	}
	
	public static SysUser getUser(){
		SysUser user = null;
		Subject subject = SecurityUtils.getSubject();
		SysUserService userService = SpringHelper.getBeanByBeanname("userService");
		//通过用户名密码登录，或者是通过rememberMe方式登录
		if(subject!=null && (subject.isRemembered() || subject.isAuthenticated())){
			try {
				user = userService.findByLoginName((String)subject.getPrincipal());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return user;
	}
	
	public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }
	
	public static int getOnlineNum(){
		return onlineUser.size();
	}
	
	public static void remove(String username){
		onlineUser.remove(username);
		currentUser.get().remove(username);
		currentUser.remove();
	}
	public static void logout(){
		SecurityUtils.getSubject().logout();
	}
	
	public static SysUser getUser(HttpServletRequest request){
		SysUser user = SessionHelper.getUser();
		if(user == null) {
			String userId = (String) request.getAttribute("userId");
			SysUserService userService = SpringHelper.getBeanByBeanname("userService");
			if(!StringUtils.isEmpty(userId)){
				user = userService.findOne(userId);
			}
		}
		return user;
	}
}
