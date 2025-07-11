package com.sydh.framework.web.exception;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.exception.DemoModeException;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler({AccessDeniedException.class})
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(403, "没有权限，请联系管理员授权");
    }


    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return AjaxResult.error(e.getMessage());
    }


    @ExceptionHandler({ServiceException.class})
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error(e.getMessage(), (Throwable) e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code.intValue(), e.getMessage()) : AjaxResult.error(e.getMessage());
    }


    @ExceptionHandler({RuntimeException.class})
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }


    @ExceptionHandler({Exception.class})
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }


    @ExceptionHandler({BindException.class})
    public AjaxResult handleBindException(BindException e) {
        log.error(e.getMessage(), (Throwable) e);
        String message = ((ObjectError) e.getAllErrors().get(0)).getDefaultMessage();
        return AjaxResult.error(message);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), (Throwable) e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }


    @ExceptionHandler({DemoModeException.class})
    public AjaxResult handleDemoModeException(DemoModeException e) {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
