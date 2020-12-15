package com.metaShare.modules.sys.oauth2;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import com.metaShare.common.entity.R;
import com.metaShare.common.utils.JSONUtils;

/**
 * oauth2过滤器
 */
public class OAuth2Filter extends AuthenticatingFilter {

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		String token = getRequestToken((HttpServletRequest) request);
		if (StringUtils.isBlank(token)) {
			return null;
		}
		return new OAuth2Token(token);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return false;
	}

	/**
	 * 检查token
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// 获取请求token，如果token不存在，直接返回401
		String token = getRequestToken((HttpServletRequest) request);
		if (StringUtils.isBlank(token)) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			R r = R.error(HttpStatus.SC_UNAUTHORIZED, "invalid token");
			String json = JSONUtils.beanToJson(r);
			httpResponse.getWriter().print(json);

			return false;
		}

		return executeLogin(request, response);
	}

	/**
	 * 登录失败
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setContentType("application/json;charset=utf-8");
		try {
			Throwable throwable = e.getCause() == null ? e : e.getCause();
			R r = R.error(HttpStatus.SC_UNAUTHORIZED, throwable.getMessage());
			String json = JSONUtils.beanToJson(r);
			httpResponse.getWriter().print(json);
		} catch (Exception ee) {

		}

		return false;
	}

	private String getRequestToken(HttpServletRequest httpRequest) {
		String token = httpRequest.getHeader("token");
		if (StringUtils.isBlank(token)) {
			token = httpRequest.getParameter("token");
		}
		return token;
	}

}
