package com.sydh.framework.aspectj;

import com.sydh.common.annotation.DataSource;
import com.sydh.common.utils.StringUtils;
import com.sydh.framework.datasource.DynamicDataSourceContextHolder;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Pointcut("@annotation(com.sydh.common.annotation.DataSource)|| @within(com.sydh.common.annotation.DataSource)")
    public void dsPointCut() {
    }


    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);

        if (StringUtils.isNotNull(dataSource)) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }


        try {
            return point.proceed();

        } finally {

            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }


    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DataSource dataSource = (DataSource) AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
        if (Objects.nonNull(dataSource)) {
            return dataSource;
        }

        return (DataSource) AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
    }
}
