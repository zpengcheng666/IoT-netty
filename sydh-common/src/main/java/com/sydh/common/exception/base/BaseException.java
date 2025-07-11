package com.sydh.common.exception.base;

import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;


public class BaseException extends RuntimeException {
    private static final long G = 1L;
    private String H;
    private String code;
    private Object[] I;
    private String J;

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
        this.H = module;
        this.code = code;
        this.I = args;
        this.J = defaultMessage;
    }


    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, null);
    }


    public BaseException(String module, String defaultMessage) {
        this(module, null, null, defaultMessage);
    }


    public BaseException(String code, Object[] args) {
        this(null, code, args, null);
    }


    public BaseException(String defaultMessage) {
        this(null, null, null, defaultMessage);
    }


    @Override
    public String getMessage() {
        String str = null;
        if (!StringUtils.isEmpty(this.code)) {
            str = MessageUtils.message(this.code, this.I);
        }
        if (str == null) {
            str = this.J;
        }
        return str;
    }


    public String getModule() {
        return this.H;
    }


    public String getCode() {
        return this.code;
    }


    public Object[] getArgs() {
        return this.I;
    }


    public String getDefaultMessage() {
        return this.J;
    }
}
