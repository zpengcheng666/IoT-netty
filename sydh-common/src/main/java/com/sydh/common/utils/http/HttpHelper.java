package com.sydh.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static String getBodyString(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (ServletInputStream servletInputStream = request.getInputStream()) {
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader((InputStream) servletInputStream, StandardCharsets.UTF_8));

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            logger.warn("getBodyString出现问题！", e);
        }

        return stringBuilder.toString();
    }
}
