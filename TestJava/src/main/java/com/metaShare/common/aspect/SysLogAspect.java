package com.metaShare.common.aspect;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.metaShare.common.utils.HttpContextUtils;
import com.metaShare.common.utils.IpUtils;
import com.metaShare.common.utils.JSONUtils;
import com.metaShare.common.utils.ShiroUtils;
import com.metaShare.modules.sys.dao.SysLogDao;
import com.metaShare.modules.sys.entity.SysLog;
import com.metaShare.modules.sys.entity.SysUser;

/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class SysLogAspect {

	@Autowired
	private SysLogDao sysLogMapper;

	@Pointcut("@annotation(com.metaShare.common.annotation.SysLog)")
	public void logPointCut() {

	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		// 执行方法
		Object result = point.proceed();
		// 执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;
		// 保存日志
		saveSysLog(point, time);
		return result;
	}

	private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLog sysLog = new SysLog();
		com.metaShare.common.annotation.SysLog syslog = method.getAnnotation(com.metaShare.common.annotation.SysLog.class);
		if (syslog != null) {
			// 注解上的描述
			sysLog.setOperation(syslog.value());
		}
		// 请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.setMethod(className + "." + methodName + "()");
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		try {
			String params = JSONUtils.beanToJson(args[0]);
			sysLog.setParams(params);
		} catch (Exception e) {

		}
		// 获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		// 设置IP地址
		sysLog.setIp(IpUtils.getIpAddr(request));
		// 用户名
		SysUser currUser = ShiroUtils.getUserEntity();
		if (currUser == null) {
			if (sysLog.getParams() == null) {
				sysLog.setUserId(0);
				sysLog.setUsername(sysLog.getParams());
			} else {
				sysLog.setUserId(0);
				sysLog.setUsername("获取用户信息为空");
			}
		} else {
			//sysLog.setUserId(ShiroUtils.getUserId());
			sysLog.setUsername(ShiroUtils.getUserEntity().getName());
		}
		sysLog.setTime(time);
		// 保存系统日志
		sysLogMapper.insert(sysLog);
	}

}
