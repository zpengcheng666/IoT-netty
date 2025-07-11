package com.sydh.framework.interceptor;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.annotation.RepeatSubmit;
import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.utils.ServletUtils;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public abstract class RepeatSubmitInterceptor
        implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.<RepeatSubmit>getAnnotation(RepeatSubmit.class);
            if (annotation != null) {
                if (isRepeatSubmit(request, annotation)) {

                    AjaxResult ajaxResult = AjaxResult.error(annotation.message());
                    ServletUtils.renderString(response, JSON.toJSONString(ajaxResult));
                    return false;
                }
            }
            return true;
        }


        return true;
    }

    public abstract boolean isRepeatSubmit(HttpServletRequest paramHttpServletRequest, RepeatSubmit paramRepeatSubmit);
}
