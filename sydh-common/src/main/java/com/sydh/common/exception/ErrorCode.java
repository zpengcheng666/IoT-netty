package com.sydh.common.exception;

import java.util.Objects;

/**
 * 错误码对象
 * 
 * 全局错误码，占用 [0, 999], 参见 GlobalErrorCodeConstants
 * 业务异常错误码，占用 [1 000 000 000, +∞)，按照以下规则分配：
 * 
 * 1. 【产品线】：占用 [1 000 000 000, 1 999 999 999]
 * 2. 【其他】：如果有其他错误码需求，可以分配新的段
 */
public class ErrorCode {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorCode errorCode = (ErrorCode) o;
        return Objects.equals(code, errorCode.code) &&
               Objects.equals(msg, errorCode.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg);
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

