package com.sydh.framework.interceptor.impl;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.annotation.RepeatSubmit;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.filter.RepeatedlyRequestWrapper;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.http.HttpHelper;
import com.sydh.framework.interceptor.RepeatSubmitInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SameUrlDataInterceptor
        extends RepeatSubmitInterceptor {
    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";


    @Value("${token.header}")
    private String header;


    @Autowired
    private RedisCache redisCache;


    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
        String nowParams = "";
        if (request instanceof RepeatedlyRequestWrapper) {

            RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
            nowParams = HttpHelper.getBodyString((ServletRequest) repeatedlyRequest);
        }


        if (StringUtils.isEmpty(nowParams)) {
            nowParams = JSON.toJSONString(request.getParameterMap());
        }
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put("repeatParams", nowParams);
        nowDataMap.put("repeatTime", Long.valueOf(System.currentTimeMillis()));


        String url = request.getRequestURI();


        String submitKey = StringUtils.trimToEmpty(request.getHeader(this.header));


        String cacheRepeatKey = "repeat_submit:" + url + submitKey;

        Object sessionObj = this.redisCache.getCacheObject(cacheRepeatKey);
        if (sessionObj != null) {

            Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
            if (sessionMap.containsKey(url)) {

                Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval())) {
                    return true;
                }
            }
        }
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put(url, nowDataMap);
        this.redisCache.setCacheObject(cacheRepeatKey, cacheMap, Integer.valueOf(annotation.interval()), TimeUnit.MILLISECONDS);
        return false;
    }


    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get("repeatParams");
        String preParams = (String) preMap.get("repeatParams");
        return nowParams.equals(preParams);
    }


    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = ((Long) nowMap.get("repeatTime")).longValue();
        long time2 = ((Long) preMap.get("repeatTime")).longValue();
        if (time1 - time2 < interval) {
            return true;
        }
        return false;
    }
}
