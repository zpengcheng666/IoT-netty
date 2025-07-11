package com.sydh.common.enums;


public enum StatusEnum {
    SUCCESS(Integer.valueOf(1), "成功"),
    FAIL(Integer.valueOf(0), "失败");

    StatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return this.status;
    }

    private final Integer status;
    private final String name;

    public String getName() {
        return this.name;
    }
}
