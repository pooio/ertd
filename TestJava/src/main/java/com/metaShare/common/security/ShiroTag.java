package com.metaShare.common.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import com.metaShare.common.utils.ShiroUtils;
import com.metaShare.modules.sys.entity.SysUser;

/**
 * Shiro权限标签
 */
@Component
public class ShiroTag {

	public boolean hasPermission(String permission) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isPermitted(permission);
	}

	public SysUser user() {
		return ShiroUtils.getUserEntity();
	}
}