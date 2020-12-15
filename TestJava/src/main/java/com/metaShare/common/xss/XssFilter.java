package com.metaShare.common.xss;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import com.metaShare.common.utils.JwtUtil;
import com.metaShare.common.utils.SpringUtils;
import com.metaShare.common.utils.StrUtils;
import com.metaShare.modules.sys.entity.SysUser;
import com.metaShare.modules.sys.service.SysUserService;

import ch.qos.logback.classic.Logger;

/**
 * XSS过滤
 */
public class XssFilter implements Filter {
	Logger logger = (Logger) LoggerFactory.getLogger(XssFilter.class);

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
		HttpServletResponse res = (HttpServletResponse) response;
		String url = xssRequest.getRequestURI();
		  // 如果是OPTIONS则结束请求
//        if (!HttpMethod.OPTIONS.toString().equals(xssRequest.getMethod())) {
//        	if (url.indexOf("api") > 0 && url.indexOf("login") < 0) {
//    			String token = xssRequest.getHeader("Authorization");// 从 http
//    			// 执行认证
//    			if (token != null) {
//    				res.setStatus(HttpStatus.SC_UNAUTHORIZED);
//    				return;
//    			}
//    			// 获取 token 中的 user id
//    			String userId;
//    			userId = JwtUtil.getUserId(token);
//    			if (!StrUtils.isEmpty(userId)) {
//    				throw new RuntimeException("401");
//    			}
//    			SysUserService sysUserService = SpringUtils.getBean("sysUserService");
//    			SysUser user = sysUserService.selectById(userId);
//    			if (user == null) {
//    				throw new RuntimeException("用户不存在，请重新登录");
//    			}
//    		}
// 
//        }
		
		Enumeration<String> parameterNames = xssRequest.getParameterNames();
		String parameter = "请求url = " + url + "，参数【";
		while (parameterNames.hasMoreElements()) {

			String name = parameterNames.nextElement();
			parameter += name + "=";
			String vaule = xssRequest.getParameter(name);
			parameter += vaule + ",";

		}
		parameter += "】";
		// System.out.println(parameter);
		logger.info(parameter);
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		res.setHeader("Access-Control-Max-Age", "3600");
		res.setHeader("Access-Control-Allow-Headers",
				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
//		 String token = xssRequest.getHeader("Authorization");//header方式
//		 boolean isFilter = true;
//		 String method = ((HttpServletRequest) request).getMethod();
//		 if (method.equals("OPTIONS")) {
//			 res.setStatus(HttpServletResponse.SC_OK);
//		 }else{
//		  if (url.indexOf("api") > 0 && url.indexOf("login") < 0) {
// 			// 执行认证
// 			if (token != null) {
// 				res.setStatus(HttpStatus.SC_UNAUTHORIZED);
// 				return;
// 			}
// 			// 获取 token 中的 user id
// 			String userId;
// 			userId = JwtUtil.getUserId(token);
// 			if (!StrUtils.isEmpty(userId)) {
// 				res.setStatus(HttpStatus.SC_UNAUTHORIZED);
// 			}
// 			SysUserService sysUserService = SpringUtils.getBean("sysUserService");
// 			SysUser user = sysUserService.selectById(userId);
// 			if (user == null) {
// 				res.setStatus(HttpStatus.SC_UNAUTHORIZED);
// 			}}
//		 }
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
