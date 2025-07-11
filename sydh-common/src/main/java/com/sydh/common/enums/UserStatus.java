package com.sydh.common.enums;


public enum UserStatus {
    OK(Integer.valueOf(0), "正常"), DISABLE(Integer.valueOf(1), "停用"), DELETED(Integer.valueOf(2), "删除");

    private final String info;

    private final Integer code;

    UserStatus(Integer code, String info) {
        this.code = code;
        this.info = info;
    }


    public Integer getCode() {
        return this.code;
    }


    public String getInfo() {
        return this.info;
    }
}
