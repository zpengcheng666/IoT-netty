package com.sydh.common.filter;

import com.sydh.common.enums.HttpMethod;
import com.sydh.common.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class XssFilter
        implements Filter {
    public List<String> excludes = new ArrayList<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String str = filterConfig.getInitParameter("excludes");
        if (StringUtils.isNotEmpty(str)) {

            String[] arrayOfString = str.split(",");
            for (byte b = 0; arrayOfString != null && b < arrayOfString.length; b++) {
                this.excludes.add(arrayOfString[b]);
            }
        }
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (a(httpServletRequest, httpServletResponse)) {

            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter((ServletRequest) xssHttpServletRequestWrapper, response);
    }


    private boolean a(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) {
        String str1 = paramHttpServletRequest.getServletPath();
        String str2 = paramHttpServletRequest.getMethod();

        if (str2 == null || HttpMethod.GET.matches(str2) || HttpMethod.DELETE.matches(str2)) {
            return true;
        }
        return StringUtils.matches(str1, this.excludes);
    }

    @Override
    public void destroy() {
    }
}
