package com.sydh.framework.config;

// import com.sydh.common.filter.PiceaFilter;  // 开源版本不再使用证书验证拦截器
import com.sydh.common.filter.RepeatableFilter;
import com.sydh.common.filter.XssFilter;
import com.sydh.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Value("${xss.excludes}")
    private String excludes;
    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @Bean
    @ConditionalOnProperty(value = {"xss.enabled"}, havingValue = "true")
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[0]);
        registration.setFilter((Filter) new XssFilter());
        registration.addUrlPatterns(StringUtils.split(this.urlPatterns, ","));
        registration.setName("xssFilter");
        registration.setOrder(-2147483648);
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", this.excludes);
        registration.setInitParameters(initParameters);
        return registration;
    }


    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter((Filter) new RepeatableFilter());
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setName("repeatableFilter");
        registration.setOrder(2147483647);
        return registration;
    }

    // 开源版本：禁用证书验证拦截器
    // 注释掉PiceaFilter，去掉证书验证和拦截
    /*
    @Bean
    public FilterRegistrationBean lcFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter((Filter) new PiceaFilter());
        registration.addUrlPatterns(new String[]{"/login", "/register"});
        registration.setName("PiceaFilter");
        registration.setOrder(-2147483648);
        return registration;
    }
    */
}
