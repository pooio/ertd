package com.metaShare.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metaShare.common.utils.StatusEnum;
import com.metaShare.modules.sys.entity.SysConfig;
import com.metaShare.modules.sys.service.SysconfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Component
public class CorsInterceptor extends WebMvcConfigurationSupport implements HandlerInterceptor {

	@Value("${seed.fileUploadPath}")
	private String path;
	@Autowired
	private SysconfigService sysconfigService;


	// @Override
	// protected void addInterceptors(InterceptorRegistry registry) {
	// registry.addInterceptor(new
	// CorsInterceptor()).addPathPatterns("/user/**");
	// super.addInterceptors(registry);
	// }
	//
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		SysConfig sysConfig5 = sysconfigService.getConfigData(StatusEnum.SysConfigType5.getStrValue());

		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		//外部访问图片路径映射到本地磁盘路径
		if(sysConfig5 != null){
			registry.addResourceHandler("/upload/**").addResourceLocations("file:" + sysConfig5.getConfigContent() + "\\upload\\");
		}
		super.addResourceHandlers(registry);
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Methods",
				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
		response.setHeader("Access-Control-Max-Age", "86400");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		// 如果是OPTIONS则结束请求
		if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
			return false;
		}

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
