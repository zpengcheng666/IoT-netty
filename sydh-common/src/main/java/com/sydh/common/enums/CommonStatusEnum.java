package com.sydh.common.enums;

import com.sydh.common.core.text.IntArrayValuable;

import java.util.Arrays;


public enum CommonStatusEnum
        implements IntArrayValuable {
    ENABLE(Integer.valueOf(0), "开启"),
    DISABLE(Integer.valueOf(1), "关闭");

    CommonStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public static final int[] ARRAYS;

    static {
        ARRAYS = Arrays.<CommonStatusEnum>stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray();
    }

    private final Integer status;
    private final String name;

    public Integer getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
