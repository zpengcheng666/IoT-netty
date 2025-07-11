package com.sydh.common.enums;

public enum LCType {
    AGENT(Long.valueOf(1L)),
    ENTERPRISE(Long.valueOf(2L)),
    PERSON(Long.valueOf(3L)),
    TRIAL(Long.valueOf(4L));

    private Long type;

    LCType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return this.type;
    }
}
