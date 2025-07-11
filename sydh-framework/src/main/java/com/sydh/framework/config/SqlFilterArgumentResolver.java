package com.sydh.framework.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SqlFilterArgumentResolver
        implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(SqlFilterArgumentResolver.class);


    private static final String[] KEYWORDS = new String[]{"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop", "sleep"};

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Page.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);

        String[] ascs = request.getParameterValues("ascs");
        String[] descs = request.getParameterValues("descs");
        String current = request.getParameter("current");
        String size = request.getParameter("size");

        Page<?> page = new Page();
        if (StrUtil.isNotBlank(current)) {
            page.setCurrent(Long.parseLong(current));
        }

        if (StrUtil.isNotBlank(size)) {
            page.setSize(Long.parseLong(size));
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        Optional.<String[]>ofNullable(ascs).ifPresent(s -> orderItemList.addAll((Collection) Arrays.<String>stream(s).filter(sqlInjectPredicate()).map(OrderItem::asc).collect(Collectors.toList())));

        Optional.<String[]>ofNullable(descs).ifPresent(s -> orderItemList.addAll((Collection) Arrays.<String>stream(s).filter(sqlInjectPredicate()).map(OrderItem::desc).collect(Collectors.toList())));

        page.addOrder(orderItemList);
        return page;
    }


    private Predicate<String> sqlInjectPredicate() {
        return sql -> {
            for (String keyword : KEYWORDS) {
                if (StrUtil.containsIgnoreCase(sql, keyword)) {
                    return false;
                }
            }
            return true;
        };
    }
}
