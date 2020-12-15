package com.metaShare.modules.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import io.lettuce.core.dynamic.annotation.Value;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config extends WebMvcConfigurationSupport {
    @Bean
    public Docket createAccepterRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("系统管理API")//分组名,不指定默认为default
                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.basePackage("com.metaShare.modules"))// 扫描的包路径
                .paths(PathSelectors.any())// 定义要生成文档的Api的url路径规则
                .build()
                .apiInfo(apiInfo())// 设置swagger-ui.html页面上的一些元素信息
                .enable(true);
    }
 
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("业务数据接收器")
                .description("提供业务数据接收模块")
                .termsOfServiceUrl("http://127.0.0.1:8081/")
                .version("1.0")
                .build();
    }
//     //addResourceHandlers方法添加了两个资源处理程序，
//     //这段代码的主要作用是对Swagger UI的支持。(访问接口页面为空白时可加上)
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/**")
//        		.addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
// 
//    }
}
