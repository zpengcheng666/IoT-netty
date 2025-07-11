package com.sydh.common.exception;

/**
 * 全局异常
 * <p>
 * 用于处理全局性的业务异常
 */
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public GlobalException() {
    }

    public GlobalException(String message) {
        this.message = message;
    }

    public GlobalException(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public String getDetailMessage() {
        return this.detailMessage;
    }

    public GlobalException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public GlobalException setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "GlobalException{" +
                "message='" + message + '\'' +
                ", detailMessage='" + detailMessage + '\'' +
                '}';
    }
}
