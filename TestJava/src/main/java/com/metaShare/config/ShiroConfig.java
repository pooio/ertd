package com.metaShare.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import com.metaShare.common.security.JwtFilter;
import com.metaShare.common.security.SessionFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.metaShare.common.security.UserRealm;

/**
 * Shiro配置
 */
@Configuration
public class ShiroConfig {

	@Bean
	public SessionManager sessionManager() {

		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		//MySessionManager sessionManager = new MySessionManager();
		// 设置session过期时间为1小时(单位：毫秒)，默认为30分钟
		sessionManager.setGlobalSessionTimeout(1 * 60 * 1000);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		//sessionManager.setSessionIdUrlRewritingEnabled(false);
		return sessionManager;
	}

	@Bean
	public SecurityManager securityManager(UserRealm userRealm, SessionManager sessionManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(userRealm);
		securityManager.setSessionManager(sessionManager);
		return securityManager;
	}

	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);

		// oauth过滤
		LinkedHashMap<String, Filter> filters = new LinkedHashMap<>();
		filters.put("session", new SessionFilter());
		filters.put("jwt", new JwtFilter());

		//filters.put("myUser", new MyUserFilter());
		shiroFilter.setFilters(filters);

		shiroFilter.setLoginUrl("/login.html");
		shiroFilter.setSuccessUrl("/");

		Map<String, String> filterMap = new LinkedHashMap<>();
		filterMap.put("/static/**", "anon");
		filterMap.put("/swagger/**", "anon");
		filterMap.put("/druid/**", "anon");
		filterMap.put("/login", "anon");
		filterMap.put("/api/login", "anon");
		filterMap.put("/api/sys/sysConfig/allList", "anon");
		filterMap.put("/api/sys/user/sendRecoverEmail", "anon");
		filterMap.put("/api/sys/user/recoverPassword", "anon");
		filterMap.put("/favicon.ico", "anon");
		filterMap.put("/api/user/register", "anon");
		filterMap.put("/", "anon");
		filterMap.put("/api/**", "jwt");
		shiroFilter.setFilterChainDefinitionMap(filterMap);
		return shiroFilter;
	}

	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
		proxyCreator.setProxyTargetClass(true);
		return proxyCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}
}
