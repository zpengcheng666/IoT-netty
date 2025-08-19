package com.sydh.common.core.handler;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;

public class ServletUtils {
    public static void writeJSON(HttpServletResponse response, Object object) {
        String content = JSONUtil.toJsonStr(object);
        ServletUtil.write(response, content, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    public static void write(HttpServletResponse response, String text, String contentType) {
        ServletUtil.write(response, text, contentType);
    }
}
