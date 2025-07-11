package com.sydh.common.filter;
//
//import com.sydh.common.core.domain.LVerify;
//import java.io.IOException;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class PiceaFilter implements Filter {
//    private static final Logger ac = LoggerFactory.getLogger(PiceaFilter.class);
//    public static boolean ret = true;
//
//    public PiceaFilter() {
//    }
//
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        try {
//            if (!(servletResponse instanceof HttpServletResponse)) {
//                ac.error("请求或响应类型不匹配");
//                throw new ServletException("请求或响应类型不匹配");
//            } else {
//                ac.info("开源版本 - 跳过证书验证拦截器，直接通过请求: {}", ((HttpServletRequest)servletRequest).getRequestURI());
//                ret = true;
//
//                filterChain.doFilter(servletRequest, servletResponse);
//            }
//        } catch (Exception var9) {
//            ac.error("证书验证拦截器发生异常: ", var9);
//            throw new ServletException("证书验证拦截器发生异常", var9);
//        }
//    }
//
//    public void destroy() {
//    }
//}
