package com.metaShare.common.security;

import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.metaShare.common.tool.state.Result;
import com.metaShare.common.tool.state.ResultCode;

public class MyUserFilter extends AccessControlFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		if (isLoginRequest(request, response)) {
			return true;
		} else {
			Subject subject = getSubject(request, response);
			return subject.getPrincipal() != null;
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		try {
			httpServletResponse.setContentType("application/json");
			httpServletResponse.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			Result result = Result.resultInfo(ResultCode.SESSION_EXPIRED, "session过期");
			writer.write(JSON.toJSONString(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
