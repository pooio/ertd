package com.metaShare.common.security;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

/**
 * 传统shiro会话管理是基于session+cookie机制，shiro默认从cookie中获取sessionId来维持整个会话系统，从而实现shiro的认证和授权信息，
 * 自定义sessionId的获取,用于前后分离中，从ajax请求头获取sessionId以维持shiro会话，实现shiro权限控制
 * @author jeremy.li
 * @since 2019-12-05
 *
 */
public class MySessionManager extends DefaultWebSessionManager {
	
	private static final String AUTHORIZATION = "Authorization";
	 
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

	public MySessionManager() {
        super();
    }
 
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader("sessionId");
        //如果请求头中有 Authorization 则其值为sessionId
        if (!StringUtils.isEmpty(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            //否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }
    }

}
