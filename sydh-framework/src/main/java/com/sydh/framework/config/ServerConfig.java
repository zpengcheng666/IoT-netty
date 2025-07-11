package com.sydh.framework.config;

import com.sydh.common.utils.ServletUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 服务器配置类
 * 用于获取服务器URL相关信息
 */
@Component
public class ServerConfig {
    
    /**
     * 获取服务器完整URL
     *
     * @return 服务器URL，如果无法获取则返回空字符串
     */
    public String getUrl() {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            if (request == null) {
                // 尝试通过Spring的RequestContextHolder获取
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    request = attrs.getRequest();
                }
            }
            if (request == null) {
                return "";
            }
            return getDomain(request);
        } catch (Exception e) {
            // 记录日志或处理异常
            return "";
        }
    }

    /**
     * 根据HttpServletRequest获取域名URL
     *
     * @param request HTTP请求对象
     * @return 域名URL，如果请求为空则返回空字符串
     */
    public static String getDomain(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        
        try {
            StringBuffer url = request.getRequestURL();
            if (url == null) {
                return "";
            }
            
            // 移除请求URI部分，只保留协议、域名和端口
            int uriLength = request.getRequestURI() != null ? request.getRequestURI().length() : 0;
            url.delete(url.length() - uriLength, url.length());
            
            // 获取上下文路径
            String contextPath = getContextPath(request);
            
            // 添加上下文路径
            if (contextPath != null && !contextPath.isEmpty()) {
                url.append(contextPath);
            }
            
            return url.toString();
        } catch (Exception e) {
            // 记录日志或处理异常
            return "";
        }
    }
    
    /**
     * 获取上下文路径
     * 
     * @param request HTTP请求对象
     * @return 上下文路径
     */
    private static String getContextPath(HttpServletRequest request) {
        try {
            // 首先尝试直接获取上下文路径
            String contextPath = request.getContextPath();
            if (contextPath != null) {
                return contextPath;
            }
            
            // 如果直接获取失败，尝试通过Session获取
            HttpSession session = request.getSession(false);
            if (session != null && session.getServletContext() != null) {
                return session.getServletContext().getContextPath();
            }
            
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
