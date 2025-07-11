package com.sydh.common.filter;

import com.sydh.common.utils.StringUtils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RepeatableFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RepeatedlyRequestWrapper repeatedlyRequestWrapper = null;
        if (request instanceof HttpServletRequest &&
                StringUtils.startsWithIgnoreCase(request.getContentType(), "application/json")) {
            repeatedlyRequestWrapper = new RepeatedlyRequestWrapper((HttpServletRequest) request, response);
        }
        if (null == repeatedlyRequestWrapper) {

            chain.doFilter(request, response);
        } else {

            chain.doFilter((ServletRequest) repeatedlyRequestWrapper, response);
        }
    }

    public void destroy() {
    }
}
