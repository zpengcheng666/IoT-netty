package com.sydh.common.enums;

/**
 * 功能回复状态枚举
 */
public enum FunctionReplyStatus {
    
    SUCCESS(200, "设备执行成功"),
    FAIL(201, "指令执行失败"),
    UNKNOWN(204, "设备超时未回复"),
    NO_REPLY(203, "指令下发成功");

    private final int code;
    private final String message;

    FunctionReplyStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    /**
     * 根据代码获取消息
     *
     * @param code 状态代码
     * @return 对应的消息，如果找不到则返回空字符串
     */
    public static String getMessage(int code) {
        for (FunctionReplyStatus status : values()) {
            if (status.getCode() == code) {
                return status.getMessage();
            }
        }
        return "";
    }
}
