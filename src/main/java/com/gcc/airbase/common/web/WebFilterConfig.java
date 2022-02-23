package com.gcc.airbase.common.web;

import com.gcc.airbase.common.filter.interceptor.HttpRequestApiInterceptor;
import com.gcc.airbase.config.AirBaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnWebApplication
@Configuration
@Slf4j
public class WebFilterConfig implements WebMvcConfigurer {


    @Autowired
    AirBaseProperties properties;

    @Bean
    public HttpRequestApiInterceptor httpRequestApiInterceptor(){
        return new HttpRequestApiInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if(properties.getApiLogEnable()) {
            registry.addInterceptor(httpRequestApiInterceptor()).addPathPatterns("/**")
                    .excludePathPatterns("/webjars/**", "/v3/**",
                            "/swagger-resources/**",
                            "/*.html", "/*.ico",
                            "/actuator/**");
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
