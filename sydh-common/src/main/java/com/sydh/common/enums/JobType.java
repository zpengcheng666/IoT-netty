package com.sydh.common.enums;

import java.util.Objects;

/**
 * 任务类型枚举
 */
public enum JobType {

    DEVICE(1),
    DEVICE_ALERT(2),
    SCENE(3),
    RULE_ENGINE(4);

    private final Integer value;

    JobType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    /**
     * 根据值获取对应的枚举
     *
     * @param value 值
     * @return 对应的枚举，如果找不到则返回null
     */
    public static JobType fromValue(Integer value) {
        for (JobType jobType : values()) {
            if (Objects.equals(jobType.getValue(), value)) {
                return jobType;
            }
        }
        return null;
    }

    /**
     * 根据值获取枚举名称
     *
     * @param value 值
     * @return 对应的枚举名称，如果找不到则返回null
     */
    public static String getName(Integer value) {
        for (JobType jobType : values()) {
            if (Objects.equals(jobType.getValue(), value)) {
                return jobType.name();
            }
        }
        return null;
    }
}
