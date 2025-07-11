package com.sydh.framework.config;

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid监控配置类
 * 用于移除Druid监控页面中的广告信息
 */
@Configuration
public class DruidConfig {
    
    @Bean
    @ConditionalOnProperty(name = {"spring.datasource.druid.stat-view-servlet.enabled"}, havingValue = "true")
    public FilterRegistrationBean<Filter> removeDruidFilterRegistrationBean(DruidStatProperties properties) {
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();

        String pattern = (config.getUrlPattern() != null) ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");

        Filter filter = new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                // 初始化方法，无需实现
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                    throws IOException, ServletException {
                chain.doFilter(request, response);
                response.resetBuffer();

                String text = Utils.readFromResource("support/http/resources/js/common.js");
                // 移除Druid监控页面的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy() {
                // 销毁方法，无需实现
            }
        };
        
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
