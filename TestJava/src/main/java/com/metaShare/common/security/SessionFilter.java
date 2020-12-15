package com.metaShare.common.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;

public class SessionFilter extends PathMatchingFilter {

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (!SecurityUtils.getSubject().isAuthenticated()) {
			if (httpRequest.getHeader("x-requested-with") != null
					&& httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
				httpResponse.setHeader("session-status", "timeout");
				httpResponse.sendError(HttpStatus.SC_UNAUTHORIZED);
				return false;
			}
		}
		return true;
	}
}