package com.metaShare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.Resource;
import net.sf.ehcache.CacheManager;

/**
 * SpringCacheConfig
 */
@Configuration
public class SpringCacheConfig {
	
	@Bean(name="ehcacheManager")
	public EhCacheManagerFactoryBean getEhCacheManagerFactoryBean(){
		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		Resource resource = new ClassPathResource("ehcache.xml");
		cacheManagerFactoryBean.setConfigLocation(resource);
		return cacheManagerFactoryBean;
	}
	
	@Bean(name="cacheManager")
	public EhCacheCacheManager getEhCacheCacheManager(CacheManager cacheManager){
		EhCacheCacheManager cacheCacheManager = new EhCacheCacheManager();
		cacheCacheManager.setCacheManager(cacheManager);
		return cacheCacheManager;
	}
}
