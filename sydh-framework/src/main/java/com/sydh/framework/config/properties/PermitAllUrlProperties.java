package com.sydh.framework.config.properties;

import com.sydh.common.annotation.Anonymous;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class PermitAllUrlProperties
        implements InitializingBean, ApplicationContextAware
{
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    public String ASTERISK = "*";

    @Override
    public void afterPropertiesSet() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);
            
            // 检查方法级别的@Anonymous注解
            Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            Optional.ofNullable(method).ifPresent(anonymous -> {
                addUrlsFromRequestMappingInfo(info);
            });
            
            // 检查控制器级别的@Anonymous注解
            Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
            Optional.ofNullable(controller).ifPresent(anonymous -> {
                addUrlsFromRequestMappingInfo(info);
            });
        });
    }

    /**
     * 从RequestMappingInfo中提取URL并添加到urls列表
     */
    private void addUrlsFromRequestMappingInfo(RequestMappingInfo info) {
        if (info.getPatternsCondition() != null) {
            info.getPatternsCondition().getPatterns().forEach(pattern -> {
                // 将路径变量替换为通配符
                String url = RegExUtils.replaceAll(pattern, PATTERN, ASTERISK);
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    public List<String> getUrls() {
        return this.urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
