package com.metaShare.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringHelper implements ApplicationContextAware {

	private static ApplicationContext context = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	/**
	 * 根据spring配置中的名词获得spring对象
	 * @param beanName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanByBeanname(String beanName){
		return context.getBean(beanName)==null?null:(T) context.getBean(beanName);
	}
	
	/**
	 * 根据class获得spring中的对象
	 * @param clazz 类
	 * @return
	 */
	public static <T> T getBeanByClassname(Class<T> clazz){
		return context.getBean(clazz);
	}
}
