package com.sydh.common.enums;

/**
 * 异常代码枚举
 */
public enum ExceptionCode {
    
    SUCCESS(200, "成功"),
    TIMEOUT(400, "超时"),
    OFFLINE(404, "设备断线"),
    FAIL(500, "失败");

    private final int code;
    private final String desc;

    ExceptionCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    /**
     * 根据代码获取描述
     *
     * @param code 异常代码
     * @return 对应的描述，如果找不到则返回空字符串
     */
    public static String getDesc(int code) {
        for (ExceptionCode exceptionCode : values()) {
            if (exceptionCode.getCode() == code) {
                return exceptionCode.getDesc();
            }
        }
        return "";
    }
}
