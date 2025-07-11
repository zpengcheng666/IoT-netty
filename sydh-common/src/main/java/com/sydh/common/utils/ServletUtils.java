/*     */ package com.sydh.common.utils;

import cn.hutool.extra.servlet.ServletUtil;
import com.sydh.common.core.text.Convert;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtils {
    public ServletUtils() {
    }

    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getRequest().getParameter(name));
    }

    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    public static Map<String, String[]> getParams(ServletRequest request) {
        Map var1 = request.getParameterMap();
        return Collections.unmodifiableMap(var1);
    }

    public static Map<String, String> getParamMap(ServletRequest request) {
        HashMap var1 = new HashMap();
        Iterator var2 = getParams(request).entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            var1.put(var3.getKey(), StringUtils.join((Object[])var3.getValue(), ","));
        }

        return var1;
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes var0 = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes)var0;
    }

    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String var1 = request.getHeader("accept");
        if (var1 != null && var1.contains("application/json")) {
            return true;
        } else {
            String var2 = request.getHeader("X-Requested-With");
            if (var2 != null && var2.contains("XMLHttpRequest")) {
                return true;
            } else {
                String var3 = request.getRequestURI();
                if (StringUtils.inStringIgnoreCase(var3, new String[]{".json", ".xml"})) {
                    return true;
                } else {
                    String var4 = request.getParameter("__ajax");
                    return StringUtils.inStringIgnoreCase(var4, new String[]{"json", "xml"});
                }
            }
        }
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static String getClientIP() {
        HttpServletRequest var0 = getRequest();
        return var0 == null ? null : ServletUtil.getClientIP(var0, new String[0]);
    }
}
