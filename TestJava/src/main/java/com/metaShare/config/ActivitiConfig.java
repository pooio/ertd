package com.metaShare.config;

import com.metaShare.modules.bpm.identity.CustomGroupEntityManager;
import com.metaShare.modules.bpm.identity.CustomGroupEntityManagerFactory;
import com.metaShare.modules.bpm.listener.*;
import org.activiti.engine.*;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Autowired
    private ProcessCompletedEventListener processCompletedEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {

        List<ActivitiEventListener> activitiEventListener=new ArrayList<ActivitiEventListener>();

        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        activitiEventListener.add(processCompletedEventListener );//配置全局监听器

        processEngineConfiguration.setEventListeners(activitiEventListener);

    }


}
