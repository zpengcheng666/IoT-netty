package com.sydh.common.enums;

public enum Language {
    ZH_CN("zh-CN"),
    EN("en-US"),
    DEFAULT("default");

    private String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static String matches(String language) {
        if (language.equals("zh")) {
            return ZH_CN.value;
        }
        return EN.value;
    }
}
